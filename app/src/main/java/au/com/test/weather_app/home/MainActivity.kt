package au.com.test.weather_app.home

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.share.adapter.RecentRecordListAdapter
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_searchbar.*
import kotlinx.android.synthetic.main.layout_weather_big.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_LOCATION = 444
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var recentRecordListAdapter: RecentRecordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = getViewModelProvider(this).get(MainViewModel::class.java)
        observeViewModelState()
    }

    override fun onResume() {
        super.onResume()

        with(layoutToolbar) {
            setOnSearchButtonClick { viewModel.fetch(it) }
            setOnGpsButtonClick { getWeatherForCurrentLocation() }
        }
        initializeRecyclerView()

        viewModel.go()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        getWeatherForCurrentLocation()
    }

    override fun inject() {
        DaggerActivityComponent.builder()
            .applicationComponent(getAppComponent())
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }

    private fun observeViewModelState() {
        viewModel.currentWeather.observe(this, Observer { currentWeather ->
            updateCurrentWeather(currentWeather)
            layoutToolbar.title = currentWeather.cityName ?: getString(
                R.string.unknown_location,
                currentWeather.latitude,
                currentWeather.longitude
            )
            txtTitle.clearFocus()
            hideKeyboard()
        })

        viewModel.recentRecords.observe(this, Observer { recentRecords ->
            recentRecordListAdapter.apply {
                data = recentRecords
                notifyDataSetChanged()
            }
        })
    }

    private fun initializeRecyclerView() {
        recentRecordListAdapter = RecentRecordListAdapter(this)
        recyclerRecent.layoutManager = LinearLayoutManager(this)
        recyclerRecent.adapter = recentRecordListAdapter
        recyclerRecent.addItemDecoration(DividerItemDecoration(this, VERTICAL))
    }

    private fun updateCurrentWeather(data: WeatherData) {
        val iconUrl = String.format(
            LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
        )
        GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

        txtMain.text = data.weather
        txtDescription.text = data.weatherDescription
        txtTemperature.text = getString(
            R.string.celsius,
            TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt()
        )
        txtHumidity.text = getString(R.string.humidity, data.humidity)
        txtWind.text = getString(R.string.wind_speed, data.windSpeed.toString())
        divider.show()
    }

    private fun hasLocationPermission(): Boolean =
        EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private fun getWeatherForCurrentLocation() {
        if (hasLocationPermission()) {
            try {
                (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    .subscribeCurrentLocation { viewModel.fetch(it.latitude, it.longitude) }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.permission_request_location),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}