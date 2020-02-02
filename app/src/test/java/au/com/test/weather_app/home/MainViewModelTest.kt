package au.com.test.weather_app.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.test.TestContextProvider
import au.com.test.weather_app.test.TestCoroutineRule
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory
import au.com.test.weather_app.util.Logger
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
    fun `on go() - last location record has a city id - get correct current weather data`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneySnow = DomainWeatherDataFactory.createWeatherDataSydneySnow()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherData())
            whenever(weatherRepository.queryWeatherById(2147714L)).thenReturn(sydneySnow)

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(sydneySnow)
        }
    }
}