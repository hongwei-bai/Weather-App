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
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.LeftButton
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.LeftButtonOnSearchMode
import au.com.test.weather_app.components.WeatherToolBar.ToolbarButton.RightButtonOnSearchMode
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseActivity
import au.com.test.weather_app.di.components.DaggerActivityComponent
import au.com.test.weather_app.di.modules.ActivityModule
import au.com.test.weather_app.locationrecord.LocationRecordActivity
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_weather_main.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_LOCATION = 444
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var locationRecordListAdapter: LocationRecordListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = getViewModelProvider(this).get(MainViewModel::class.java)
        observeViewModelState()

        initializeRecyclerView()
        initializeToolbar()
    }

    override fun onResume() {
        super.onResume()

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

    override fun onBackPressed() {
        if (layoutToolbar.isOnSearchMode()) {
            layoutToolbar.onLostFocus()
        } else {
            super.onBackPressed()
        }
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
            currentWeather?.let {
                updateCurrentWeather(currentWeather)
                layoutToolbar.title = currentWeather.cityName ?: getString(
                    R.string.unknown_location,
                    currentWeather.latitude,
                    currentWeather.longitude
                )
                txtTitle.clearFocus()
                hideKeyboard()
            } ?: layoutToolbar.switchSearchMode(true)
        })

        viewModel.recentRecords.observe(this, Observer { recentRecords ->
            locationRecordListAdapter.apply {
                data = recentRecords
                notifyDataSetChanged()
            }
        })
    }

    private fun initializeRecyclerView() {
        locationRecordListAdapter = LocationRecordListAdapter(this)
        recyclerLocationRecord.layoutManager = LinearLayoutManager(this)
        recyclerLocationRecord.adapter = locationRecordListAdapter
        recyclerLocationRecord.addItemDecoration(DividerItemDecoration(this, VERTICAL))
    }

    private fun initializeToolbar() {
        with(layoutToolbar) {
            isEnableSearch = true
            leftIcon = R.drawable.selector_edit
            leftIconOnSearchMode = R.drawable.selector_gps
            rightIconOnSearchMode = R.drawable.selector_arrow_forward
            setOnButtonClick { button, input, _ ->
                when (button) {
                    LeftButton -> startActivity(LocationRecordActivity.intent(context))
                    LeftButtonOnSearchMode -> getWeatherForCurrentLocation()
                    RightButtonOnSearchMode -> viewModel.fetch(input)
                    else -> {
                        // Do nothing
                    }
                }
            }
        }
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