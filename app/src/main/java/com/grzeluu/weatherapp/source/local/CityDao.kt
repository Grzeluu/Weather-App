package com.grzeluu.weatherapp.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg city: DBCity)

    @Query("SELECT * FROM city_data_table WHERE city_name = :regex || '%' ")
    fun getMatchingCities(regex: String): List<DBCity>

    @Query("SELECT * FROM city_data_table ORDER BY ((lat-:lat)*(lat-:lat)) + ((lon - :lon)*(lon - :lon)) ASC LIMIT 1")
    fun getNearestCity(lat: Double, lon: Double): DBCity
}