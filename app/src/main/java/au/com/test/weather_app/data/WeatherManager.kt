package au.com.test.weather_app.data

import au.com.test.weather_app.data.source.cache.Cache
import au.com.test.weather_app.data.source.local.owm.LocalOpenWeatherMapDataSource
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.data.source.remote.owm.OpenWeatherMapDataSource
import au.com.test.weather_app.data.source.remote.owm.models.WeatherRepsonse
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherManager @Inject constructor(
    private val localOpenWeatherMapDataSource: LocalOpenWeatherMapDataSource,
    private val openWeatherMapDataSource: OpenWeatherMapDataSource,
    private val cache: Cache
) :
    WeatherRepository {
    override fun getCityList(): Observable<List<City>> =
        localOpenWeatherMapDataSource.getCityList()

    override fun queryWeatherData(cityName: String, countryCode: String?): Observable<WeatherRepsonse> =
        openWeatherMapDataSource.getWeatherByCityName(cityName, countryCode)

    override fun queryWeatherById(cityId: Long): Observable<WeatherRepsonse> =
        openWeatherMapDataSource.getWeatherById(cityId)

    override fun queryWeatherByCoordinate(lat: Double, lon: Double): Observable<WeatherRepsonse> =
        openWeatherMapDataSource.getWeatherByCoordinate(lat, lon)

    override fun queryWeatherByZipCode(zipCode: Long, countryCode: String?): Observable<WeatherRepsonse> =
        openWeatherMapDataSource.getWeatherByZipCode(zipCode, countryCode)
}