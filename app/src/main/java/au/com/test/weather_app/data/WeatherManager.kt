package au.com.test.weather_app.data

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import au.com.test.weather_app.LocalProperties.Paging.ENABLE_PLACE_HOLDERS
import au.com.test.weather_app.LocalProperties.Paging.MAX_SIZE
import au.com.test.weather_app.LocalProperties.Paging.PAGE_SIZE
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
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var weatherDao = WeatherDb.get(context).weatherDao()

    override fun getCityList(): List<City> =
        localOpenWeatherMapDataSource.getCityList()

    override suspend fun queryWeatherByCityName(cityName: String, countryCode: String?): WeatherData? =
        WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByCityName(cityName, countryCode))

    override suspend fun queryWeatherById(cityId: Long?): WeatherData? =
        cityId?.let { WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherById(it)) }

    override suspend fun queryWeatherByCoordinate(lat: Double, lon: Double): WeatherData? =
        WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByCoordinate(lat, lon))

    override suspend fun queryWeatherByZipCode(zipCode: Long?, countryCode: String?): WeatherData? =
        zipCode?.let { WeatherMapper.mapToDomainEntities(openWeatherMapDataSource.getWeatherByZipCode(it, countryCode)) }

    override fun getAllLocationRecordsSortByLatestUpdate(): LiveData<PagedList<WeatherData>> =
        weatherDao.allRecordByLastUpdate().toLiveData(Config(PAGE_SIZE, MAX_SIZE, ENABLE_PLACE_HOLDERS))

    override fun lookupLocationRecordsSortByLatestUpdate(keywork: String): LiveData<PagedList<WeatherData>> =
        weatherDao.lookupRecordsByLastUpdate(keywork).toLiveData(Config(PAGE_SIZE, MAX_SIZE, ENABLE_PLACE_HOLDERS))

    override fun getLastLocationRecord(): WeatherData? = weatherDao.latestRecord()

    override fun getLocationRecordByCityId(cityId: Long?): WeatherData? = cityId?.let { weatherDao.recordByCityId(it) }

    override fun getLocationRecordByZipCode(zipCode: Long?, countryCode: String?): WeatherData? =
        zipCode?.let { countryCode?.let { weatherDao.recordByZipCode(zipCode, countryCode) } }

    override fun getLocationRecordByLocation(lat: Double, lon: Double): WeatherData? = weatherDao.recordByLocation(lat, lon)

    override fun updateLocationRecord(data: WeatherData) = weatherDao.update(data)

    override fun insertLocationRecord(data: WeatherData) = weatherDao.insert(data)

    override fun deleteLocationRecord(data: WeatherData) = weatherDao.delete(data)

    override fun deleteLocationRecords(list: List<WeatherData>) = weatherDao.delete(list)
}