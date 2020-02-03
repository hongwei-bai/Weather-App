package au.com.test.weather_app.data

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.sqlite.db.SimpleSQLiteQuery
import au.com.test.weather_app.data.domain.entities.CityData
import au.com.test.weather_app.data.domain.mappers.CityMapper
import au.com.test.weather_app.data.source.cache.Cache
import au.com.test.weather_app.data.source.local.dao.CityDb
import au.com.test.weather_app.data.source.local.owm.LocalOpenWeatherMapDataSource
import au.com.test.weather_app.di.annotations.AppContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CityManager @Inject constructor(
    @AppContext private val context: Context,
    private val localOpenWeatherMapDataSource: LocalOpenWeatherMapDataSource,
    private val cache: Cache
) : CityRepository {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var cityDao = CityDb.get(context).cityDao()

    override fun readCityList(): List<CityData> = localOpenWeatherMapDataSource.getCityList().map { CityMapper.mapToDomainEntities(it) }

    override fun writeCityList(list: List<CityData>) {
        cityDao.insert(list)
    }

    override fun getCityCount(): Long = cityDao.getCount()

    override fun lookupCity(city: String, limit: Int): List<CityData> =
        if (city.isNotBlank()) {
            "WHERE name LIKE '$city%'"
        } else {
            ""
        }.let { whereClause ->
            cityDao.rawQuery(
                SimpleSQLiteQuery("SELECT * FROM CityData $whereClause ORDER BY name COLLATE NOCASE ASC, searchCount DESC, lastSearch DESC LIMIT $limit")
            )
        }

    //@Query("SELECT * FROM CityData WHERE name like :city ORDER BY name COLLATE NOCASE ASC, searchCount DESC, lastSearch DESC LIMIT :limit")
    //    fun lookupCity(city: String, limit: Int): List<CityData>
    //
    //    @Query("SELECT * FROM CityData WHERE countryCode like :country ORDER BY name COLLATE NOCASE ASC, searchCount DESC, lastSearch DESC LIMIT :limit")
    //    fun lookupCountry(country: String, limit: Int): List<CityData>
    //
    //    @Query("SELECT * FROM CityData WHERE name like :city AND countryCode like :country ORDER BY name COLLATE NOCASE ASC, searchCount DESC, lastSearch DESC LIMIT :limit")
    //    fun lookupCityAndCountry(city: String, country: String, limit: Int): List<CityData>
}