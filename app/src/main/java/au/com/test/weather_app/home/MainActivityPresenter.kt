package au.com.test.weather_app.home

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.util.FileUtil
import au.com.test.weather_app.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) {
    companion object {
        private val TAG = MainActivityPresenter::class.java.simpleName
    }

    private val disposables = CompositeDisposable()

    fun test() {
        logger.d(TAG, "hello world")

//        disposables.add(
//            weatherRepository.queryWeatherData("London", "GB").subscribe({
//                logger.d(TAG, "weather: $it")
//            }, {
//                logger.w(TAG, "subscribe fetchWeatherData onError: ${it.localizedMessage}")
//            })
//        )

//        disposables.add(
//            weatherRepository.queryWeatherById(1502).subscribe({
//                logger.d(TAG, "queryWeatherById 1502 -> weather: $it")
//            }, {
//                logger.w(TAG, "subscribe fetchWeatherData onError: ${it.localizedMessage}")
//            })
//        )

//        disposables.add(
//            weatherRepository.queryWeatherById(2643743).subscribe({
//                logger.d(TAG, "queryWeatherById 2643743 -> weather: $it")
//            }, {
//                logger.w(TAG, "subscribe fetchWeatherData onError: ${it.localizedMessage}")
//            })
//        )

        disposables.add(
            weatherRepository.getCityList().subscribe({
                logger.d(TAG, "getCityList: ${it.size}")
            }, {
                logger.w(TAG, "getCityList onError: ${it.localizedMessage}")
            })
        )
    }

    fun clear() {
        disposables.clear()
    }
}