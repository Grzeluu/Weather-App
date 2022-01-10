package com.grzeluu.weatherapp.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grzeluu.weatherapp.model.City

@Database(
    entities = [City::class],
    version = 1
)
abstract class CityDatabase : RoomDatabase() {

    abstract fun CityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabase(context: Context): CityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDatabase::class.java,
                    "room_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}