package com.urdnot.iot.api

import com.urdnot.iot.api.actors.QueryOpenWeather._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class UtilsSpec extends Matchers with AnyWordSpecLike{
  val openWeatherResponseKelvin: OpenWeatherResponse = OpenWeatherResponse(
//     "coord":{"lat":47,"lon":-122},
    coord = Coord(lon = 47, lat = -122),
//     "weather":[{"description":"light rain","icon":"10d","id":500,"main":"Rain"}],
    weather = List(WeatherItems(id = 500, main = "Rain", description = "light rain", icon = "10d")),
//     "base":"stations",
    base = "stations",
//     "main":{"feels_like":287.92,"humidity":94,"pressure":1019,"temp":287.77,"temp_max":288.15,"temp_min":287.59},
    main = MainWeather(temp = 287.77, feels_like = 287.92, temp_min = 287.59, temp_max = 288.15, pressure = 1019, humidity = 94),
//     "wind":{"deg":223,"speed":1.43}
    wind = Wind(speed = Option(2.43), deg = Option(223)),
//     "rain":{"1h":0.69},
    rain = Option(Rain(`1h` = 0.69)),
//     "clouds":{"all":100},
    clouds = Clouds(all = 100),
//     "dt":1590437820,
    dt = 1590437820,
//     "sys":{"country":"US","id":2032880,"sunrise":1590409301,"sunset":1590464920,"type":3},
    sys = SysWeather(`type` = 3, id = 2032880, country = "US", sunrise = 1590409301, sunset = 1590464920),
//     "timezone":-25200,
    timezone = -25200,
//     "id":5806769,
    id = 5806769,
//    "name":"Pierce",
    name = "Pierce",
//    {"cod":200,}
    cod = 200)
  "kelvinToF" must {
    "accurately convert Kelvin temperatures to Fahrenheit" in {
      assert(openWeatherResponseKelvin.main.temp === 58.316)
    }
  }
}
