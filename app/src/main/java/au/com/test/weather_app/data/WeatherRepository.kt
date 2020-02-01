package au.com.test.weather_app.data

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.owm.models.City
import io.reactivex.Observable


interface WeatherRepository {
    fun getCityList(): Observable<List<City>>

    fun queryWeatherByCityName(cityName: String, countryCode: String? = null): Observable<WeatherData>

    fun queryWeatherById(cityId: Long): Observable<WeatherData>

    fun queryWeatherByCoordinate(lat: Double, lon: Double): Observable<WeatherData>

    fun queryWeatherByZipCode(zipCode: Long, countryCode: String? = null): Observable<WeatherData>
}