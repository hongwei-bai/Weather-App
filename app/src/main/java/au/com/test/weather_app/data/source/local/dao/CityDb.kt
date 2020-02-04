package au.com.test.weather_app.data.source.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import au.com.test.weather_app.data.domain.entities.CityData

@Database(entities = [CityData::class], version = 1)
abstract class CityDb : RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {
        private var instance: CityDb? = null
        @Synchronized
        fun get(context: Context): CityDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDb::class.java, "CityDatabase"
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
