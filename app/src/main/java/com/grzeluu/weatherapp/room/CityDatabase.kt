package com.grzeluu.weatherapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grzeluu.weatherapp.model.City

@Database(entities = [City::class], version = 1)
abstract class CityDatabase : RoomDatabase() {

    abstract fun CityDao(): CityDao

    companion object {
        private var instance: CityDatabase? = null

        fun getInstance(context: Context): CityDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    CityDatabase::class.java,
                    "cities")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}