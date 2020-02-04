package au.com.test.weather_app.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import au.com.test.weather_app.data.domain.entities.CityData


@Dao
interface CityDao {
    @Query("SELECT COUNT(*) FROM CityData")
    fun getCount(): Long

    @Insert
    fun insert(list: List<CityData>)

    @Insert
    fun update(data: CityData)

    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): List<CityData>
}