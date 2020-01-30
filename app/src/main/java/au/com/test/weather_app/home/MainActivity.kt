package au.com.test.weather_app.home

import android.os.Bundle
import au.com.test.weather_app.R
import au.com.test.weather_app.di.common.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity(), MainActivityView {
    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        presenter.test()
    }

    override fun onPause() {
        presenter.clear()
        super.onPause()
    }

    override fun getContainerId(): Int = R.id.layoutContainer
}