package au.com.test.weather_app.di.components

import android.content.Context
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.source.cache.Cache
import au.com.test.weather_app.data.source.local.owm.LocalOpenWeatherMapDataSource
import au.com.test.weather_app.data.source.remote.owm.OpenWeatherMapDataSource
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.util.Logger

interface Singletons {
    @AppContext
    fun provideContext(): Context

    fun provideWeatherRepository(): WeatherRepository

    fun provideOpenWeatherMapDataSource(): OpenWeatherMapDataSource

    fun provideLocalOpenWeatherMapDataSource(): LocalOpenWeatherMapDataSource

    fun provideCache(): Cache

    fun provideLogger(): Logger
}