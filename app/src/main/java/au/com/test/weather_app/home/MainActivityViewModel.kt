package au.com.test.weather_app.home

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.home.presenter.RecentSearchManager
import au.com.test.weather_app.util.Logger
import java.util.regex.Pattern
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    @AppContext private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val recentSearchManager: RecentSearchManager,
    private val logger: Logger
) : BaseViewModel() {
    companion object {
        private val TAG = MainActivityViewModel::class.java.simpleName

        private const val REGEX_COUNTRY_CODE = "[ ,]{1}\\w{2}"
    }

    fun go() {

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
                        notifyViewUpdate(it)
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
                notifyViewUpdate(it)
            }, {
                logger.w(TAG, "queryWeatherByCoordinate onError: ${it.localizedMessage}")
            })
        )
    }

    private fun notifyViewUpdate(data: WeatherData) {
//        view.onCurrentWeatherUpdate(data)
//        view.onRecentRecordListUpdate(recentSearchManager.apply {
//            addRecord(data)
//        }.getList())
    }

    private fun getCountryCode(input: String): String? =
        with(Pattern.compile(REGEX_COUNTRY_CODE).matcher(input)) {
            if (find()) {
                group(0)
            } else null
        }
}