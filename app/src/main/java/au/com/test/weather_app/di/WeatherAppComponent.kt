package au.com.test.weather_app.di

import au.com.test.weather_app.WeatherApp
import au.com.test.weather_app.di.module.DataModule
import au.com.test.weather_app.di.module.NetworkModule
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [WeatherAppModule::class, DataModule::class, NetworkModule::class])
interface WeatherAppComponent : AndroidInjector<WeatherApp> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<WeatherApp>
}