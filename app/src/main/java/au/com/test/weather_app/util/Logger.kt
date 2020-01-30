package au.com.test.weather_app.util

import android.util.Log
import au.com.test.weather_app.LocalProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Logger @Inject constructor() {

    fun d(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.d(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.i(tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.w(tag, message)
        }
    }

    fun e(tag: String, message: String) {
        if (LocalProperties.IS_LOGGING_ENABLED) {
            Log.e(tag, message)
        }
    }
}