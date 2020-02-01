package au.com.test.weather_app.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


class MainActivity : BaseActivity(), MainActivityView, LocationListener {

    companion object {
        private const val REQUEST_CODE_LOCATION = 444
    }

    @Inject
    lateinit var presenter: MainActivityPresenter

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        with(layoutSearchbar) {
            setOnSearchButtonClick { presenter.fetch(it) }
            setOnGpsButtonClick { getWeatherForCurrentLocation() }
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
            txtWind.text = data.windSpeed.toString() + "/" + data.windDegree
        }
    }

    override fun getContainerId(): Int = R.id.layoutContainer

    override fun onLocationChanged(p0: Location?) {
        Log.i("wa aaaa", "onLocationChanged $p0")
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
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

    private fun hasLocationPermission(): Boolean =
        EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private fun getWeatherForCurrentLocation() {
        if (hasLocationPermission()) {
            try {
                (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    .subscribeCurrentLocation {
                        presenter.fetch(it.latitude, it.longitude)
                    }
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

@SuppressLint("MissingPermission")
private fun LocationManager.subscribeCurrentLocation(action: (location: Location) -> Unit) {
    requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        LocalProperties.Config.LOCATION_REQUEST_MIN_TIME,
        LocalProperties.Config.LOCATION_REQUEST_MIN_DISTANCE,
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                action.invoke(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extra: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }
    )
}