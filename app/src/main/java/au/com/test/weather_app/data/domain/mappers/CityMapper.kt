package au.com.test.weather_app.data.domain.mappers

import au.com.test.weather_app.data.domain.entities.CityData
import au.com.test.weather_app.data.source.local.owm.models.City

object CityMapper {
    fun mapToDomainEntities(raw: City): CityData = CityData(
        id = 0,
        name = raw.name,
        countryCode = raw.country,
        latitude = raw.coord.lat,
        longitude = raw.coord.lon,
        owmCityId = raw.id,
        searchCount = 0,
        lastSearch = 0L
    )
}