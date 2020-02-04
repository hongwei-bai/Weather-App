package au.com.test.weather_app.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.CityRepository
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
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.shouldBe
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
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
    private var cityRepository: CityRepository = mock()

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
            cityRepository,
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
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataSydneyCleared())
            weatherRepository.stub { onBlocking { queryWeatherById(2147714L) }.doReturn(sydneySnow) }
            weatherRepository.stub {
                onBlocking { getLocationRecordByCityId(2147714L) }.doReturn(
                    sydneySnow
                )
            }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(sydneySnow)
            verify(weatherRepository, never()).queryWeatherByCoordinate(any(), any())
            verify(weatherRepository, never()).getLocationRecordByLocation(any(), any())
            verify(weatherRepository, times(1)).updateLocationRecord(eq(sydneySnow))
            verify(weatherRepository, never()).insertLocationRecord(any())
        }
    }

    @Test
    fun `on go() - last location record has zip code - get correct current weather data by coordinate`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val surryHillsRain = DomainWeatherDataFactory.createWeatherDataSurryHillsRain()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataSurryHillsClearedLastWeek())
            weatherRepository.stub {
                onBlocking { queryWeatherByZipCode(2010, "AU") }.doReturn(
                    surryHillsRain
                )
            }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(surryHillsRain)
            verify(weatherRepository, never()).queryWeatherById(any())
        }
    }

    @Test
    fun `on go() - last location record has zip code - update record in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val surryHillsRain = DomainWeatherDataFactory.createWeatherDataSurryHillsRain()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataSurryHillsClearedLastWeek())
            weatherRepository.stub {
                onBlocking { queryWeatherByZipCode(2010, "AU") }.doReturn(
                    surryHillsRain
                )
            }
            weatherRepository.stub {
                onBlocking { getLocationRecordByZipCode(2010, "AU") }.doReturn(
                    surryHillsRain
                )
            }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(surryHillsRain)
            verify(weatherRepository, never()).queryWeatherById(any())
            verify(weatherRepository, never()).getLocationRecordByCityId(any())
            verify(weatherRepository, times(1)).updateLocationRecord(eq(surryHillsRain))
            verify(weatherRepository, never()).insertLocationRecord(any())
        }
    }

    @Test
    fun `on go() - last location record has corrdinate only - get correct current weather data by coordinate`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataNowhereCleared())
            weatherRepository.stub {
                onBlocking {
                    queryWeatherByCoordinate(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, never()).queryWeatherById(any())
        }
    }

    @Test
    fun `on go() - last location record has corrdinate only - update record in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()
            whenever(weatherRepository.getLastLocationRecord()).thenReturn(DomainWeatherDataFactory.createWeatherDataNowhereCleared())
            weatherRepository.stub {
                onBlocking {
                    queryWeatherByCoordinate(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }
            weatherRepository.stub {
                onBlocking {
                    getLocationRecordByLocation(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }

            // When
            viewModel.go()

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, never()).queryWeatherById(any())
            verify(weatherRepository, never()).getLocationRecordByCityId(any())
            verify(weatherRepository, times(1)).updateLocationRecord(eq(nowhereRain))
            verify(weatherRepository, never()).insertLocationRecord(any())
        }
    }

    @Test
    fun `on fetch(lat, lon) - fetch weather by corrdinate - get correct current weather data`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()
            whenever(weatherRepository.queryWeatherByCoordinate(-83.28, 105.95)).thenReturn(
                DomainWeatherDataFactory.createWeatherDataNowhereRainLastWeek()
            )
            weatherRepository.stub {
                onBlocking {
                    queryWeatherByCoordinate(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }

            // When
            viewModel.fetch(-83.28, 105.95)

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, never()).queryWeatherById(any())
        }
    }

    @Test
    fun `on fetch(lat, lon) - fetch weather by corrdinate, location exist in db - update record in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()

            weatherRepository.stub {
                onBlocking {
                    queryWeatherByCoordinate(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }
            whenever(weatherRepository.getLocationRecordByLocation(-83.28, 105.95)).thenReturn(
                DomainWeatherDataFactory.createWeatherDataNowhereRainLastWeek()
            )

            // When
            viewModel.fetch(-83.28, 105.95)

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, times(1)).updateLocationRecord(eq(nowhereRain))
            verify(weatherRepository, never()).insertLocationRecord(any())
        }
    }

    @Test
    fun `on fetch(lat, lon) - fetch weather by corrdinate, location not exist in db - insert record to db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val nowhereRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()

            weatherRepository.stub {
                onBlocking {
                    queryWeatherByCoordinate(
                        -83.28,
                        105.95
                    )
                }.doReturn(nowhereRain)
            }
            whenever(weatherRepository.getLocationRecordByLocation(-83.28, 105.95)).thenReturn(null)

            // When
            viewModel.fetch(-83.28, 105.95)

            // Then
            verify(viewStateObserver).onChanged(nowhereRain)
            verify(weatherRepository, times(1)).insertLocationRecord(eq(nowhereRain))
            verify(weatherRepository, never()).updateLocationRecord(any())
        }
    }

    @Test
    fun `on fetch - fetch weather by city name search - get correct current weather data`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneyRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()

            weatherRepository.stub {
                onBlocking { queryWeatherByCityName("Sydney", "AU") }.doReturn(
                    sydneyRain
                )
            }
            whenever(weatherRepository.getLocationRecordByCityId(2147714L)).thenReturn(
                DomainWeatherDataFactory.createWeatherDataSydneyClearedLastWeek()
            )

            // When
            viewModel.fetch("Sydney, AU")

            // Then
            verify(viewStateObserver).onChanged(sydneyRain)
        }
    }

    @Test
    fun `on fetch - fetch weather by city name search, location exist in db - update record in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneyRain = DomainWeatherDataFactory.createWeatherDataSydneyRain()

            weatherRepository.stub {
                onBlocking { queryWeatherByCityName("Sydney", "AU") }.doReturn(
                    sydneyRain
                )
            }
            whenever(weatherRepository.getLocationRecordByCityId(2147714L)).thenReturn(
                DomainWeatherDataFactory.createWeatherDataSydneyClearedLastWeek()
            )

            // When
            viewModel.fetch("Sydney, AU")

            // Then
            verify(viewStateObserver).onChanged(sydneyRain)
            verify(weatherRepository, times(1)).updateLocationRecord(eq(sydneyRain))
            verify(weatherRepository, never()).insertLocationRecord(any())
        }
    }

    @Test
    fun `on fetch - fetch weather by city name search, location not exist in db - insert record to db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val sydneyRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()

            weatherRepository.stub {
                onBlocking { queryWeatherByCityName("Sydney", "AU") }.doReturn(
                    sydneyRain
                )
            }
            whenever(weatherRepository.getLocationRecordByCityId(2147714L)).thenReturn(null)

            // When
            viewModel.fetch("Sydney, AU")

            // Then
            verify(viewStateObserver).onChanged(sydneyRain)
            verify(weatherRepository, never()).updateLocationRecord(any())
            verify(weatherRepository, times(1)).insertLocationRecord(eq(sydneyRain))
        }
    }

    @Test
    fun `on fetch - fetch weather by zip code search - get correct current weather data`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val surryHillsRain = DomainWeatherDataFactory.createWeatherDataNowhereRain()

            weatherRepository.stub {
                onBlocking { queryWeatherByZipCode(2010, "AU") }.doReturn(
                    surryHillsRain
                )
            }
            whenever(weatherRepository.getLocationRecordByZipCode(2010, "AU")).thenReturn(
                DomainWeatherDataFactory.createWeatherDataSurryHillsClearedLastWeek()
            )

            // When
            viewModel.fetch("2010, AU")

            // Then
            verify(viewStateObserver).onChanged(surryHillsRain)
        }
    }

    @Test
    fun `on getCountryCode() - type varies input - get correct country code, scenario #testName`() {
        // Given
        val testData = table(
            headers("testName", "input", "expectedResult"),
            row("city name only", "Sydney", ""),
            row("city name with space", "Los Angles", ""),
            row("city name and country code divided by space", "Sydney AU", "AU"),
            row("city name and country code divided by comma", "Sydney,AU", "AU"),
            row("city name and country code divided by space and comma", "Los Angles, US", "US"),
            row("city name with space and country code divided by space", "Los Angles US", "US"),
            row("city name with space and country code divided by comma", "Los Angles,US", "US"),
            row(
                "city name with space and country code divided by space and comma",
                "Los Angles, US",
                "US"
            )
        )

        forAll(testData) { testName: String, input: String, expectedResult: String? ->
            // When
            val actualResult = with(viewModel) {
                trimValidCountryCode(getCountryCode(input))
            }

            // Then
            actualResult shouldBe expectedResult
        }
    }

    @Test
    fun `on initializeCityIndexTable - no city data in db - verify read from json and save in db correctly`() {
        testCoroutineRule.runBlockingTest {
            // Given
            cityRepository.stub { onBlocking { getCityCount() }.doReturn(0) }

            // When
            viewModel.initializeCityIndexTable()

            // Then
            verify(cityRepository, times(1)).readCityList()
            verify(cityRepository, times(1)).writeCityList(any())
        }
    }

    @Test
    fun `on initializeCityIndexTable - has city data in db - verify no initialization launched`() {
        testCoroutineRule.runBlockingTest {
            // Given
            cityRepository.stub { onBlocking { getCityCount() }.doReturn(1) }

            // When
            viewModel.initializeCityIndexTable()

            // Then
            verify(cityRepository, never()).readCityList()
            verify(cityRepository, never()).writeCityList(any())
        }
    }
}
