package au.com.test.weather_app.data.source.remote.owm.services

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.remote.owm.model.WeatherRepsonse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    //api.openweathermap.org/data/2.5/weather?q={city name}
    //api.openweathermap.org/data/2.5/weather?q={city name},{country code}
    @GET(LocalProperties.Network.API_PATHS.WEATHER)
    fun getWeather(@Path("cityName") cityName: String): Observable<WeatherRepsonse>
}