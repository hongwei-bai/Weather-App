package au.com.test.weather_app.recent

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.Logger
import javax.inject.Inject

class RecentLocationActivityViewModel @Inject constructor(
    @AppContext private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) : BaseViewModel() {
    companion object {
        private val TAG = RecentLocationActivityViewModel::class.java.simpleName
    }
}