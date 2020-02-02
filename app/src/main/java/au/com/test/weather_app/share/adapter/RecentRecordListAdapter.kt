package au.com.test.weather_app.share.adapter

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
import au.com.test.weather_app.share.adapter.RecentRecordListAdapter.RecentRecordItemHolder
import au.com.test.weather_app.util.DateUtil
import au.com.test.weather_app.util.GlideApp
import au.com.test.weather_app.util.TemperatureUtil
import au.com.test.weather_app.util.gone
import au.com.test.weather_app.util.hide
import au.com.test.weather_app.util.show
import kotlinx.android.synthetic.main.layout_weather_item.view.*
import kotlin.math.roundToInt

class RecentRecordListAdapter(private val context: Context) :
    PagedListAdapter<WeatherData, RecentRecordItemHolder>(diffCallback) {
    private var onItemClickListener: ((position: Int, data: WeatherData) -> Unit)? = null

    private var onItemDeleteClickListener: ((position: Int, data: WeatherData) -> Unit)? = null

    private var onItemSelectListener: ((position: Int, data: WeatherData, isSelect: Boolean) -> Unit)? = null

    var data: List<WeatherData> = emptyList()

    var workMode: WorkMode = WorkMode.Default
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        holder.bind(data[position], position, workMode, onItemClickListener, onItemDeleteClickListener, onItemSelectListener)

    fun setOnItemClickListener(listener: (Int, WeatherData) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemDeleteClickListener(listener: (Int, WeatherData) -> Unit) {
        onItemDeleteClickListener = listener
    }

    fun setOnItemSelectClickListener(listener: (Int, WeatherData, Boolean) -> Unit) {
        onItemSelectListener = listener
    }

    class RecentRecordItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            data: WeatherData,
            position: Int,
            workMode: WorkMode,
            clickListener: ((Int, WeatherData) -> Unit)?,
            deleteListener: ((Int, WeatherData) -> Unit)?,
            selectListener: ((Int, WeatherData, Boolean) -> Unit)?
        ) {
            with(itemView) {
                val iconUrl = String.format(
                    LocalProperties.Network.API_WEATHER_ICON_URL, data.weatherIcon
                )
                GlideApp.with(imgIcon).load(iconUrl).into(imgIcon)

                if (data.cityName != null) {
                    txtTitle.text = data.cityName
                    txtTitle.show()
                    imgGpsIcon.gone()
                    txtGpsLocation.gone()
                } else {
                    txtTitle.text = ""
                    txtTitle.hide()
                    imgGpsIcon.show()
                    txtGpsLocation.show()
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

                when (workMode) {
                    WorkMode.Default -> {
                        imgDelete.gone()
                        imgCheckBox.gone()
                    }
                    WorkMode.Delete -> {
                        imgDelete.show()
                        imgCheckBox.gone()
                    }
                    WorkMode.MultipleDelete -> {
                        imgDelete.gone()
                        imgCheckBox.show()
                    }
                }

                setOnItemClickListener(position, data, clickListener, deleteListener, selectListener)
            }
        }

        private fun setOnItemClickListener(
            position: Int,
            data: WeatherData,
            clickListener: ((Int, WeatherData) -> Unit)?,
            deleteListener: ((Int, WeatherData) -> Unit)?,
            selectListener: ((Int, WeatherData, Boolean) -> Unit)?
        ) {
            itemView.setOnClickListener {
                clickListener?.invoke(position, data)
                with(it.imgCheckBox) {
                    if (visibility == View.VISIBLE) {
                        isSelected = !isSelected
                        selectListener?.invoke(position, data, isSelected)
                    }
                }
            }
            itemView.imgDelete.setOnClickListener { deleteListener?.invoke(position, data) }
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