package com.urdnot.iot.utils.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.urdnot.iot.utils.actors.QueryOpenWeather._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class PressureReading(dateTimeStamp: Long, locationId: Long, hga: Int)

// domain model
final case class Item(name: String, id: Long)




final case class LightSwitch(switch: String)

final case class LightSwitchStatus(status: String)

final case class DoorStatus(status: String)

final case class WeatherPressureReply(pa: Int)

trait DataObjects extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val currentWeatherRequestFormat:RootJsonFormat[CurrentWeatherData] = jsonFormat1(CurrentWeatherData)
  implicit val coordFormat: RootJsonFormat[Coord] = jsonFormat2(Coord)
  implicit val weatherItemsFormat: RootJsonFormat[WeatherItems] = jsonFormat4(WeatherItems)
  implicit val mainWeatherFormat: RootJsonFormat[MainWeather] = jsonFormat6(MainWeather)
  implicit val windFormat: RootJsonFormat[Wind] = jsonFormat2(Wind)
  implicit val rainFormat: RootJsonFormat[Rain] = jsonFormat1(Rain)
  implicit val cloudFormat: RootJsonFormat[Clouds] = jsonFormat1(Clouds)
  implicit val sysWeatherFormat: RootJsonFormat[SysWeather] = jsonFormat5(SysWeather)
  implicit val openWeatherFormat: RootJsonFormat[OpenWeatherResponse] = jsonFormat13(OpenWeatherResponse)

  implicit val LightSwitchFormat: RootJsonFormat[LightSwitch] = jsonFormat1(LightSwitch)
  implicit val LightSwitchStatusFormat: RootJsonFormat[LightSwitchStatus] = jsonFormat1(LightSwitchStatus)
  implicit val DoorStatusFormat: RootJsonFormat[DoorStatus] = jsonFormat1(DoorStatus)
  implicit val weatherPressureFormat: RootJsonFormat[WeatherPressureReply] = jsonFormat1(WeatherPressureReply)
}