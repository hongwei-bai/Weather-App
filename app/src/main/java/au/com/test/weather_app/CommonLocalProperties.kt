package au.com.test.weather_app

import java.util.*

abstract class CommonLocalProperties {
    class ApiPath {
        companion object {
            const val WEATHER = "data/2.5/weather"
        }
    }

    class Locale {
        companion object {
            val DEFAULT = Locale("en", "AU")
            val API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        }
    }
}