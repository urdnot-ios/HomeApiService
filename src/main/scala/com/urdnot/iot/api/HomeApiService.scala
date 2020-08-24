package com.urdnot.iot.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import com.urdnot.iot.api.messages.DataObjects

import scala.concurrent.ExecutionContextExecutor

object HomeApiService extends DataObjects {
  val config = ConfigFactory.load()
  val log: Logger = Logger("homeApiService")

  implicit val system: ActorSystem = ActorSystem("home-api-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val kitchenLightUri: String = config.getString("akka.uri.kitchenLight")
  implicit val kitchenDoorUri: String = config.getString("akka.uri.kitchenDoor")
  implicit val lightChangeRoute: String = config.getString("akka.route.lightChange")
  implicit val lightStatusRoute: String = config.getString("akka.route.lightStatus")

  implicit val garageDoorUri: String = config.getString("akka.uri.garageDoor")
  implicit val doorStatusRoute: String = config.getString("akka.route.doorStatus")


  def main(args: Array[String]): Unit = {
    val route = HomeApiRoutes.setupRoutes()

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
}
