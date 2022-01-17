package com.grzeluu.weatherapp.repository

import android.app.Application
import com.grzeluu.weatherapp.source.RetrofitInstance
import com.grzeluu.weatherapp.source.OpenWeatherService

class WeatherRepository(
    private val application: Application
) {
    private val client: OpenWeatherService = RetrofitInstance.weatherApi

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        appid: String,
        exclude: String
    ) = client.getWeather(lat, lon, units, appid, exclude)

    suspend fun getCurrentCity(
        lat: Double,
        lon: Double,
        appid: String,
    ) = client.getCurrentLocation(lat, lon, appid)

}