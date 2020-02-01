package au.com.test.weather_app.data

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.domain.mappers.WeatherMapper
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

    override fun queryWeatherByCityName(
        cityName: String,
        countryCode: String?
    ): Observable<WeatherData> =
        openWeatherMapDataSource.getWeatherByCityName(cityName, countryCode).map {
            WeatherMapper.mapToDomainEntities(it)
        }

    override fun queryWeatherById(cityId: Long): Observable<WeatherData> =
        openWeatherMapDataSource.getWeatherById(cityId).map {
            WeatherMapper.mapToDomainEntities(it)
        }

    override fun queryWeatherByCoordinate(lat: Double, lon: Double): Observable<WeatherData> =
        openWeatherMapDataSource.getWeatherByCoordinate(lat, lon).map {
            WeatherMapper.mapToDomainEntities(it)
        }

    override fun queryWeatherByZipCode(
        zipCode: Long,
        countryCode: String?
    ): Observable<WeatherData> =
        openWeatherMapDataSource.getWeatherByZipCode(zipCode, countryCode).map {
            WeatherMapper.mapToDomainEntities(it)
        }
}