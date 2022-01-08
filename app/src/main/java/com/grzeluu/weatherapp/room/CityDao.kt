package com.grzeluu.weatherapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.grzeluu.weatherapp.model.City

@Dao
interface CityDao {

    @Insert
    fun insert(vararg city: City)

    @Query("SELECT * FROM cities WHERE name = :regex || '%' ")
    fun getMatchingCities(regex: String): LiveData<List<City>>
}