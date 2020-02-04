package au.com.test.weather_app.data.domain.mappers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.test.factory.ApiWeatherResponseFactory
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class WeatherMapperTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `on mapToDomainEntities() - standard api response - convert to domain entity correctly`() {
        // Given
        val weatherData = ApiWeatherResponseFactory.createWeatherResponse()

        // When
        val result: WeatherData? = WeatherMapper.mapToDomainEntities(weatherData)

        // Then
        assert(result is WeatherData)
        assert(result != null)
        result?.id shouldBe 0
        result?.cityName shouldBe weatherData.name
        result?.countryCode shouldBe weatherData.sys.country
        result?.zipCode shouldBe null
        result?.latitude shouldBe weatherData.coord.lat
        result?.longitude shouldBe weatherData.coord.lon
        result?.weatherConditionId shouldBe weatherData.weather.first().id
        result?.weather shouldBe weatherData.weather.first().main
        result?.weatherDescription shouldBe weatherData.weather.first().description
        result?.weatherIcon shouldBe weatherData.weather.first().icon
        result?.temperature shouldBe weatherData.main.temp
        result?.temperatureMin shouldBe weatherData.main.temp_min
        result?.temperatureMax shouldBe weatherData.main.temp_max
        result?.humidity shouldBe weatherData.main.humidity
        result?.windSpeed shouldBe weatherData.wind.speed
        result?.windDegree shouldBe weatherData.wind.deg
        assert(System.currentTimeMillis() >= result?.lastUpdate ?: Long.MAX_VALUE)
        assert(System.currentTimeMillis() - result?.lastUpdate!! < 10000L)
    }
}