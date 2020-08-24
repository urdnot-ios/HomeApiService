package com.urdnot.iot.api

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.urdnot.iot.api.actors.QueryOpenWeather.WeatherPressureReply
import com.urdnot.iot.api.messages.DataObjects
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class RouteSpec  extends AnyWordSpec with Matchers with ScalatestRouteTest with DataObjects {
  lazy val route = HomeApiRoutes.setupRoutes()
  // curl -d '{ "request": "pa" }' -H "Content-Type: application/json" -X GET http://localhost:8081/iot/v1/seaLevelPressure
  "The route service" should {
    "return a JSON reply for Post requests to the deploy route" in {
      // tests:
      val request = Get("/iot/v1/seaLevelPressure", HttpEntity(`application/json`, """{ "request": "pa" }"""))
      request ~> route ~> check {
        responseAs[WeatherPressureReply] shouldEqual WeatherPressureReply(pa=1016)
      }
      request ~> route ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
      }
    }
  }
}
