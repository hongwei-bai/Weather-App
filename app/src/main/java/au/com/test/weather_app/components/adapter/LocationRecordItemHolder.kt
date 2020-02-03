package au.com.test.weather_app.components.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.Default
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.Delete
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.MultipleDelete
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.util.DateUtil
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import au.com.test.weather_app.util.gone
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.layout_weather_item.view.*
import kotlin.math.roundToInt

class LocationRecordItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        data: WeatherData,
        position: Int,
        workMode: WorkMode,
        clickListener: ((Int, WeatherData) -> Unit)?,
        deleteListener: ((Int, WeatherData) -> Unit)?,
        selectListener: ((Int, WeatherData, Boolean) -> Unit)?,
        itemSelectStatus: Array<Boolean>
    ) {
        with(itemView) {
            val iconUrl = String.format(
                LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
            )
            GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

            txtTitle.text = data.getCityTitle() ?: ""
            data.isGpsCoordinate().let { isGpsCoordinate ->
                txtTitle.show(!isGpsCoordinate)
                imgGpsIcon.show(isGpsCoordinate)
                txtGpsLocation.show(isGpsCoordinate)
                if (isGpsCoordinate) {
                    txtGpsLocation.text = itemView.resources.getString(R.string.gps_location, data.latitude, data.longitude)
                }
            }
            txtMain.text = data.weather
            txtTemperature.text = resources.getString(
                R.string.celsius,
                TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt()
            )
            txtLastUpdate.text = DateUtil.getSmartTimeString(itemView.context, data.lastUpdate)

            when (workMode) {
                Default -> {
                    imgDelete.gone()
                    imgCheckBox.gone()
                }
                Delete -> {
                    imgDelete.show()
                    imgCheckBox.gone()
                }
                MultipleDelete -> {
                    imgDelete.gone()
                    imgCheckBox.show()
                    imgCheckBox.isSelected = itemSelectStatus[position]
                }
            }

            setOnItemClickListener(position, data, clickListener, deleteListener, selectListener, itemSelectStatus)
        }
    }

    private fun setOnItemClickListener(
        position: Int,
        data: WeatherData,
        clickListener: ((Int, WeatherData) -> Unit)?,
        deleteListener: ((Int, WeatherData) -> Unit)?,
        selectListener: ((Int, WeatherData, Boolean) -> Unit)?,
        itemSelectStatus: Array<Boolean>
    ) {
        itemView.setOnClickListener {
            with(it.imgCheckBox) {
                if (visibility == View.VISIBLE) {
                    itemSelectStatus[position] = !itemSelectStatus[position]
                    isSelected = itemSelectStatus[position]
                    selectListener?.invoke(position, data, isSelected)
                }
            }
            clickListener?.invoke(position, data)
        }
        itemView.imgDelete.setOnClickListener { deleteListener?.invoke(position, data) }
    }
}