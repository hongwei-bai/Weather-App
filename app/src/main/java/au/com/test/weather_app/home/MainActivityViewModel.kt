package au.com.test.weather_app.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.dao.model.WeatherDb
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    @AppContext private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) : BaseViewModel() {
    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName

        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}"
    }

    private val dao = WeatherDb.get(context).weatherDao()

    val currentWeather: MutableLiveData<WeatherData> = MutableLiveData()

    val recentRecords = dao.allRecordByLastUpdate().toLiveData(
        Config(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
    )

    fun go() {
        GlobalScope.launch(Dispatchers.IO) {
            dao.latestRecord()?.apply {
                (cityId?.let { cityId ->
                    weatherRepository.queryWeatherById(cityId)
                } ?: weatherRepository.queryWeatherByCoordinate(latitude, longitude))
                    .subscribe({
                        logger.d(TAG, "queryWeatherById ($cityId) -> weather: $it")
                        notifyUpdate(it)
                    }, {
                        logger.w(TAG, "queryWeatherById onError: ${it.localizedMessage}")
                    })
            }
        }
    }

    fun fetch(input: String) {
        val countryCode: String? = getCountryCode(input)

        val keyWord = countryCode?.let {
            input.replace(it, "")
        } ?: input

        disposables.add(
            with(weatherRepository) {
                (keyWord.toLongOrNull()?.let { queryWeatherByZipCode(it, countryCode) }
                    ?: queryWeatherByCityName(keyWord, countryCode))
                    .subscribe({
                        logger.d(TAG, "queryWeather by $keyWord, $countryCode: $it")
                        notifyUpdate(it)
                    }, {
                        logger.w(TAG, "queryWeather onError: ${it.localizedMessage}")
                    })
            }
        )
    }

    fun fetch(lat: Double, lon: Double) {
        disposables.add(
            weatherRepository.queryWeatherByCoordinate(lat, lon).subscribe({
                logger.d(TAG, "queryWeatherByCoordinate ($lat, $lon) -> weather: $it")
                notifyUpdate(it)
            }, {
                logger.w(TAG, "queryWeatherByCoordinate onError: ${it.localizedMessage}")
            })
        )
    }

    private fun notifyUpdate(data: WeatherData) {
        GlobalScope.launch(Dispatchers.IO) {
            val match: WeatherData? = if (data.cityId != null) {
                dao.recordByCityId(data.cityId)
            } else {
                dao.recordByLocation(data.latitude, data.longitude)
            }

            match?.let {
                dao.update(data.apply {
                    id = it.id
                })
            } ?: dao.insert(data)
        }
        GlobalScope.launch(Dispatchers.Main) { currentWeather.value = data }
    }

    private fun getCountryCode(input: String): String? =
        with(Pattern.compile(REGEX_COUNTRY_CODE).matcher(input)) {
            if (find()) {
                group(0)
            } else null
        }
}