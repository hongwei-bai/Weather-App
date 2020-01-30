package au.com.test.weather_app.home

import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {
    @Binds
    abstract fun bindMainActivityView(mainActivity: MainActivity): MainActivityView
}