package au.com.test.weather_app.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.test.weather_app.R
import au.com.test.weather_app.data.domain.entities.CityData
import au.com.test.weather_app.home.search.SearchSuggestionListAdapter.SearchSuggestionViewHolder
import kotlinx.android.synthetic.main.layout_search_suggestion_item.view.*

class SearchSuggestionListAdapter(private val context: Context) : RecyclerView.Adapter<SearchSuggestionViewHolder>() {

    var data: List<CityData> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var clickListener: ((position: Int, city: CityData) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int, CityData) -> Unit) {
        clickListener = listener
    }

    inner class SearchSuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.txtCity.text = data[position].getCityTitle()
            itemView.setOnClickListener {
                clickListener?.invoke(position, data[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionViewHolder =
        SearchSuggestionViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_search_suggestion_item, parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SearchSuggestionViewHolder, position: Int) = holder.bind(position)
}