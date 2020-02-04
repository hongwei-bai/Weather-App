package au.com.test.weather_app.data

import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.domain.entities.CityData


interface CityRepository {
    fun readCityList(): List<CityData>

    fun writeCityList(list: List<CityData>)

    fun getCityCount(): Long

    fun lookupCity(city: String, limit: Int = LocalProperties.Local.SEARCH_LIST_LIMIT): List<CityData>
}