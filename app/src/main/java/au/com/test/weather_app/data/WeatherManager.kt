package au.com.test.weather_app.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.domain.mappers.WeatherMapper
import au.com.test.weather_app.data.source.cache.Cache
import au.com.test.weather_app.data.source.local.dao.model.WeatherDb
import au.com.test.weather_app.data.source.local.owm.LocalOpenWeatherMapDataSource
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.data.source.remote.owm.OpenWeatherMapDataSource
import au.com.test.weather_app.di.annotations.AppContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherManager @Inject constructor(
    @AppContext private val context: Context,
    private val localOpenWeatherMapDataSource: LocalOpenWeatherMapDataSource,
    private val openWeatherMapDataSource: OpenWeatherMapDataSource,
    private val cache: Cache
) :
    WeatherRepository {
    private val weatherDao = WeatherDb.get(context).weatherDao()

    override fun getCityList(): List<City> =
        localOpenWeatherMapDataSource.getCityList()

    override suspend fun queryWeatherByCityName(
        cityName: String,
        countryCode: String?
    ): WeatherData? = WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByCityName(cityName, countryCode))

    override suspend fun queryWeatherById(cityId: Long): WeatherData? =
        WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherById(cityId))

    override suspend fun queryWeatherByCoordinate(lat: Double, lon: Double): WeatherData? =
        WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByCoordinate(lat, lon))

    override suspend fun queryWeatherByZipCode(
        zipCode: Long,
        countryCode: String?
    ): WeatherData? =
        WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByZipCode(zipCode, countryCode))

    override fun getAllLocationRecordsSortByLatestUpdate(): LiveData<PagedList<WeatherData>> =
        weatherDao.allRecordByLastUpdate().toLiveData(
            Config(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        )

    override fun getLastLocationRecord(): WeatherData? = weatherDao.latestRecord()

    override fun getLocationRecordByCityId(cityId: Long): WeatherData? = weatherDao.recordByCityId(cityId)

    override fun getLocationRecordByLocation(lat: Double, lon: Double): WeatherData? = weatherDao.recordByLocation(lat, lon)

    override fun updateLocationRecord(data: WeatherData) = weatherDao.update(data)

    override fun insertLocationRecord(data: WeatherData) = weatherDao.insert(data)

    override fun deleteLocationRecord(data: WeatherData) = weatherDao.delete(data)

    override fun deleteLocationRecords(list: List<WeatherData>) = weatherDao.delete(list)
}