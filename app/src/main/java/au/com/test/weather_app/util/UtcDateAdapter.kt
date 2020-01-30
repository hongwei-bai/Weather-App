package au.com.test.weather_app.util

import android.annotation.SuppressLint
import au.com.test.weather_app.LocalProperties.Locale.API_TIME_FORMAT
import com.google.gson.*
import com.google.gson.internal.bind.util.ISO8601Utils
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Date adapter that parses UTC dates and converts it to a correct timezone
 *
 * Default Gson adapter parses UTC dates wrong, e.g. "2019-06-03T08:59:54Z" (UTC)
 * [DefaultDateTypeAdapter] -> 2019-06-03 08:59:54 local time
 * [UtcDateAdapter] -> 2019-06-03 18:59:54 local time
 *
 * This adapter should be registered with [GsonBuilder]
 */
class UtcDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    private val dateFormats: MutableList<DateFormat> = ArrayList()
    @SuppressLint("SimpleDateFormat")
    private val utcDateFormat = SimpleDateFormat(API_TIME_FORMAT)

    init {
        utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT))
        dateFormats.add(utcDateFormat)
    }

    @Synchronized
    override fun serialize(
        date: Date,
        type: Type,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(utcDateFormat.format(date))
    }

    @Synchronized
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): Date? {
        var deserializedDate: Date? = null
        if (jsonElement.asString.isNotEmpty()) {
            synchronized(dateFormats) {
                for (dateFormat in dateFormats) {
                    try {
                        deserializedDate = dateFormat.parse(jsonElement.asString)
                    } catch (ignored: ParseException) {
                        // ignore failed patterns
                    }

                    try {
                        deserializedDate =
                            ISO8601Utils.parse(jsonElement.asString, ParsePosition(0))
                    } catch (e: ParseException) {
                        throw JsonSyntaxException(jsonElement.asString, e)
                    }
                }
            }
        }

        return deserializedDate
    }
}