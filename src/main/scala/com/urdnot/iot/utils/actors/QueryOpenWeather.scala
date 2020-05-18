package com.urdnot.iot.utils.actors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import com.urdnot.iot.utils.HomeApiService.config

import scala.concurrent.{ExecutionContextExecutor, Future}

object QueryOpenWeather {
  implicit val system: ActorSystem = ActorSystem("home-api-service")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  case class CurrentSeaLevelPressure()
  // { "request_type": "currentPressure" }

  case class CurrentWeatherData(requestType: String)
  // { "request_type": "currentWeatherData" }

  final case class Coord(lon: Int, lat: Int)

  // "coord": {"lon":-122,"lat":47}

  final case class WeatherItems(id: Int, main: String, description: String, icon: String)

  //  "weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}]

  final case class MainWeather(temp: Double, feels_like: Double, temp_min: Double, temp_max: Double, pressure: Int, humidity: Int)
//    (temp* − 273.15) × 1.8 + 32 = 63.356°F
  //  "main":
  //    {"temp":285.36,"feels_like":283.32,"temp_min":284.26,"temp_max":285.93,"pressure":1018,"humidity":78}

  final case class Wind(speed: Double, deg: Option[Int])

  //  "wind":
  //    {"speed":2.42,"deg":119}

  final case class Rain(`1h`: Double)

  // "rain":{"1h":0.13}

  final case class Clouds(all: Int)

  //  "clouds":
  //    {"all":80}

  final case class SysWeather(`type`: Int, id: Int, country: String, sunrise: Long, sunset: Long)

  //  "sys":
  //    {"type":3,"id":2032880,"country":"US","sunrise":1589545916,"sunset":1589600219}

  final case class OpenWeatherResponse(
                                        coord: Coord,
                                        weather: List[WeatherItems],
                                        base: String,
                                        main: MainWeather,
                                        wind: Wind,
                                        rain: Option[Rain],
                                        clouds: Clouds,
                                        dt: Long,
                                        sys: SysWeather,
                                        timezone: Int,
                                        id: Long,
                                        name: String,
                                        cod: Int
                                      )

  /*
  need to unmarshall objects, but not primitives. So "base" is String, not a custom object
  However a list, like "weather" is a list of the unmarshalled object
  {
  "coord":{"lon":-122,"lat":47},
  "weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],
  "base":"stations",
  "main":{"temp":286.3,"feels_like":285.87,"temp_min":285.93,"temp_max":287.04,"pressure":1010,"humidity":96},
  "wind":{"speed":1.73,"deg":123},
  "clouds":{"all":100},
  "dt":1589648740,
  "sys":{"type":3,"id":2001439,"country":"US","sunrise":1589632245,"sunset":1589686694},
  "timezone":-25200,
  "id":5806769,
  "name":"Pierce",
  "cod":200
  }
   */
  def getCurrentOpenWeather(): Future[HttpResponse] = {
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        //uri = Uri("https://api.openweathermap.org/data/2.5/weather")
        // https://api.openweathermap.org/data/2.5/weather?lat=47&lon=-122&APPID=b9f91a83c2b3569c285d01650bdc9f49
        uri = Uri(config.getString("akka.uri.openWeather"))
          .withQuery(
            Query(
              "lat" -> config.getString("akka.OpenWeather.lat"),
              "lon" -> config.getString("akka.OpenWeather.lon"),
              "APPID" -> config.getString("akka.OpenWeather.APPID")
            )
          ),
        headers = Nil,
        protocol = HttpProtocols.`HTTP/1.1`
      )
    )
  }
}

//class QueryOpenWeather extends Actor with ActorLogging with DataObjects {
//
//  implicit val system = ActorSystem()
//  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//
//  def receive: PartialFunction[Any, Unit] = {
//    case CurrentSeaLevelPressure => log.info("reqeust for pressure")
//      sender() ! queryOpenWeather()
//    case _ => log.info("Unknown request")
//      sender() ! "Unknown reqeust"
//  }
//  def queryOpenWeather(): HttpRequest = {
//    //uri = Uri("https://api.openweathermap.org/data/2.5/weather")
//    //https://api.openweathermap.org/data/2.5/weather?lat=47&lon=-122&APPID=b9f91a83c2b3569c285d01650bdc9f49
//    val method: HttpMethod = HttpMethods.GET
//    val uri: Uri = Uri(config.getString("akka.uri.openWeather"))
//      .withQuery(
//        Query(
//          "lat" -> config.getString("akka.OpenWeather.lat"),
//          "lon" -> config.getString("akka.OpenWeather.lon"),
//          "APPID" -> config.getString("akka.OpenWeather.APPID")
//        )
//      )
//    HttpRequest(
//      method = method,
//      uri = uri,
//      entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, ""))
//
//  }
//}
