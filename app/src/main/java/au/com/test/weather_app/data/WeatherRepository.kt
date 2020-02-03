package au.com.test.weather_app.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.owm.models.City


interface WeatherRepository {
    fun getCityList(): List<City>

    suspend fun queryWeatherByCityName(cityName: String, countryCode: String? = null): WeatherData?

    suspend fun queryWeatherById(cityId: Long?): WeatherData?

    suspend fun queryWeatherByCoordinate(lat: Double, lon: Double): WeatherData?

    suspend fun queryWeatherByZipCode(zipCode: Long?, countryCode: String? = null): WeatherData?

    fun getAllLocationRecordsSortByLatestUpdate(): LiveData<PagedList<WeatherData>>

    fun lookupLocationRecordsSortByLatestUpdate(keyword: String): LiveData<PagedList<WeatherData>>

    fun getLastLocationRecord(): WeatherData?

    fun getLocationRecordByCityId(cityId: Long?): WeatherData?

    fun getLocationRecordByZipCode(zipCode: Long?, countryCode: String?): WeatherData?

    fun getLocationRecordByLocation(lat: Double, lon: Double): WeatherData?

    fun updateLocationRecord(data: WeatherData)

    fun insertLocationRecord(data: WeatherData)

    fun deleteLocationRecord(data: WeatherData)

    fun deleteLocationRecords(list: List<WeatherData>)
}