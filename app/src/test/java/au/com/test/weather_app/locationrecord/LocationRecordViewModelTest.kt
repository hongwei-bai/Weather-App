package au.com.test.weather_app.locationrecord

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.test.TestContextProvider
import au.com.test.weather_app.test.TestCoroutineRule
import au.com.test.weather_app.test.factory.DomainWeatherDataFactory
import au.com.test.weather_app.util.Logger
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@ExperimentalCoroutinesApi
class LocationRecordViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: LocationRecordViewModel
    private var weatherRepository: WeatherRepository = mock()

    @Mock
    lateinit var logger: Logger

    @Before
    fun setup() {
        LocalProperties.IS_LOGGING_ENABLED = false
        MockitoAnnotations.initMocks(this)
        viewModel = LocationRecordViewModel(
            weatherRepository,
            logger,
            TestContextProvider()
        )

        clearInvocations(weatherRepository)
    }

    @Test
    fun `on delete() - call delete a weather record - weather delete method called correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val weatherData = DomainWeatherDataFactory.createWeatherData()

            // When
            viewModel.delete(weatherData)

            // Then
            verify(weatherRepository, times(1)).deleteLocationRecord(eq(weatherData))
            verify(weatherRepository, never()).deleteLocationRecords(any())
        }
    }

    @Test
    fun `on deleteList() - call delete multiple weather records - weather batch delete method called correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val weatherDataList = DomainWeatherDataFactory.createWeatherDataList()

            // When
            viewModel.delete(weatherDataList)

            // Then
            verify(weatherRepository, times(1)).deleteLocationRecords(eq(weatherDataList))
            verify(weatherRepository, never()).deleteLocationRecord(any())
        }
    }
}