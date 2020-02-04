package au.com.test.weather_app.di.components

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import au.com.test.weather_app.di.annotations.PerScreen
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.di.modules.ViewModelModule
import au.com.test.weather_app.di.annotations.ActivityContext
import au.com.test.weather_app.home.MainActivity
import au.com.test.weather_app.locationrecord.LocationRecordActivity
import dagger.Component

@PerScreen
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class, ViewModelModule::class])
interface ActivityComponent : Singletons {

    fun provideAppCompatActivity(): AppCompatActivity

    @ActivityContext
    fun provideActivityContext(): Context

    fun inject(mainActivity: MainActivity)

    fun inject(locationRecordActivity: LocationRecordActivity)
}