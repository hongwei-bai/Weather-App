package au.com.test.weather_app.data.domain.entities

data class WeatherData(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
    val temperature: Float,
    val temperatureMin: Float,
    val temperatureMax: Float,
    val humidity: Int,
    val windSpeed: Float,
    val windDegree: Int
)