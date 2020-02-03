package au.com.test.weather_app.locationrecord

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import au.com.test.weather_app.data.WeatherRepository
import au.com.test.weather_app.data.domain.entities.WeatherData
import au.com.test.weather_app.di.base.BaseViewModel
import au.com.test.weather_app.util.CoroutineContextProvider
import au.com.test.weather_app.util.Logger
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRecordViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val logger: Logger,
    private val contextProvider: CoroutineContextProvider
) : BaseViewModel() {
    companion object {
        private val TAG = LocationRecordViewModel::class.java.simpleName
    }

    var recentRecords: LiveData<PagedList<WeatherData>> =
        weatherRepository.getAllLocationRecordsSortByLatestUpdate()

    fun go() {

    }

    fun delete(data: WeatherData) =
        uiScope.launch(contextProvider.IO) {
            logger.i(TAG, "delete record: $data")
            weatherRepository.deleteLocationRecord(data)
        }

    fun delete(list: List<WeatherData>) =
        uiScope.launch(contextProvider.IO) {
            logger.w(TAG, "batch delete records (size: ${list.size}): $list")
            weatherRepository.deleteLocationRecords(list)
        }
}