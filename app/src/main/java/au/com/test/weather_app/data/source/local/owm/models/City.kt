package au.com.test.weather_app.data.source.local.owm.models

import au.com.test.weather_app.data.source.remote.owm.models.Coordinate

data class City(
    val id: Long,
    val name: String,
    val country: String,
    val coord: Coordinate
)