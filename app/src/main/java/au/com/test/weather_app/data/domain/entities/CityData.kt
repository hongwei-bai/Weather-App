package au.com.test.weather_app.data.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CityData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val name: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val owmCityId: Long,
    var searchCount: Long = 0,
    var lastSearch: Long
) {
    override fun toString(): String = "$name, $countryCode"

    fun getCityTitle(): String? = "$name $countryCode"
}