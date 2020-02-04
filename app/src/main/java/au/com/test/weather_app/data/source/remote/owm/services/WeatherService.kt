package au.com.test.weather_app.data.source.remote.owm.services

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.remote.owm.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    suspend fun getWeatherByCityName(@Query("q") cityName: String): WeatherResponse

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    suspend fun getWeatherById(@Query("id") id: Long): WeatherResponse

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    suspend fun getWeatherByCoordinate(@Query("lat") lat: Double, @Query("lon") lon: Double): WeatherResponse

    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    suspend fun getWeatherByZipCode(@Query("zip") zipCode: String): WeatherResponse
}