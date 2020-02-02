package au.com.test.weather_app.di.modules

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.remote.owm.services.WeatherService
import au.com.test.weather_app.util.Logger
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        private val TAG = Logger.TAG_APP + "OkHttp"
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(
        logger: Logger
    ): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor { logger.i(TAG, it) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

        if (LocalProperties.IS_LOGGING_ENABLED) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideApiInterceptor(): Interceptor =
        Interceptor { chain ->
            val url = chain.request().url().newBuilder().addQueryParameter(LocalProperties.Network.KEY_API_KEY, LocalProperties.Network.API_KEY).build()
            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }

    @Singleton
    @Provides
    fun provideOkHttp(
        apiInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(apiInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(LocalProperties.Network.API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(LocalProperties.Network.API_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(LocalProperties.Network.API_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cache(null)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit = buildRetrofit(okHttpClient, converterFactory)

    private fun buildRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(LocalProperties.Network.API_BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherService(retrofit: Retrofit) =
        retrofit.create(WeatherService::class.java)
}
