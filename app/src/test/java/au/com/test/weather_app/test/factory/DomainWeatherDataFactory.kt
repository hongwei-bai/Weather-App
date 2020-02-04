package au.com.test.weather_app.test.factory

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.test.factory.TestCity.SomewhereInAntarctica
import au.com.test.weather_app.test.factory.TestCity.SurryHills
import au.com.test.weather_app.test.factory.TestCity.Sydney
import au.com.test.weather_app.test.factory.TestDate.Now
import au.com.test.weather_app.test.factory.TestDate.WeekAgo
import au.com.test.weather_app.test.factory.TestWeatherCondition.Cleared
import au.com.test.weather_app.test.factory.TestWeatherCondition.Rain
import au.com.test.weather_app.test.factory.TestWeatherCondition.Snow

object DomainWeatherDataFactory {
    fun createWeatherDataList(): List<WeatherData> = listOf(
        createWeatherData(),
        createWeatherData()
    )

    fun createWeatherDataSydneyCleared() = createWeatherData()
    fun createWeatherDataSydneyRain() = createWeatherData(weather = Rain)
    fun createWeatherDataSydneySnow() = createWeatherData(weather = Snow)

    fun createWeatherDataSydneyClearedLastWeek() = createWeatherData(date = WeekAgo)
    fun createWeatherDataSydneyRainLastWeek() = createWeatherData(weather = Rain, date = WeekAgo)
    fun createWeatherDataSydneySnowLastWeek() = createWeatherData(weather = Snow, date = WeekAgo)

    fun createWeatherDataNowhereCleared() = createWeatherData(city = SomewhereInAntarctica)
    fun createWeatherDataNowhereRain() = createWeatherData(city = SomewhereInAntarctica, weather = Rain)
    fun createWeatherDataNowhereSnow() = createWeatherData(city = SomewhereInAntarctica, weather = Snow)

    fun createWeatherDataNowhereClearedLastWeek() = createWeatherData(city = SomewhereInAntarctica, date = WeekAgo)
    fun createWeatherDataNowhereRainLastWeek() = createWeatherData(city = SomewhereInAntarctica, weather = Rain, date = WeekAgo)
    fun createWeatherDataNowhereSnowLastWeek() = createWeatherData(city = SomewhereInAntarctica, weather = Snow, date = WeekAgo)

    fun createWeatherDataSurryHillsCleared() = createWeatherData(city = SurryHills)
    fun createWeatherDataSurryHillsRain() = createWeatherData(city = SurryHills, weather = Rain)
    fun createWeatherDataSurryHillsSnow() = createWeatherData(city = SurryHills, weather = Snow)

    fun createWeatherDataSurryHillsClearedLastWeek() = createWeatherData(city = SurryHills, date = WeekAgo)
    fun createWeatherDataSurryHillsRainLastWeek() = createWeatherData(city = SurryHills, weather = Rain, date = WeekAgo)
    fun createWeatherDataSurryHillsSnowLastWeek() = createWeatherData(city = SurryHills, weather = Snow, date = WeekAgo)

    fun createWeatherData(city: TestCity = Sydney, weather: TestWeatherCondition = Cleared, date: TestDate = Now): WeatherData = WeatherData(
        id = 0,
        cityId = city.cityId,
        cityName = city.cityName,
        countryCode = city.countryCode,
        zipCode = city.zipCode,
        latitude = city.lat,
        longitude = city.lon,
        weatherConditionId = 500,
        weather = weather.main,
        weatherDescription = weather.desc,
        weatherIcon = "10n",
        temperature = 295.61f,
        temperatureMin = 294.82f,
        temperatureMax = 296.48f,
        humidity = 94,
        windSpeed = 5.1f,
        windDegree = 200,
        lastUpdate = date.time
    )
}