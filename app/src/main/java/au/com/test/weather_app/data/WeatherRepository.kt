package au.com.test.weather_app.data

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse
import io.reactivex.Observable


interface WeatherRepository {
    fun getCityList(): Observable<List<City>>

    fun queryWeatherData(cityName: String, countryCode: String? = null): Observable<WeatherData>

    fun queryWeatherById(cityId: Long): Observable<WeatherRepsonse>

    fun queryWeatherByCoordinate(lat: Double, lon: Double): Observable<WeatherRepsonse>

    fun queryWeatherByZipCode(zipCode: Long, countryCode: String? = null): Observable<WeatherRepsonse>
}