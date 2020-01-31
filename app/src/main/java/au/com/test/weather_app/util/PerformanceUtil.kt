package au.com.test.weather_app.util

import android.util.Log
import java.math.BigDecimal

class PerformanceUtil<T> {
    private val TAG = Logger.TAG_APP + "perf:"

    fun tick(process: () -> T): T {
        val t0 = System.nanoTime()
        val r = process.invoke()
        val diff = System.nanoTime() - t0
        Log.d(TAG, "process <$process> consumed ${BigDecimal(diff / 1000000.00).setScale(2, BigDecimal.ROUND_HALF_UP)} ms.")
        return r
    }
}