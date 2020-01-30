package au.com.test.weather_app.data

import au.com.test.weather_app.data.source.cache.Cache
import au.com.test.weather_app.data.source.remote.owm.OpenWeatherMapDataSource
import au.com.test.weather_app.data.source.remote.owm.model.WeatherRepsonse
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherManager @Inject constructor(
    private val openWeatherMapDataSource: OpenWeatherMapDataSource,
    private val cache: Cache
) :
    WeatherRepository {

    override fun fetchWeatherData(cityName: String): Observable<WeatherRepsonse> {
        return openWeatherMapDataSource.fetchWeather(cityName)
    }
}