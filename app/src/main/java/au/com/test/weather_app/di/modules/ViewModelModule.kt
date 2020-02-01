package au.com.test.weather_app.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.test.weather_app.di.utils.ViewModelFactory
import au.com.test.weather_app.di.utils.ViewModelKey
import au.com.test.weather_app.home.MainActivityViewModel
import au.com.test.weather_app.recent.RecentLocationActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecentLocationActivityViewModel::class)
    internal abstract fun bindRecentLocationActivityViewModel(recentLocationActivityViewModel: RecentLocationActivityViewModel): ViewModel


    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
