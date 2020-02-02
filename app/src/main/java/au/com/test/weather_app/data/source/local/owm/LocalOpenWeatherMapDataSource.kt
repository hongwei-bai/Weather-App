package au.com.test.weather_app.data.source.local.owm

import android.content.Context
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.di.annotations.AppContext
import au.com.test.weather_app.util.FileUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalOpenWeatherMapDataSource @Inject constructor(
    @AppContext private val context: Context
) {
    fun getCityList(): List<City> = Gson().fromJson(
        FileUtil.loadJSONFromAsset(context, LocalProperties.Local.CITY_LIST),
        object : TypeToken<List<City>>() {}.type
    )
}