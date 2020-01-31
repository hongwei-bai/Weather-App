package au.com.test.weather_app.data.source.remote.owm

import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse
import au.com.test.weather_app.data.source.remote.owm.services.WeatherService
import au.com.test.weather_app.data.source.remote.owm.helper.RequestHelper.buildQueryCompositeParameter
import io.reactivex.Observable
import javax.inject.Inject

open class OpenWeatherMapDataSource @Inject constructor(
    private val weatherService: WeatherService
) {
    fun getWeatherByCityName(cityName: String, countryCode: String? = null): Observable<WeatherRepsonse> {
        return weatherService.getWeatherByCityName(buildQueryCompositeParameter(cityName, countryCode))
    }

    fun getWeatherById(cityId: Long): Observable<WeatherRepsonse> {
        return weatherService.getWeatherById(cityId)
    }

    fun getWeatherByCoordinate(lat: Double, lon: Double): Observable<WeatherRepsonse> {
        return weatherService.getWeatherByCoordinate(lat, lon)
    }

    fun getWeatherByZipCode(zipCode: Long, countryCode: String? = null): Observable<WeatherRepsonse> {
        return weatherService.getWeatherByCityName(buildQueryCompositeParameter(zipCode.toString(), countryCode))
    }
}