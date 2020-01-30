package au.com.test.weather_app.data.source.remote.owm

import au.com.test.weather_app.data.source.remote.owm.model.WeatherRepsonse
import au.com.test.weather_app.data.source.remote.owm.services.WeatherService
import io.reactivex.Observable
import javax.inject.Inject

open class OpenWeatherMapDataSource @Inject constructor(
    private val weatherService: WeatherService
) {
    fun fetchWeather(cityName: String): Observable<WeatherRepsonse> {
        return weatherService.getWeather(cityName)
    }
}