package com.urdnot.iot.api.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.urdnot.iot.api.actors.Doors.{DoorStatus, DoorStatusResponse}
import com.urdnot.iot.api.actors.QueryOpenWeather.{Clouds, Coord, CurrentWeatherData, MainWeather, OpenWeatherResponse, Rain, SysWeather, WeatherItems, WeatherPressureReply, WeatherPressureRequest, Wind}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

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

  implicit val weatherPressureRequestFormat: RootJsonFormat[WeatherPressureRequest] = jsonFormat1(WeatherPressureRequest)
  implicit val weatherPressureReplyFormat: RootJsonFormat[WeatherPressureReply] = jsonFormat1(WeatherPressureReply)


  implicit val DoorStatusFormat: RootJsonFormat[DoorStatus] = jsonFormat1(DoorStatus)
  implicit val DoorStatusReply: RootJsonFormat[DoorStatusResponse] = jsonFormat1(DoorStatusResponse)
}