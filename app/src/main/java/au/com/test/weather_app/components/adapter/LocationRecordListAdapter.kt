package au.com.test.weather_app.components.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import au.com.test.weather_app.R
import au.com.test.weather_app.components.adapter.LocationRecordListAdapter.WorkMode.Default
import au.com.test.weather_app.data.domain.entities.WeatherData

class LocationRecordListAdapter(private val context: Context) :
    PagedListAdapter<WeatherData, LocationRecordItemHolder>(diffCallback) {
    private var onItemClickListener: ((position: Int, data: WeatherData) -> Unit)? = null

    private var onItemDeleteClickListener: ((position: Int, data: WeatherData) -> Unit)? = null

    private var onItemSelectListener: ((position: Int, data: WeatherData, isSelect: Boolean) -> Unit)? = null

    var data: List<WeatherData> = emptyList()
        set(value) {
            field = value
            itemSelectStatus = Array(value.size) { false }
        }

    private var itemSelectStatus: Array<Boolean> = emptyArray()

    var workMode: WorkMode = Default
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationRecordItemHolder =
        LocationRecordItemHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_weather_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LocationRecordItemHolder, position: Int) =
        holder.bind(data[position], position, workMode, onItemClickListener, onItemDeleteClickListener, onItemSelectListener, itemSelectStatus)

    fun setOnItemClickListener(listener: (Int, WeatherData) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemDeleteClickListener(listener: (Int, WeatherData) -> Unit) {
        onItemDeleteClickListener = listener
    }

    fun setOnItemSelectClickListener(listener: (Int, WeatherData, Boolean) -> Unit) {
        onItemSelectListener = listener
    }

    fun selectAll() {
        for (i in itemSelectStatus.indices) {
            itemSelectStatus[i] = true
        }
        notifyDataSetChanged()
    }

    fun selectNone() {
        for (i in itemSelectStatus.indices) {
            itemSelectStatus[i] = false
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<WeatherData> = data.filterIndexed { i, _ -> itemSelectStatus[i] }

    fun isAllSelected(): Boolean = itemSelectStatus.filter { it }.size == itemSelectStatus.size

    fun getSelectedCount(): Int = itemSelectStatus.filter { it }.size

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<WeatherData>() {
            override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean =
                oldItem == newItem
        }
    }

    enum class WorkMode { Default, Delete, MultipleDelete; }
}