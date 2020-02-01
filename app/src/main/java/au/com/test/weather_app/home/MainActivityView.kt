package au.com.test.weather_app.home

import au.com.test.weather_app.data.domain.entities.WeatherData

interface MainActivityView {
    fun onCurrentWeatherUpdate(data: WeatherData)

    fun onRecentRecordListUpdate(list: List<WeatherData>)
}