package com.grzeluu.weatherapp.repository

import android.app.Application
import com.grzeluu.weatherapp.source.RetrofitInstance
import com.grzeluu.weatherapp.source.WeatherService

class AppRepository(
    private val application: Application,
) {
    private val client: WeatherService = RetrofitInstance.weatherApi

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        appid: String,
        exclude: String
    ) = client.getWeather(lat, lon, units, appid, exclude)

}