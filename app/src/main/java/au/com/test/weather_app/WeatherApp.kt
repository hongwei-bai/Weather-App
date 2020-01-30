package au.com.test.weather_app

import au.com.test.weather_app.di.DaggerWeatherAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class WeatherApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerWeatherAppComponent.factory().create(this)
}