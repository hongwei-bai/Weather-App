@file:Suppress("DEPRECATION")

package au.com.test.weather_app.util

import android.content.Context
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    internal const val DATE_FORMAT = "d MMM yyyy HH:mm"

    const val ONE_MINUTE = 60 * 1000L
    const val ONE_HOUR = 60 * 60 * 1000L
    const val ONE_DAY = 24 * 60 * 60 * 1000L

    fun getSmartTimeString(
        context: Context,
        time: Long,
        diff: Long = System.currentTimeMillis() - time
    ): String =
        when (diff) {
            in 0 until ONE_MINUTE -> context.resources.getString(R.string.just_now)
            in ONE_MINUTE until ONE_HOUR -> context.resources.getString(
                R.string.minutes_ago,
                (diff / ONE_MINUTE).toInt()
            )
            in ONE_HOUR until ONE_DAY -> context.resources.getString(
                R.string.hours_ago,
                (diff / ONE_HOUR).toInt()
            )
            in ONE_DAY until ONE_DAY * 30 -> context.resources.getString(
                R.string.days_ago,
                (diff / ONE_DAY).toInt()
            )
            else -> Date(time).toLocalString()
        }
}

fun Date.toLocalString(): String =
    SimpleDateFormat(DateUtil.DATE_FORMAT, LocalProperties.Locale.DEFAULT).format(this).replace(
        ".",
        ""
    )
