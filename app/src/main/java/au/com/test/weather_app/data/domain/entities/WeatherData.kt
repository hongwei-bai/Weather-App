package au.com.test.weather_app.data.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val cityId: Long?,
    val cityName: String?,
    val countryCode: String?,
    var zipCode: Long?,
    val latitude: Double,
    val longitude: Double,
    val weatherConditionId: Long,
    val weather: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val temperature: Float,
    val temperatureMin: Float,
    val temperatureMax: Float,
    val humidity: Int,
    val windSpeed: Float,
    val windDegree: Int,
    var lastUpdate: Long
) {
    fun getCityTitle(): String? = if (cityName != null && countryCode != null) {
        "$cityName,  ${zipCode ?: ""} $countryCode"
    } else {
        null
    }

    fun isGpsCoordinate(): Boolean = cityId == null && zipCode == null

    fun getQueryGroup(): QueryGroup =
        if (cityId != null) {
            QueryGroup.CityId
        } else if (zipCode != null && countryCode != null) {
            QueryGroup.ZipCode
        } else {
            QueryGroup.Corrdinate
        }

    // Sorted by priority
    enum class QueryGroup { CityId, ZipCode, Corrdinate }
}