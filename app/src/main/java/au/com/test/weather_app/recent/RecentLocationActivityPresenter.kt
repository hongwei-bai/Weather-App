package au.com.test.weather_app.recent

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.util.Logger
import javax.inject.Inject

class RecentLocationPresenter @Inject constructor(
    private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) {
    companion object {
        private val TAG = RecentLocationPresenter::class.java.simpleName
    }
}