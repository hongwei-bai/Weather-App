package au.com.test.weather_app.data.domain.mappers

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse

object WeatherMapper {
    fun mapToDomainEntities(response: WeatherRepsonse): WeatherData {
        return WeatherData(30.0f)
    }
}