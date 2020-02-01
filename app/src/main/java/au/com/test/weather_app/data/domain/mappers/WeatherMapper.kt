package au.com.test.weather_app.data.domain.mappers

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse

object WeatherMapper {
    fun mapToDomainEntities(response: WeatherRepsonse): WeatherData? =
        if (response.isSuccess()) {
            WeatherData(
                id = response.weather[0].id,
                main = response.weather[0].main,
                description = response.weather[0].description,
                icon = response.weather[0].icon,
                temperature = response.main.temp,
                temperatureMin = response.main.temp_min,
                temperatureMax = response.main.temp_max,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                windDegree = response.wind.deg
            )
        } else {
            null
        }
}