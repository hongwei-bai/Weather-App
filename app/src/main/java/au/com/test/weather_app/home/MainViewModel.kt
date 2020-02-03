package au.com.test.weather_app.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.CityRepository
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.CityData
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.CityId
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.Corrdinate
import au.com.test.weather_app.data.domain.entities.WeatherData.QueryGroup.ZipCode
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.CoroutineContextProvider
import au.com.test.weather_app.util.Logger
import au.com.test.weather_app.util.PerformanceUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val logger: Logger,
    private val contextProvider: CoroutineContextProvider
) : BaseViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private val COUNTRY_CODE_DIVIDER_CHARS = arrayOf(" ", ",")
        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}$"
    }

    val currentWeather: MutableLiveData<WeatherData> = MutableLiveData()

    val recentRecords: LiveData<PagedList<WeatherData>> = weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    val searchSuggestions: MutableLiveData<List<CityData>> = MutableLiveData()

    val uiError: MutableLiveData<Throwable?> = MutableLiveData()

    private val handler = CoroutineExceptionHandler { _, exception ->
        logger.e(TAG, "caught view model level exception: ${exception.localizedMessage}")
        uiError.value = exception
    }

    fun go() {
        fetchLatestLocation()
    }

    fun initializeCityIndexTable() {
        uiScope.launch(contextProvider.IO) {
            val count = cityRepository.getCityCount()
            logger.i(TAG, "initializeCityIndexTable city db count: $count")

            if (count == 0L) {
                val list = PerformanceUtil<List<CityData>>().tick { cityRepository.readCityList() }
                PerformanceUtil<Unit>().tick { cityRepository.writeCityList(list) }
            }
        }
    }

    fun onSearchTextChange(string: String) {
        uiScope.launch(contextProvider.Main) {
            searchSuggestions.value = if (string.isNotBlank()) {
                withContext(contextProvider.IO) {
                    cityRepository.lookupCity(string)
                }
            } else emptyList()
        }
    }

    fun fetch(input: String) {
        logger.i(TAG, "fetch(): input string: $input")
        val info = parseInput(input)
        logger.i(TAG, "fetch(): parsed keyWord: ${info.first}, countryCode: ${info.second}")
        fetch(info.first, info.second)
    }

    fun fetch(lat: Double, lon: Double) {
        uiScope.launch(handler) {
            withContext(contextProvider.IO) {
                logger.i(TAG, "fetch(lat, lon): queryWeatherByCoordinate lat: $lat, lon: $lon")
                weatherRepository.queryWeatherByCoordinate(lat, lon)
            }?.let {
                logger.i(TAG, "fetch(lat, lon): new current weather: $it")
                updateLocationRecords(it)
            }
        }
    }

    fun fetch(data: WeatherData) {
        uiScope.launch(handler) {
            withContext(contextProvider.IO) {
                val queryGroup = data.getQueryGroup()
                logger.i(TAG, "fetch(WeatherData): last location record: ${data.cityName}, zip: ${data.zipCode}, location: ${data.latitude}, ${data.longitude}, queryGroup: $queryGroup")
                when (queryGroup) {
                    CityId -> weatherRepository.queryWeatherById(data.cityId)
                    ZipCode -> weatherRepository.queryWeatherByZipCode(data.zipCode, data.countryCode)
                    Corrdinate -> {
                        println("go(): call queryWeatherByCoordinate")
                        weatherRepository.queryWeatherByCoordinate(data.latitude, data.longitude)
                    }
                }
            }?.let {
                logger.i(TAG, "fetch(WeatherData): new current weather: $it")
                updateLocationRecords(it, data.zipCode)
            }
        }
    }

    fun fetch(keyWord: String, countryCode: String) {
        var zipCode: Long? = null
        uiScope.launch(handler) {
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

    private fun emitNullCurrentWeatherLiveData(): WeatherData? {
        uiScope.launch { currentWeather.value = null }
        return null
    }

    private fun updateLocationRecords(data: WeatherData, queryZipCode: Long? = null) {
        uiScope.launch {
            currentWeather.value = data
            uiError.value = null
        }
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

    private fun fetchLatestLocation() {
        uiScope.launch(contextProvider.IO) {
            weatherRepository.getLastLocationRecord()?.let { data ->
                fetch(data)
            } ?: emitNullCurrentWeatherLiveData()
        }
    }

    private fun parseInput(input: String): Pair<String, String> {
        val countryCodeWithDividers: String? = getCountryCode(input)
        val keyWord = trimValidCityNameOrZipCode(input, countryCodeWithDividers)
        val countryCode = trimValidCountryCode(countryCodeWithDividers)
        return Pair(keyWord, countryCode)
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