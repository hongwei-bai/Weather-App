package au.com.test.weather_app.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

class MainViewModel @Inject constructor(
    @AppContext private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
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
            withContext(Dispatchers.Default) {
                weatherRepository.getLastLocationRecord()?.run {
                    cityId?.let {
                        weatherRepository.queryWeatherById(cityId)
                    } ?: weatherRepository.queryWeatherByCoordinate(latitude, longitude)
                }
            }?.let { updateLocationRecords(it) }
        }
    }

    fun fetch(input: String) {
        val countryCode: String? = getCountryCode(input)

        val keyWord = countryCode?.let {
            input.replace(it, "")
        } ?: input

        uiScope.launch {
            withContext(Dispatchers.Default) {
                with(weatherRepository) {
                    (keyWord.toLongOrNull()?.let { queryWeatherByZipCode(it, countryCode) }
                        ?: queryWeatherByCityName(keyWord, countryCode))
                }
            }?.let { updateLocationRecords(it) }
        }
    }

    fun fetch(lat: Double, lon: Double) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                weatherRepository.queryWeatherByCoordinate(lat, lon)
            }?.let { updateLocationRecords(it) }
        }
    }

    private fun updateLocationRecords(data: WeatherData) {
        uiScope.launch { currentWeather.value = data }
        uiScope.launch(IO) {
            with(weatherRepository) {
                val match: WeatherData? = if (data.cityId != null) {
                    getLocationRecordByCityId(data.cityId)
                } else {
                    getLocationRecordByLocation(data.latitude, data.longitude)
                }

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