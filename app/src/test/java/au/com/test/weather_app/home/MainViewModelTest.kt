package au.com.test.weather_app.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.test.TestContextProvider
import au.com.test.weather_app.test.TestCoroutineRule
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory
import au.com.test.weather_app.util.Logger
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: MainViewModel
    private var weatherRepository: WeatherRepository = mock()

    @Mock
    lateinit var logger: Logger
    private var viewStateObserver: Observer<WeatherData> = mock()
    private var viewStateObserver2: Observer<City> = mock()

    @Before
    fun setup() {
        LocalProperties.IS_LOGGING_ENABLED = false
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(
            weatherRepository,
            logger,
            TestContextProvider()
        ).apply {
            currentWeather.observeForever(viewStateObserver)
        }

        clearInvocations(weatherRepository)
    }

    @After
    fun teardown() {
        LocalProperties.IS_LOGGING_ENABLED = true
    }

    @Test
    fun `on go() - no location record in db - emit a null current weather to view so that view could show search bar`() {
        testCoroutineRule.runBlockingTest {
            // Given
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(null)

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(null)
        }
    }

    @Test
    fun `on go() - last location record has city id - get correct current weather data`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneySnow = DomainWeatherDataFactory.createWeatherDataSydneySnow()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherData())
            weatherRepository.stub { onBlocking { queryWeatherById(2147714L) }.doReturn(sydneySnow) }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(sydneySnow)
            verify(weatherRepository, never()).queryWeatherByCoordinate(any(), any())
        }
    }

    @Test
    fun `on go() - last location record has city id - update record in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneySnow = DomainWeatherDataFactory.createWeatherDataSydneySnowLastWeek()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherData())
            weatherRepository.stub { onBlocking { queryWeatherById(2147714L) }.doReturn(sydneySnow) }
            weatherRepository.stub { onBlocking { getLocationRecordByCityId(2147714L) }.doReturn(sydneySnow) }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(sydneySnow)
            verify(weatherRepository, never()).queryWeatherByCoordinate(any(), any())
        }
    }

    @Test
    fun `on go() - last location record has no city id - get correct current weather data by coordinate`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataNowhereSnow())
            weatherRepository.stub { onBlocking { queryWeatherByCoordinate(-33.87, 151.21) }.doReturn(nowhereRain) }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, never()).queryWeatherById(any())
        }
    }
}