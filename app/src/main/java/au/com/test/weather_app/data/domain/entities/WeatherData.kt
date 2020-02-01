package au.com.test.weather_app.data.domain.entities

import android.content.Context
import au.com.test.weather_app.R

data class WeatherData(
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
) {
    fun getTitle(context: Context): String = cityName ?: context.resources.getString(R.string.unknown_location, latitude, longitude)
}