package au.com.test.weather_app.di

import android.app.Application
import android.content.Context
import au.com.test.weather_app.WeatherApp
import au.com.test.weather_app.di.activity.WeatherAppActivityModule
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class, WeatherAppActivityModule::class])
abstract class WeatherAppModule {
    @Binds
    abstract fun application(app: WeatherApp): Application

    @Binds
    abstract fun applicationContext(app: WeatherApp): Context
}