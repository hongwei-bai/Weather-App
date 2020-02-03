package au.com.test.weather_app.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.CityId
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.Corrdinate
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.ZipCode
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

        private val COUNTRY_CODE_DIVIDER_CHARS = arrayOf(" ", ",")
        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}$"
    }

    val currentWeather: MutableLiveData<WeatherData> = MutableLiveData()

    val recentRecords: LiveData<PagedList<WeatherData>> =
        weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    fun go() {
        uiScope.launch {
            var queryZipCode: Long? = null
            withContext(contextProvider.IO) {
                weatherRepository.getLastLocationRecord()?.run {
                    queryZipCode = zipCode
                    val queryGroup = getQueryGroup()
                    println("go(): last location record: $cityName, zip: $zipCode, location: $latitude, $longitude, queryGroup: $queryGroup")
                    logger.i(TAG, "go(): last location record: $cityName, zip: $zipCode, location: $latitude, $longitude, queryGroup: $queryGroup")
                    when (queryGroup) {
                        CityId -> weatherRepository.queryWeatherById(cityId)
                        ZipCode -> weatherRepository.queryWeatherByZipCode(zipCode, countryCode)
                        Corrdinate -> {
                            println("go(): call queryWeatherByCoordinate")
                            weatherRepository.queryWeatherByCoordinate(latitude, longitude)
                        }
                    }
                } ?: emitNullCurrentWeatherLiveData()
            }?.let {
                println("go(): new current weather: $it")
                logger.i(TAG, "go(): new current weather: $it")
                updateLocationRecords(it, queryZipCode)
            }
        }
    }

    fun fetch(input: String) {
        logger.i(TAG, "fetch(): input string: $input")
        val countryCodeWithDividers: String? = getCountryCode(input)

        val keyWord = trimValidCityNameOrZipCode(input, countryCodeWithDividers)
        val countryCode = trimValidCountryCode(countryCodeWithDividers)
        logger.i(TAG, "fetch(): parsed keyWord: $keyWord, countryCode: $countryCode")

        var zipCode: Long? = null
        uiScope.launch {
            withContext(contextProvider.IO) {
                with(weatherRepository) {
                    (keyWord.toLongOrNull()?.let {
                        logger.i(TAG, "fetch(): queryWeatherByZipCode zip: $it, countryCode: $countryCode")
                        zipCode = it
                        queryWeatherByZipCode(it, countryCode)
                    } ?: queryWeatherByCityName(keyWord, countryCode))
                }
            }?.let {
                logger.i(TAG, "fetch(): new current weather: $it")
                updateLocationRecords(it, zipCode)
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

    private fun emitNullCurrentWeatherLiveData(): WeatherData? {
        uiScope.launch { currentWeather.value = null }
        return null
    }

    private fun updateLocationRecords(data: WeatherData, queryZipCode: Long? = null) {
        uiScope.launch { currentWeather.value = data }
        uiScope.launch(contextProvider.IO) {
            data.zipCode = queryZipCode
            val match: WeatherData? = getLocationRecord(data)
            logger.i(TAG, "found matched record in db: $match")

            match?.let {
                weatherRepository.updateLocationRecord(data.apply {
                    id = it.id
                    zipCode = queryZipCode
                })
            } ?: weatherRepository.insertLocationRecord(data)
        }
    }

    private fun getLocationRecord(data: WeatherData): WeatherData? = with(weatherRepository) {
        when (data.getQueryGroup()) {
            CityId -> getLocationRecordByCityId(data.cityId)
            ZipCode -> getLocationRecordByZipCode(data.zipCode, data.countryCode)
            Corrdinate -> getLocationRecordByLocation(data.latitude, data.longitude)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getCountryCode(input: String): String? =
        with(Pattern.compile(REGEX_COUNTRY_CODE).matcher(input)) {
            if (find()) {
                group(0)
            } else null
        }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun trimValidCountryCode(countryCodeWithDivider: String?): String =
        countryCodeWithDivider?.run {
            var string = this
            COUNTRY_CODE_DIVIDER_CHARS.forEach {
                while (string.contains(it)) {
                    string = string.replace(it, "")
                }
            }
            string
        } ?: ""

    private fun trimValidCityNameOrZipCode(input: String, countryCodeWithDividers: String?): String {
        var string = countryCodeWithDividers?.let { input.replace(countryCodeWithDividers, "") } ?: input
        COUNTRY_CODE_DIVIDER_CHARS.forEach {
            while (string.contains(it)) {
                string = string.replace(it, "")
            }
        }
        return string
    }
}