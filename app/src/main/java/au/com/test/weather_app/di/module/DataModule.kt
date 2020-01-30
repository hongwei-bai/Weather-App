package au.com.test.weather_app.di.module

import au.com.test.weather_app.data.WeatherManager
import au.com.test.weather_app.data.WeatherRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindWeatherRepository(weatherRepository: WeatherManager): WeatherRepository
}