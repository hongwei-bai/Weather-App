package au.com.test.weather_app.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import kotlinx.android.synthetic.main.layout_weather_main.view.*
import kotlin.math.roundToInt


class WeatherView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        initialize(context)
    }

    fun update(data: WeatherData) {
        val iconUrl = String.format(
            LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
        )
        GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

        txtMain.text = data.weather
        txtDescription.text = data.weatherDescription
        txtTemperature.text = resources.getString(
            R.string.celsius,
            TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt()
        )
        txtHumidity.text = resources.getString(R.string.humidity, data.humidity)
        txtWind.text = resources.getString(R.string.wind_speed, data.windSpeed.toString())
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_weather_main, this)
    }
}