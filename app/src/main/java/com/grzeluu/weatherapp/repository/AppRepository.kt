package com.grzeluu.weatherapp.repository

import android.app.Application
import android.util.Log
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.source.local.CityDao
import com.grzeluu.weatherapp.source.local.CityDatabase
import com.grzeluu.weatherapp.source.local.DBCity
import com.grzeluu.weatherapp.source.network.RetrofitInstance
import com.grzeluu.weatherapp.source.network.WeatherService
import retrofit2.Response

class AppRepository(application: Application) {

    val client: WeatherService = RetrofitInstance.weatherApi
    val cityDao: CityDao = CityDatabase.getDatabase(application).CityDao()

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        appid: String,
        exclude: String
    ): Response<WeatherResponse> {
        val response = client.getWeather(lat, lon, units, appid, exclude)
        Log.i("Response", response.body().toString())
        return response
    }

    suspend fun getNearestCity(
        lat: Double,
        lon: Double
    ): DBCity {

        val city = cityDao.getNearestCity(lat, lon)
        return city
    }
}