package au.com.test.weather_app.data.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val cityId: Long?,
    val cityName: String?,
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
)