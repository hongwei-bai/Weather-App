package au.com.test.weather_app.data.source.remote.owm.services

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    fun getWeatherByCityName(@Query("q") cityName: String): Observable<WeatherRepsonse>

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    fun getWeatherById(@Query("id") id: Long): Observable<WeatherRepsonse>

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    fun getWeatherByCoordinate(@Query("lat") lat: Double, @Query("lon") lon: Double): Observable<WeatherRepsonse>

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    fun getWeatherByZipCode(@Query("zip") zipCode: String): Observable<WeatherRepsonse>
}