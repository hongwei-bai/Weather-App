package au.com.test.weather_app.home

import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) {
    companion object {
        private val TAG = MainActivityPresenter::class.java.simpleName
    }

    private val disposables = CompositeDisposable()

    fun test() {
        logger.d(TAG, "hello world")

        disposables.add(
            weatherRepository.fetchWeatherData("London").subscribe({
                logger.d(TAG, "weather: $it")
            }, {
                logger.w(TAG, "subscribe fetchWeatherData onError: ${it.localizedMessage}")
            })
        )

    }

    fun clear() {
        disposables.clear()
    }
}