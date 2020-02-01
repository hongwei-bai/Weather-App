package au.com.test.weather_app.util

import android.content.Context
import java.io.InputStream
import java.nio.charset.Charset

object FileUtil {
    fun loadJSONFromAsset(context: Context, filename: String): String {
        val `is`: InputStream = context.assets.open(filename)
        val size: Int = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        return String(buffer, Charset.forName("UTF-8"))
    }
}