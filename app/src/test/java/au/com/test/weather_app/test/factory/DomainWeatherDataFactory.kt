package au.com.test.weather_app.test.factory

import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestCity.SomewhereInAntarctica
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestCity.Sydney
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestDate.Now
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestDate.WeekAgo
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestWeatherCondition.Cleared
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestWeatherCondition.Rain
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory.TestWeatherCondition.Snow

object DomainWeatherDataFactory {
    enum class TestWeatherCondition(val main: String, val desc: String) {
        Cleared("Cleared", "cleared"),
        Snow("Snow", "heavy snow"),
        Rain("Rain", "light rain");
    }

    enum class TestCity(val cityId: Long?, val cityName: String?, val lat: Double, val lon: Double) {
        Sydney(2147714L, "Sydney", 151.21, -33.87),
        Melbourne(2158177L, "Melbourne", 144.96, -37.81),
        Los_Angeles(5368361L, "Los Angeles", -118.24, 34.05),
        SomewhereInAntarctica(null, null, 105.95, -83.28);
    }

    enum class TestDate(val time: Long) {
        Now(System.currentTimeMillis()),
        HoursAgo(System.currentTimeMillis() - 60 * 60 * 1000L),
        WeekAgo(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
    }

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

    fun createWeatherData(city: TestCity = Sydney, weather: TestWeatherCondition = Cleared, date: TestDate = Now): WeatherData = WeatherData(
        id = 0,
        cityId = city.cityId,
        cityName = city.cityName,
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