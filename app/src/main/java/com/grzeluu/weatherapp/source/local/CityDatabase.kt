package com.grzeluu.weatherapp.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.grzeluu.weatherapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

import com.google.gson.reflect.TypeToken

@Database(
    entities = [DBCity::class],
    version = 1
)
abstract class CityDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): CityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDatabase::class.java,
                    "room_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(
                        CityDatabaseCallback(
                            context,
                            scope
                        )
                    ).build()
                INSTANCE = instance
                return instance
            }

        }

        private class CityDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val jsonObj = JsonParser().parse(
                            readJSONFromAsset(context)
                        ).asJsonObject

                        val cities: List<DBCity> =
                            Gson().fromJson(jsonObj, object : TypeToken<List<DBCity?>?>() {}.type)

                        populateDatabase(database, cities)
                    }
                }
            }
        }

        fun populateDatabase(database: CityDatabase, cities: List<DBCity>) {
            val cityDao = database.cityDao()

            cityDao.deleteAll()

            for (city in cities) {
                cityDao.insert(city)
            }
        }

        fun readJSONFromAsset(context: Context): String {
            val json: String
            try {
                val inputStream: InputStream = context.resources.openRawResource(R.raw.city_names)
                json = inputStream.bufferedReader().use {
                    it.readText()
                }
            } catch (ex: Exception) {
                ex.localizedMessage
                return ""
            }
            return json
        }
    }
}