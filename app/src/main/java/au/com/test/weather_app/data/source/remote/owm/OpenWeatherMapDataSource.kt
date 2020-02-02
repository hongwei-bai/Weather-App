package au.com.test.weather_app.data.source.remote.owm

import au.com.test.weather_app.data.source.remote.owm.helper.RequestHelper.buildQueryCompositeParameter
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse
import au.com.test.weather_app.data.source.remote.owm.services.WeatherService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class OpenWeatherMapDataSource @Inject constructor(
    private val weatherService: WeatherService
) {
    suspend fun getWeatherByCityName(cityName: String, countryCode: String? = null): WeatherRepsonse =
        weatherService.getWeatherByCityName(buildQueryCompositeParameter(cityName, countryCode))

    suspend fun getWeatherById(cityId: Long): WeatherRepsonse = weatherService.getWeatherById(cityId)

    suspend fun getWeatherByCoordinate(lat: Double, lon: Double): WeatherRepsonse = weatherService.getWeatherByCoordinate(lat, lon)

    suspend fun getWeatherByZipCode(zipCode: Long, countryCode: String? = null): WeatherRepsonse =
        weatherService.getWeatherByZipCode(buildQueryCompositeParameter(zipCode.toString(), countryCode))
}