package au.com.test.weather_app.di.components

import au.com.test.weather_app.di.modules.ApplicationModule
import au.com.test.weather_app.di.modules.NetworkModule
import au.com.test.weather_app.di.modules.SingletonModule
import au.com.test.weather_app.WeatherApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, SingletonModule::class, NetworkModule::class])
interface ApplicationComponent: Singletons {

    fun inject(application: WeatherApp)
}