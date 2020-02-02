package au.com.test.weather_app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.home.adapter.RecentRecordListAdapter.RecentRecordItemHolder
import au.com.test.weather_app.util.DateUtil
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import kotlinx.android.synthetic.main.layout_weather_item.view.*
import kotlin.math.roundToInt

class RecentRecordListAdapter(private val context: Context) :
    PagedListAdapter<WeatherData, RecentRecordItemHolder>(diffCallback) {
    private var onItemClickListener: ((Int, WeatherData) -> Unit)? = null

    var data: List<WeatherData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentRecordItemHolder =
        RecentRecordItemHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_weather_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecentRecordItemHolder, position: Int) =
        holder.bind(data[position], position, onItemClickListener)

    fun setOnItemClickListener(listener: (Int, WeatherData) -> Unit) {
        onItemClickListener = listener
    }

    class RecentRecordItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: WeatherData, position: Int, listener: ((Int, WeatherData) -> Unit)?) {
            with(itemView) {
                val iconUrl = String.format(
                    LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
                )
                GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

                if (data.cityName != null) {
                    txtTitle.text = data.cityName
                    txtTitle.visibility = View.VISIBLE
                    imgGpsIcon.visibility = View.GONE
                    txtGpsLocation.visibility = View.GONE
                } else {
                    txtTitle.text = ""
                    txtTitle.visibility = View.INVISIBLE
                    imgGpsIcon.visibility = View.VISIBLE
                    txtGpsLocation.visibility = View.VISIBLE
                    txtGpsLocation.text = itemView.resources.getString(
                        R.string.gps_location,
                        data.latitude,
                        data.longitude
                    )
                }
                txtMain.text = data.weather
                txtTemperature.text = resources.getString(
                    R.string.celsius,
                    TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt()
                )
                txtLastUpdate.text = DateUtil.getSmartTimeString(itemView.context, data.lastUpdate)

                setOnItemClickListener(position, data, listener)
            }
        }

        private fun setOnItemClickListener(
            position: Int,
            data: WeatherData,
            listener: ((Int, WeatherData) -> Unit)?
        ) {
            itemView.setOnClickListener {
                listener?.invoke(position, data)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<WeatherData>() {
            override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean =
                oldItem == newItem
        }
    }
}