package com.grzeluu.weatherapp.repository

import android.util.Log
import com.grzeluu.weatherapp.model.Hourly
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.RetrofitInstance
import com.grzeluu.weatherapp.network.WeatherService
import retrofit2.Response

class AppRepository {

    var client: WeatherService = RetrofitInstance.weatherApi

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        appid: String,
        exclude: String
    ): Response<WeatherResponse> {
        val response =  client.getWeather(lat, lon, units, appid, exclude)
        Log.i("Response", response.body().toString())
        return response
    }
}