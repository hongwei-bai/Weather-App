package au.com.test.weather_app.uicomponents

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.Logger
import au.com.test.weather_app.util.Logger.Companion
import au.com.test.weather_app.util.TemperatureUtil
import au.com.test.weather_app.util.gone
import au.com.test.weather_app.util.hide
import au.com.test.weather_app.util.show
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.layout_weather_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    companion object {
        private val TAG = "${Logger.TAG_APP}${WeatherView::class.java.simpleName}"
    }

    private var onLoadCompleteListener: ((success: Boolean) -> Unit)? = null

    fun update(data: WeatherData) {
        txtEmpty.gone()
        val iconUrl = String.format(
            LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
        )
        GlideApp.with(imgIcon)
            .load(iconUrl)
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    showError()
                    onLoadCompleteListener?.invoke(false)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    GlobalScope.launch(Dispatchers.Main) {
                        progressBarLoading.gone()
                        layoutWeatherInfoContainer.show()
                        Log.i(TAG, "loader onResourceReady")
                    }
                    onLoadCompleteListener?.invoke(true)
                    return false
                }
            }).into(imgIcon)

        txtMain.text = data.weather
        txtDescription.text = data.weatherDescription
        txtTemperature.text = resources.getString(
            R.string.celsius,
            TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt()
        )
        txtHumidity.text = resources.getString(R.string.humidity, data.humidity)
        txtWind.text = resources.getString(R.string.wind_speed, data.windSpeed.toString())
    }

    fun showWelcome() {
        Log.i(TAG, "loader showWelcome")
        progressBarLoading.gone()
        layoutWeatherInfoContainer.hide()
        txtEmpty.show()
        txtEmpty.text = resources.getString(R.string.welcome)
    }

    fun showError() {
        Log.i(TAG, "loader showError")
        progressBarLoading.gone()
        layoutWeatherInfoContainer.hide()
        txtEmpty.show()
        txtEmpty.text = resources.getString(R.string.empty_view)
    }

    fun setLoading(isShowSpinner: Boolean = true, listener: ((Boolean) -> Unit)? = null) {
        Log.i(TAG, "loader setLoading: $isShowSpinner")
        onLoadCompleteListener = listener
        layoutWeatherInfoContainer.hide()
        txtEmpty.gone()
        progressBarLoading.show(isShowSpinner)
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_weather_main, this)
    }
}