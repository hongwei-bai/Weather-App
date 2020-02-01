package au.com.test.weather_app.home

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.di.common.BaseActivity


@SuppressLint("MissingPermission")
internal fun LocationManager.subscribeCurrentLocation(action: (location: Location) -> Unit) {
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

internal fun BaseActivity.hideKeyboard() {
    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}