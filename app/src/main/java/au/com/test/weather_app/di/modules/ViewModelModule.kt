package au.com.test.weather_app.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.test.weather_app.di.utils.ViewModelFactory
import au.com.test.weather_app.di.utils.ViewModelKey
import au.com.test.weather_app.home.MainViewModel
import au.com.test.weather_app.locationrecord.LocationRecordViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainActivityViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationRecordViewModel::class)
    internal abstract fun bindRecentLocationActivityViewModel(locationRecordViewModel: LocationRecordViewModel): ViewModel


    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
