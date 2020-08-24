package com.urdnot.iot.api.actors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.urdnot.iot.api.HomeApiService.config

import scala.concurrent.{ExecutionContextExecutor, Future}

object Doors {
  implicit val system: ActorSystem = ActorSystem("door-status")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val garageDoorUri: String = config.getString("akka.uri.garageDoor")
  implicit val doorStatusRoute: String = config.getString("akka.route.doorStatus")

  final case class DoorStatus(door: String)
  final case class DoorStatusResponse(status: String)

  def getGarageDoorStatus(door: DoorStatus): Future[HttpResponse] = {
    Http().singleRequest(HttpRequest(uri = s"$garageDoorUri$doorStatusRoute?door=${door.door}"))
  }
}
