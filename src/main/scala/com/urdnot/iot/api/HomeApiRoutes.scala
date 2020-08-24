package com.urdnot.iot.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, onComplete, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.{ByteString, Timeout}
import com.typesafe.scalalogging.Logger
import com.urdnot.iot.api.actors.Doors.{DoorStatus, DoorStatusResponse, getGarageDoorStatus}
import com.urdnot.iot.api.actors.QueryOpenWeather.{CurrentWeatherData, OpenWeatherResponse, WeatherPressureReply, WeatherPressureRequest, getCurrentOpenWeather}
import com.urdnot.iot.api.messages.DataObjects
import spray.json._
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object HomeApiRoutes extends DataObjects {
  val service = "iot"
  val version = "v1"
  implicit val system: ActorSystem = ActorSystem("home-api-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val log: Logger = Logger("homeApiRoutes")

  def setupRoutes(): Route = {
    pathPrefix(service / version) {
      concat(
        path("seaLevelPressure") {
          get {
            implicit val timeout: Timeout = 5.seconds
//            curl -d '{ "request": "pa" }' -H "Content-Type: application/json" -X GET http://localhost:8081/iot/v1/seaLevelPressure
            entity(as[WeatherPressureRequest]) { _ =>
              log.info("seaLevelPressure request")
              onComplete(getCurrentOpenWeather()) {
                case Success(res: HttpResponse) =>
                  complete(
                    res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body: ByteString =>
                      WeatherPressureReply(body.utf8String.parseJson.convertTo[OpenWeatherResponse].main.pressure) match {
                        case re: WeatherPressureReply => re
                        case _ => log.error("Unable to parse OpenWeatherResponse, sending default value")
                          WeatherPressureReply(pa = 1024)
                      }
                    }
                  )
                case Failure(_) => log.error("Bad reply from OpenWeather, sending default value")
                  complete(WeatherPressureReply(pa = 1024))
              }
            }
          }
        },
        path("currentOpenWeather") {
          get {
            implicit val timeout: Timeout = 5.seconds
//            curl -d '{ "requestType": "currentWeatherData" }' -H "Content-Type: application/json" -X GET http://localhost:8081/iot/v1/currentOpenWeather
            entity(as[CurrentWeatherData]) { _ =>
              onComplete(getCurrentOpenWeather()) {
                case Success(res: HttpResponse) =>
                  // convert their response into my data structure
                  complete(
                    Unmarshal(res.entity).to[OpenWeatherResponse]
                  )
                case Failure(_) => log.error("Bad reply from OpenWeather")
                  complete("""{"error": "unable to get current weather"}""")
              }
            }
          }
        },
        path("garageDoor") {
          get {
            implicit val timeout: Timeout = 5.seconds
//            curl -d '{ "door": "outer" }' -H "Content-Type: application/json" -X GET http://localhost:8081/iot/v1/garageDoor
//            {"status":"closed"}
//            curl -d '{ "door": "inner" }' -H "Content-Type: application/json" -X GET http://localhost:8081/iot/v1/garageDoor
//            {"status":"closed"}
            entity(as[DoorStatus]) { doorStatus: DoorStatus =>
              onComplete(getGarageDoorStatus(doorStatus)){
                case Success(res: HttpResponse) =>
                  // convert their response into my data structure
                  complete(
                    Unmarshal(res.entity).to[DoorStatusResponse]
                  )
                case Failure(_) => log.error("Bad reply from Door")
                  complete("""{"error": "unable to get Door Status"}""")
              }
            }
          }
        }
      )
    }
  }
}
