package com.grzeluu.weatherapp.network

import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.util.MyResult
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("2.5/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
        @Query("exclude") exclude: String?
    ): Response<WeatherResponse>
}