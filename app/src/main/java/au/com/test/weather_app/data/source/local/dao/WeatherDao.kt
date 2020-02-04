package au.com.test.weather_app.data.source.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import au.com.test.weather_app.data.domain.entities.WeatherData

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherData ORDER BY lastUpdate COLLATE NOCASE DESC")
    fun allRecordByLastUpdate(): DataSource.Factory<Int, WeatherData>

    @Query("SELECT * FROM WeatherData WHERE cityName like :keyword ORDER BY lastUpdate COLLATE NOCASE DESC")
    fun lookupRecordsByLastUpdate(keyword: String): DataSource.Factory<Int, WeatherData>

    @Query("SELECT * FROM WeatherData ORDER BY lastUpdate COLLATE NOCASE DESC LIMIT 1")
    fun latestRecord(): WeatherData?

    @Query("SELECT * FROM WeatherData WHERE cityId = :cityId")
    fun recordByCityId(cityId: Long): WeatherData?

    @Query("SELECT * FROM WeatherData WHERE zipCode = :zipCode AND countryCode = :countryCode")
    fun recordByZipCode(zipCode: Long, countryCode: String): WeatherData?

    @Query("SELECT * FROM WeatherData WHERE latitude = :latitude AND longitude = :longitude")
    fun recordByLocation(latitude: Double, longitude: Double): WeatherData?

    @Update
    fun update(data: WeatherData)

    @Insert
    fun insert(data: WeatherData)

    @Delete
    fun delete(data: WeatherData)

    @Delete
    fun delete(list: List<WeatherData>)
}