package au.com.test.weather_app.test.factory

import au.com.test.weather_app.data.source.remote.owm.models.Cloud
import au.com.test.weather_app.data.source.remote.owm.models.Coordinate
import au.com.test.weather_app.data.source.remote.owm.models.Main
import au.com.test.weather_app.data.source.remote.owm.models.Sys
import au.com.test.weather_app.data.source.remote.owm.models.Weather
import au.com.test.weather_app.data.source.remote.owm.models.WeatherResponse
import au.com.test.weather_app.data.source.remote.owm.models.Wind
import au.com.test.weather_app.test.factory.TestCity.SomewhereInAntarctica
import au.com.test.weather_app.test.factory.TestCity.SurryHills
import au.com.test.weather_app.test.factory.TestCity.Sydney
import au.com.test.weather_app.test.factory.TestDate.Now
import au.com.test.weather_app.test.factory.TestDate.WeekAgo
import au.com.test.weather_app.test.factory.TestWeatherCondition.Cleared
import au.com.test.weather_app.test.factory.TestWeatherCondition.Rain
import au.com.test.weather_app.test.factory.TestWeatherCondition.Snow

object ApiWeatherResponseFactory {
    fun createWeatherResponseSydneyCleared() = createWeatherResponse()
    fun createWeatherResponseSydneyRain() = createWeatherResponse(weather = Rain)
    fun createWeatherResponseSydneySnow() = createWeatherResponse(weather = Snow)

    fun createWeatherResponseSydneyClearedLastWeek() = createWeatherResponse(date = WeekAgo)
    fun createWeatherResponseSydneyRainLastWeek() = createWeatherResponse(weather = Rain, date = WeekAgo)
    fun createWeatherResponseSydneySnowLastWeek() = createWeatherResponse(weather = Snow, date = WeekAgo)

    fun createWeatherResponseNowhereCleared() = createWeatherResponse(city = SomewhereInAntarctica)
    fun createWeatherResponseNowhereRain() = createWeatherResponse(city = SomewhereInAntarctica, weather = Rain)
    fun createWeatherResponseNowhereSnow() = createWeatherResponse(city = SomewhereInAntarctica, weather = Snow)

    fun createWeatherResponseNowhereClearedLastWeek() = createWeatherResponse(city = SomewhereInAntarctica, date = WeekAgo)
    fun createWeatherResponseNowhereRainLastWeek() = createWeatherResponse(city = SomewhereInAntarctica, weather = Rain, date = WeekAgo)
    fun createWeatherResponseNowhereSnowLastWeek() = createWeatherResponse(city = SomewhereInAntarctica, weather = Snow, date = WeekAgo)

    fun createWeatherResponseSurryHillsCleared() = createWeatherResponse(city = SurryHills)
    fun createWeatherResponseSurryHillsRain() = createWeatherResponse(city = SurryHills, weather = Rain)
    fun createWeatherResponseSurryHillsSnow() = createWeatherResponse(city = SurryHills, weather = Snow)

    fun createWeatherResponseSurryHillsClearedLastWeek() = createWeatherResponse(city = SurryHills, date = WeekAgo)
    fun createWeatherResponseSurryHillsRainLastWeek() = createWeatherResponse(city = SurryHills, weather = Rain, date = WeekAgo)
    fun createWeatherResponseSurryHillsSnowLastWeek() = createWeatherResponse(city = SurryHills, weather = Snow, date = WeekAgo)

    fun createWeatherResponse(city: TestCity = Sydney, weather: TestWeatherCondition = Cleared, date: TestDate = Now): WeatherResponse = WeatherResponse(
        coord = Coordinate(lon = city.lon, lat = city.lat),
        weather = listOf(
            Weather(
                id = 500,
                main = weather.main,
                description = weather.desc,
                icon = "10n"
            )
        ),
        base = "stations",
        main = Main(
            temp = 295.61f,
            feels_like = 295.61f,
            temp_min = 294.82f,
            temp_max = 296.48f,
            pressure = 1003,
            humidity = 94
        ),
        visibility = 10000,
        wind = Wind(speed = 5.1f, deg = 200),
        cloud = Cloud(all = 75),
        dt = date.time,
        sys = Sys(
            type = 1,
            id = 9600,
            country = city.countryCode!!,
            sunrise = 1580671116,
            sunset = 1580720420
        ),
        timezone = 39600,
        id = city.cityId!!,
        name = city.name,
        cod = 200
    )
}