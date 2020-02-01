package au.com.test.weather_app.data.source.local.dao.model

import androidx.paging.DataSource
import androidx.room.*
import au.com.test.weather_app.data.domain.entities.WeatherData

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherData ORDER BY lastUpdate COLLATE NOCASE DESC")
    fun allRecordByLastUpdate(): DataSource.Factory<Int, WeatherData>

    @Query("SELECT * FROM WeatherData ORDER BY lastUpdate COLLATE NOCASE DESC LIMIT 1")
    fun latestRecord(): WeatherData?

    @Query("SELECT * FROM WeatherData WHERE cityId = :cityId")
    fun recordByCityId(cityId: Long): WeatherData

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