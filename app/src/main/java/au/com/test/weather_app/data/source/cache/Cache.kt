package au.com.test.weather_app.data.source.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Cache @Inject constructor() {
    var weather: String? = null
}