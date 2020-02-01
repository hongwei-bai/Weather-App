package au.com.test.weather_app

import java.util.*

object LocalProperties : CommonLocalProperties() {
    const val IS_LOGGING_ENABLED = true
    val LOCALE = Locale

    object Locale {
        val DEFAULT = Locale("en", "AU")
        val API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    }

    object Network {
        const val API_CONNECT_TIMEOUT = 10L
        const val API_READ_TIMEOUT = 10L
        const val API_WRITE_TIMEOUT = 10L

        const val API_BASE_URL = "https://api.openweathermap.org/"
        const val API_KEY = "9ba46ef8a53abd8397757ceb374ec4d3"
        const val API_WEATHER_ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png"
        val API_PATHS = ApiPath
    }

    object Local {
        const val CITY_LIST = "city.list.json"
    }
}