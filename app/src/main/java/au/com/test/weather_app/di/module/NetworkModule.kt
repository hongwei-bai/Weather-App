package au.com.test.weather_app.di.module

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.remote.owm.services.WeatherService
import au.com.test.weather_app.util.UtcDateAdapter
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

        if (LocalProperties.IS_LOGGING_ENABLED) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(LocalProperties.Network.API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(LocalProperties.Network.API_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(LocalProperties.Network.API_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cache(null)

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = buildRetrofit(okHttpClient)

    private fun buildRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val gson = GsonBuilder().registerTypeAdapter(
            Date::class.java,
            UtcDateAdapter()
        ).create()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(LocalProperties.Network.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherService(retrofit: Retrofit) =
        retrofit.create(WeatherService::class.java)
}
