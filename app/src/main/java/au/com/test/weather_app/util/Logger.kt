package au.com.test.weather_app.util

import android.util.Log
import au.com.test.weather_app.LocalProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Logger @Inject constructor() {

    companion object {
        // Short for Weather-App. Easy for filtering Weather-App logs.
        const val TAG_APP = "wa:"
    }

    fun d(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.d(TAG_APP + tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.i(TAG_APP + tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.w(TAG_APP + tag, message)
        }
    }

    fun e(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.e(tag, message)
        }
    }
}