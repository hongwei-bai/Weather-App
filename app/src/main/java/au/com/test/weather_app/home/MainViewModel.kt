package au.com.test.weather_app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.CoroutineContextProvider
import au.com.test.weather_app.util.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val logger: Logger,
    private val contextProvider: CoroutineContextProvider
) : BaseViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}"
    }

    val currentWeather: MutableLiveData<WeatherData> = MutableLiveData()

    val recentRecords: LiveData<PagedList<WeatherData>> =
        weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    fun go() {
        uiScope.launch {
            withContext(contextProvider.IO) {
                weatherRepository.getLastLocationRecord()?.run {
                    logger.i(
                        TAG,
                        "go(): last location record: $cityName, location: $latitude, $longitude"
                    )
                    cityId?.let {
                        logger.i(TAG, "go(): queryWeatherById: $cityId")
                        weatherRepository.queryWeatherById(cityId)
                    } ?: weatherRepository.queryWeatherByCoordinate(latitude, longitude)
                } ?: emitNullCurrentWeatherLiveData()
            }?.let {
                logger.i(TAG, "go(): new current weather: $it")
                updateLocationRecords(it)
            }
        }
    }

    private fun emitNullCurrentWeatherLiveData(): WeatherData? {
        uiScope.launch { currentWeather.value = null }
        return null
    }

    fun fetch(input: String) {
        logger.i(TAG, "fetch(): input string: $input")
        val countryCode: String? = getCountryCode(input)

        val keyWord = countryCode?.let {
            input.replace(it, "")
        } ?: input
        logger.i(TAG, "fetch(): parsed keyWord: $keyWord, countryCode: $countryCode")

        uiScope.launch {
            withContext(contextProvider.IO) {
                with(weatherRepository) {
                    (keyWord.toLongOrNull()?.let {
                        logger.i(
                            TAG,
                            "fetch(): queryWeatherByZipCode zip: $it, countryCode: $countryCode"
                        )
                        queryWeatherByZipCode(it, countryCode)
                    }
                        ?: queryWeatherByCityName(keyWord, countryCode))
                }
            }?.let {
                logger.i(TAG, "fetch(): new current weather: $it")
                updateLocationRecords(it)
            }
        }
    }

    fun fetch(lat: Double, lon: Double) {
        uiScope.launch {
            withContext(contextProvider.IO) {
                logger.i(TAG, "fetch(lat, lon): queryWeatherByCoordinate lat: $lat, lon: $lon")
                weatherRepository.queryWeatherByCoordinate(lat, lon)
            }?.let {
                logger.i(TAG, "fetch(lat, lon): new current weather: $it")
                updateLocationRecords(it)
            }
        }
    }

    private fun updateLocationRecords(data: WeatherData) {
        uiScope.launch { currentWeather.value = data }
        uiScope.launch(contextProvider.IO) {
            with(weatherRepository) {
                val match: WeatherData? = if (data.cityId != null) {
                    getLocationRecordByCityId(data.cityId)
                } else {
                    getLocationRecordByLocation(data.latitude, data.longitude)
                }
                logger.i(TAG, "found matched record in db: $match")

                match?.let {
                    updateLocationRecord(data.apply {
                        id = it.id
                    })
                } ?: insertLocationRecord(data)
            }
        }
    }

    private fun getCountryCode(input: String): String? =
        with(Pattern.compile(REGEX_COUNTRY_CODE).matcher(input)) {
            if (find()) {
                group(0)
            } else null
        }
}