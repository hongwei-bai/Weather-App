package au.com.test.weather_app.data.source.remote.owm.helper

object RequestHelper {
    fun buildQueryCompositeParameter(vararg parameters: String?): String {
        parameters.filterNotNull().let { list ->
            if (list.isNotEmpty()) {
                return StringBuilder(list.first()).apply {
                    for (i in 1 until list.size) {
                        append(",")
                        append(list[i])
                    }
                }.toString()
            }
        }
        return ""
    }
}