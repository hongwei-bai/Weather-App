package au.com.test.weather_app.test.factory

import au.com.test.weather_app.data.domain.entities.WeatherData

object DomainWeatherDataFactory {
    fun createWeatherDataList(): List<WeatherData> = listOf(
        createWeatherData(),
        createWeatherData()
    )

    fun createWeatherData(): WeatherData = WeatherData(
        id = 0,
        cityId = 2147714,
        cityName = "Sydney",
        latitude = -33.87,
        longitude = 151.21,
        weatherConditionId = 500,
        weather = "Rain",
        weatherDescription = "light rain",
        weatherIcon = "10n",
        temperature = 295.61f,
        temperatureMin = 294.82f,
        temperatureMax = 296.48f,
        humidity = 94,
        windSpeed = 5.1f,
        windDegree = 200,
        lastUpdate = System.currentTimeMillis()
    )

    fun createWeatherDataSydneySnow(): WeatherData = WeatherData(
        id = 0,
        cityId = 2147714,
        cityName = "Sydney",
        latitude = -33.87,
        longitude = 151.21,
        weatherConditionId = 500,
        weather = "Snow",
        weatherDescription = "light rain",
        weatherIcon = "20n",
        temperature = 295.61f,
        temperatureMin = 294.82f,
        temperatureMax = 296.48f,
        humidity = 94,
        windSpeed = 5.1f,
        windDegree = 200,
        lastUpdate = System.currentTimeMillis()
    )
}