package au.com.test.weather_app.data.source.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import au.com.test.weather_app.data.domain.entities.WeatherData

@Database(entities = [WeatherData::class], version = 1)
abstract class WeatherDb : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        private var instance: WeatherDb? = null
        @Synchronized
        fun get(context: Context): WeatherDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDb::class.java, "WeatherDatabase"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            // do nothing
                        }
                    }).build()
            }
            return instance!!
        }
    }
}
