package au.com.test.weather_app.data.source.remote.owm.models

import au.com.test.weather_app.data.source.remote.owm.models.WeatherResponseCode.SUCCESS

data class WeatherRepsonse(
    val coord: Coordinate,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val cloud: Cloud,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
) {
    fun isSuccess(): Boolean =
        cod == SUCCESS && weather.isNotEmpty()
}

data class Coordinate(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Long,
    val humidity: Int
)

data class Wind(
    val speed: Float,
    val deg: Int
)

data class Cloud(
    val all: Int
)

data class Sys(
    val type: Int,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)