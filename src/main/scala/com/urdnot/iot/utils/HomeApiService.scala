package com.urdnot.iot.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import com.urdnot.iot.utils.actors.QueryOpenWeather
import com.urdnot.iot.utils.actors.QueryOpenWeather.{CurrentWeatherData, OpenWeatherResponse}
import com.urdnot.iot.utils.messages.{DataObjects, DoorStatus, LightSwitch, LightSwitchStatus}
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object HomeApiService extends DataObjects {
  val config = ConfigFactory.load()
  val log: Logger = Logger("homeApiService")

  implicit val system: ActorSystem = ActorSystem("home-api-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val kitchenLightUri: String = config.getString("akka.uri.kitchenLight")
  implicit val kitchenDoorUri: String = config.getString("akka.uri.kitchenDoor")
  implicit val lightChangeRoute: String = config.getString("akka.route.lightChange")
  implicit val lightStatusRoute: String = config.getString("akka.route.lightStatus")

  implicit val garageDoorUri: String = config.getString("akka.uri.garageDoor")
  implicit val doorStatusRoute: String = config.getString("akka.route.doorStatus")


  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        //        post {
        //          // curl -X POST -G 'http://localhost:8081/kitchenDoor'
        //          path("kitchenDoor") {
        //            onComplete(getDoorStatus()) {
        //              case Success(res) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Await.result(manageResponse(res, "door"), 1.seconds)))
        //              case Failure(_) => complete("something went wrong")
        //            }
        //          }
        //        },
        post {
          // curl -X POST -G 'http://localhost:8081/garage1'
          path("garage1") {
            onComplete(getGarageDoorStatus("inner")) {
              case Success(res) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Await.result(manageResponse(res, "door"), 1.seconds)))
              case Failure(_) => complete("something went wrong")
            }
          }
        },
        post {
          // curl -X POST -G 'http://localhost:8081/garage2'
          path("garage2") {
            onComplete(getGarageDoorStatus("outer")) {
              case Success(res) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Await.result(manageResponse(res, "door"), 1.seconds)))
              case Failure(_) => complete("something went wrong")
            }
          }
        },
        //        post {
        //          // curl -X POST 'http://localhost:8081/kitchenLight?setStatus=off'
        //          // curl -X POST 'http://localhost:8081/kitchenLight?setStatus=on'
        //          path("kitchenLight") {
        //            parameters("setStatus") { setStatus =>
        //              onComplete(changeLight(setStatus)) {
        //                case Success(res) => complete(res)
        //                case Failure(_) => complete("request failed: ")
        //              }
        //            }
        //          }
        //        },
        //        get {
        //          // curl -G 'http://localhost:8081/kitchenLightStatus'
        //          path("kitchenLightStatus") {
        //            onComplete(getKitchenLightStatus()) {
        //              case Success(res) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Await.result(manageResponse(res, "light"), 1.seconds)))
        //              case Failure(_) => complete("request failed: ")
        //            }
        //          }
        //        },
        get {
          // curl -G 'http://localhost:8081/seaLevelPressure'
          path("seaLevelPressure") {
            onComplete(getCurrentSeaLevelPressure()) {
              case Success(res: HttpResponse) => complete (
                HttpEntity(ContentTypes.`text/html(UTF-8)`, Await.result(manageResponse(res, "seaLevelPressure"), 1.seconds))
              )
              case Failure(_) => complete("1024")
            }
          }
        },
        get {
          implicit val timeout: Timeout = 5.seconds
          // curl -d '{ "requestType": "currentWeatherData" }' -H "Content-Type: application/json" -X GET http://localhost:8081/currentOpenWeather
          path("currentOpenWeather") {
            entity(as[CurrentWeatherData]) { weatherRequest: CurrentWeatherData =>
              onComplete(QueryOpenWeather.getCurrentOpenWeather()) {
                case Success(res: HttpResponse) =>
                  // convert their response into my data structure
                  complete(
                    Unmarshal(res.entity).to[OpenWeatherResponse]
                  )
                case Failure(_) => complete("""{"error": "unable to get current weather"}""")
              }
            }
          }
        }
      )
    val bindingFuture = Http().bindAndHandle(
      route,
      config.getString("akka.server.server"),
      config.getInt("akka.server.port")
    )
    println(s"Server online at ${config.getString("akka.server.server")}:${config.getInt("akka.server.port")}")
    try {
      //    Here we start the HTTP server and log the info
      bindingFuture.map { serverBinding ⇒
        log.info(s"RestApi bound to ${serverBinding.localAddress}")
      }
    } catch {
      //    If the HTTP server fails to start, we throw an Exception and log the error and close the system
      case ex: Exception ⇒
        log.error(ex + s" Failed to bind to ${config.getString("akka.server.server")}:${config.getInt("akka.server.port")}!")
        //      System shutdown
        system.terminate()
    }
  }

  def getKitchenLightStatus(): Future[HttpResponse] = {
    Http().singleRequest(HttpRequest(
      method = HttpMethods.GET,
      uri = s"$kitchenLightUri$lightStatusRoute"
    ))
  }

  def changeLight(setStatus: String): Future[HttpResponse] = {
    Marshal(LightSwitch(setStatus)).to[RequestEntity].flatMap { entity =>
      val req = HttpRequest(
        method = HttpMethods.POST,
        uri = s"$kitchenLightUri$lightChangeRoute",
        entity = entity
      )
      println(req)
      Http(system).singleRequest(req)
    }
  }

  def getCurrentSeaLevelPressure(): Future[HttpResponse] = {
    val request: HttpRequest = HttpRequest(
      method = HttpMethods.GET,
      //uri = Uri("https://api.openweathermap.org/data/2.5/weather")
      // https://api.openweathermap.org/data/2.5/weather?lat=47&lon=-122&APPID=b9f91a83c2b3569c285d01650bdc9f49
      uri = Uri(config.getString("akka.uri.openWeather"))
        .withQuery(Query(
          "lat" -> config.getString("akka.OpenWeather.lat"),
          "lon" -> config.getString("akka.OpenWeather.lon"),
          "APPID" -> config.getString("akka.OpenWeather.APPID"))
        ),
      headers = Nil,
      protocol = HttpProtocols.`HTTP/1.1`
    )
    Http().singleRequest(request)
  }



  def manageResponse(result: HttpResponse, source: String): Future[String] = {
    source match {
      case "seaLevelPressure" => result.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        log.info(body.utf8String)
        body.utf8String.parseJson.convertTo[OpenWeatherResponse].main.pressure.toString
      }
      case "currentOpenWeather" => result.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        log.info(body.utf8String)
        body.utf8String.parseJson.convertTo[OpenWeatherResponse].toString
      }
      case "light" => result.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        body.utf8String.parseJson.convertTo[LightSwitchStatus].status
      }
      case "door" => result.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
        body.utf8String.parseJson.convertTo[DoorStatus].status
      }
      case _ => Future("Unknown response: " + result)
    }
  }

  def getDoorStatus(): Future[HttpResponse] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$kitchenDoorUri$doorStatusRoute"))
    responseFuture
  }

  def getGarageDoorStatus(door: String): Future[HttpResponse] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$garageDoorUri$doorStatusRoute?door=$door"))
    responseFuture
  }
}
