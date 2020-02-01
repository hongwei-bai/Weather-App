package au.com.test.weather_app.di.modules

import android.app.Application
import android.content.Context
import au.com.test.weather_app.di.annotations.AppContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    @AppContext
    fun provideContext(): Context = application
}