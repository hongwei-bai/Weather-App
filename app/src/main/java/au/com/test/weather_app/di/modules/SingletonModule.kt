package au.com.test.weather_app.di.modules

import au.com.test.weather_app.data.CityManager
import au.com.test.weather_app.data.CityRepository
import au.com.test.weather_app.data.WeatherManager
import au.com.test.weather_app.data.WeatherRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SingletonModule {
    @Binds
    @Singleton
    fun bindWeatherRepository(weatherManager: WeatherManager): WeatherRepository

    @Binds
    @Singleton
    fun bindCityRepository(cityManager: CityManager): CityRepository
}