package au.com.test.weather_app.home

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.common.BaseActivity
import au.com.test.weather_app.util.GlideApp
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_weather_big.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : BaseActivity(), MainActivityView {
    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        layoutSearchbar.setOnSearchButtonClick {
            presenter.search(it)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        presenter.clear()
        super.onPause()
    }

    override fun onCurrentWeatherUpdate(data: WeatherData) {
        GlobalScope.launch(Dispatchers.Main) {
            val iconUrl = String.format(
                LocalProperties.Network.API_WEATHER_ICON_URL, data.icon
            )
            GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

            txtMain.text = data.main
            txtDescription.text = data.description
            txtTemperature.text = data.temperature.toString()
            txtHumidity.text = data.humidity.toString()
            txtWind.text = data.windSpeed.toString() + "/" + data.windSpeed
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("aaaa", "code: $keyCode")

        return super.onKeyDown(keyCode, event)
    }

    override fun getContainerId(): Int = R.id.layoutContainer
}