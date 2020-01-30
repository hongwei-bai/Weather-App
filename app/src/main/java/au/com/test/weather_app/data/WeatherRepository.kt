package au.com.test.weather_app.data

import au.com.test.weather_app.data.source.remote.owm.model.WeatherRepsonse
import io.reactivex.Observable


interface WeatherRepository {
    fun fetchWeatherData(cityName: String) : Observable<WeatherRepsonse>
}