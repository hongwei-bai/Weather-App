package au.com.test.weather_app.recent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.data.source.local.dao.model.WeatherDb
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.Logger
import javax.inject.Inject

class LocationRecordViewModel @Inject constructor(
    @AppContext private val context: Context,
    private val weatherRepository: WeatherRepository,
    private val logger: Logger
) : BaseViewModel() {
    companion object {
        private val TAG = LocationRecordViewModel::class.java.simpleName
    }

    private val weatherDao = WeatherDb.get(context).weatherDao()

    val recentRecords: LiveData<PagedList<WeatherData>> = weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    fun go() {

    }

    fun delete(data: WeatherData) {

    }

}