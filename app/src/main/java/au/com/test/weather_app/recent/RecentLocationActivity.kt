package au.com.test.weather_app.recent

import android.os.Bundle
import au.com.test.weather_app.R
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import javax.inject.Inject

class RecentLocationActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: RecentLocationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = getViewModelProvider(this).get(RecentLocationActivityViewModel::class.java)
    }

    override fun inject() {
        DaggerActivityComponent.builder()
            .applicationComponent(getAppComponent())
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }
}