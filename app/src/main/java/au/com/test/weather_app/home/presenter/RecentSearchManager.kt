package au.com.test.weather_app.home.presenter

import au.com.test.weather_app.data.domain.entities.WeatherData
import java.math.BigDecimal
import javax.inject.Inject

class RecentSearchManager @Inject constructor() {
    companion object {
        private const val LOCATION_PRECISION = 4
    }

    private val indexTable: HashMap<Any, WeatherData> = hashMapOf()

    fun addRecord(data: WeatherData) {
        indexTable[getKey(data)] = data
    }

    fun getList(): List<WeatherData> = indexTable.map {
        it.value
    }.sortedByDescending {
        it.lastUpdate
    }

    private fun getKey(data: WeatherData): Any = data.cityId ?: Pair(roundTo4DecimalPlaces(data.latitude), roundTo4DecimalPlaces(data.longitude))

    private fun roundTo4DecimalPlaces(double: Double) = BigDecimal(double).setScale(LOCATION_PRECISION, BigDecimal.ROUND_HALF_UP).toDouble()

}