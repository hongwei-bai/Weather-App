package au.com.test.weather_app.data.source.local.owm

import android.content.Context
import au.com.test.weather_app.LocalProperties
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.util.FileUtil
import au.com.test.weather_app.util.PerformanceUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

open class LocalOpenWeatherMapDataSource @Inject constructor(
    private val context: Context
) {
    fun getCityList(): Observable<List<City>> {
        return Observable.create {
            it.apply {
                onNext(
                    PerformanceUtil<List<City>>().tick {
                        Gson().fromJson(
                            FileUtil.loadJSONFromAsset(context, LocalProperties.Local.CITY_LIST),
                            object : TypeToken<List<City>>() {}.type
                        )
                    }
                )
                onComplete()
            }
        }
    }
}