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
import au.com.test.weather_app.uicomponents.model.Default
import au.com.test.weather_app.uicomponents.model.Error
import au.com.test.weather_app.uicomponents.model.Loading
import au.com.test.weather_app.uicomponents.model.Success
import au.com.test.weather_app.uicomponents.model.ViewState
import au.com.test.weather_app.util.CoroutineContextProvider
import au.com.test.weather_app.util.Logger
import au.com.test.weather_app.util.PerformanceUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
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

    val currentWeatherState: MutableLiveData<ViewState> = MutableLiveData()

    val recentRecords: LiveData<PagedList<WeatherData>> =
        weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    val searchSuggestions: MutableLiveData<List<CityData>> = MutableLiveData()

    private val handler = CoroutineExceptionHandler { _, exception ->
        logger.e(TAG, "caught view model level exception: ${exception.localizedMessage}")
        publishViewStateError(exception)
    }

    fun go() = launch {
        val record = fetchLatestLocationRecord()
        if (record != null) {
            fetch(record)
        } else {
            publishViewStateDefault()
        }
    }

    fun fetch(input: String) = parseInput(input).run { fetch(first, second) }

    fun fetch(lat: Double, lon: Double) = fetchWeather { fetchWeatherByCoordinate(lat, lon) }

    fun fetch(data: WeatherData) = when (data.getQueryGroup()) {
        CityId -> fetchWeather { fetchWeatherByCityId(data.cityId) }
        ZipCode -> fetchWeather(data.zipCode) { fetchWeatherByZipCode(data.zipCode, data.countryCode) }
        Corrdinate -> fetchWeather { fetchWeatherByCoordinate(data.latitude, data.longitude) }
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

    // TODO top level function, to simplify.
    fun fetch(keyWord: String, countryCode: String) {
        publishViewStateLoading()
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
                postProcessFetchedData(it, zipCode)
            }
        }
    }

    // below is details flows
    private fun fetchWeather(zipCode: Long? = null, action: suspend () -> WeatherData) {
        launch {
            publishViewStateLoading()
            val weather = action.invoke()
            publishViewStateSuccess(weather)
            updateLocationRecords(weather, zipCode)
        }
    }

    private fun launch(action: suspend () -> Unit) {
        uiScope.launch(handler) {
            action.invoke()
        }
    }

    private suspend fun fetchLatestLocationRecord(): WeatherData =
        withContext(contextProvider.IO) {
            weatherRepository.getLastLocationRecord() ?: throw IllegalArgumentException()
        }

    private suspend fun fetchWeatherByCityId(cityId: Long?): WeatherData =
        withContext(contextProvider.IO) {
            logger.i(TAG, "fetch(): queryWeatherByCityId cityId: $cityId")
            val r = weatherRepository.queryWeatherById(cityId)
            logger.e(TAG, "r: $r")
            r ?: throw IllegalArgumentException()
        }

    private suspend fun fetchWeatherByCityName(cityName: String, countryCode: String): WeatherData =
        withContext(contextProvider.IO) {
            logger.i(TAG, "fetch(): queryWeatherByCityName zip: $cityName, countryCode: $countryCode")
            weatherRepository.queryWeatherByCityName(cityName, countryCode) ?: throw IllegalArgumentException()
        }

    private suspend fun fetchWeatherByZipCode(zipCode: Long?, countryCode: String?): WeatherData =
        withContext(contextProvider.IO) {
            logger.i(TAG, "fetch(): queryWeatherByZipCode zip: $zipCode, countryCode: $countryCode")
            weatherRepository.queryWeatherByZipCode(zipCode, countryCode) ?: throw IllegalArgumentException()
        }

    private suspend fun fetchWeatherByCoordinate(lat: Double, lon: Double): WeatherData =
        withContext(contextProvider.IO) {
            logger.i(TAG, "fetch(lat, lon): queryWeatherByCoordinate lat: $lat, lon: $lon")
            weatherRepository.queryWeatherByCoordinate(lat, lon) ?: throw IllegalArgumentException()
        }

    private fun postProcessFetchedData(data: WeatherData, queryZipCode: Long? = null) {
        publishViewStateSuccess(data)
        updateLocationRecords(data, queryZipCode)
    }

    private fun updateLocationRecords(data: WeatherData, queryZipCode: Long? = null) {
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

    private fun trimValidCityNameOrZipCode(
        input: String,
        countryCodeWithDividers: String?
    ): String {
        var string =
            countryCodeWithDividers?.let { input.replace(countryCodeWithDividers, "") } ?: input
        COUNTRY_CODE_DIVIDER_CHARS.forEach {
            while (string.contains(it)) {
                string = string.replace(it, "")
            }
        }
        return string
    }

    private fun publishViewStateSuccess(data: WeatherData) =
        uiScope.launch(contextProvider.Main) {
            currentWeatherState.value = Success(data)
        }

    private fun publishViewStateLoading() =
        uiScope.launch(contextProvider.Main) {
            currentWeatherState.value = Loading
        }

    private fun publishViewStateError(exception: Throwable) =
        uiScope.launch(contextProvider.Main) {
            currentWeatherState.value = Error(exception)
        }

    private fun publishViewStateDefault() =
        uiScope.launch(contextProvider.Main) {
            currentWeatherState.value = Default
        }
}