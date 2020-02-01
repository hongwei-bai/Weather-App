package au.com.test.weather_app.recent

import dagger.Binds
import dagger.Module

@Module
abstract class RecentLocationActivityModule {
    @Binds
    abstract fun bindRecentLocationActivityView(recentLocationActivity: RecentLocationActivity): RecentLocationActivityView
}