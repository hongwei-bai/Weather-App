package au.com.test.weather_app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.home.adapter.RecentRecordListAdapter.RecentRecordItemHolder
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import kotlinx.android.synthetic.main.layout_weather_item.view.*
import kotlin.math.roundToInt

class RecentRecordListAdapter(private val context: Context) : RecyclerView.Adapter<RecentRecordItemHolder>() {
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

                txtTitle.text = data.getTitle(context)
                txtMain.text = data.weather
                txtTemperature.text = resources.getString(R.string.celsius, TemperatureUtil.kalvinToCelsius(data.temperature).roundToInt())

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
}