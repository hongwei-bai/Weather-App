package au.com.test.weather_app.data.source.remote.owm.interceptors

import android.util.Log
import au.com.test.weather_app.LocalProperties
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class OpenWeatherMapInterceptor @Inject constructor() : Interceptor {

    companion object {
        private val TAG = OpenWeatherMapInterceptor::class.java.simpleName

        private const val GET = "GET"
        private const val KEY_API_KEY = "APPID"
    }

    override fun intercept(chain: Interceptor.Chain?): Response? {
        var request = chain?.request()
        request = handlerRequest(request!!)
        return chain?.proceed(request)
    }

    private fun handlerRequest(request: Request): Request =
        when (request.method()) {
            GET -> handlerGetRequest(request)
            else -> request
        }

    private fun handlerGetRequest(request: Request): Request {
        return request.newBuilder()
            .url(appendCredential(request.url()))
            .build()
    }

    private fun appendCredential(url: HttpUrl): String =
        StringBuilder(url.toString()).apply {
            append("&")
            append(KEY_API_KEY)
            append("=")
            append(LocalProperties.Network.API_KEY)
        }.toString()
}