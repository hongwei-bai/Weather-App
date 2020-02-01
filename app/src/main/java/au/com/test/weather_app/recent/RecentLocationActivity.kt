package au.com.test.weather_app.recent

import android.os.Bundle
import au.com.test.weather_app.R
import au.com.test.weather_app.di.common.BaseActivity
import javax.inject.Inject

class RecentLocationActivity : BaseActivity(), RecentLocationActivityView {
    @Inject
    lateinit var presenter: RecentLocationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun getContainerId(): Int = R.id.layoutContainer
}