package au.com.test.weather_app

import android.app.Application
import au.com.test.weather_app.di.modules.ApplicationModule
import au.com.test.weather_app.di.components.ApplicationComponent
import au.com.test.weather_app.di.components.DaggerApplicationComponent
import javax.inject.Inject

class WeatherApp : Application() {
    @Inject
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        inject()
    }

    fun inject() {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build().inject(this)
    }
}