package au.com.test.weather_app.di.activity

import au.com.test.weather_app.home.MainActivity
import au.com.test.weather_app.home.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WeatherAppActivityModule {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivityInjector(): MainActivity?
}