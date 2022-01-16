package com.grzeluu.weatherapp.source

import com.grzeluu.weatherapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    @GET("2.5/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
        @Query("exclude") exclude: String?
    ): Response<WeatherResponse>

    @GET("2.5/weather")
    suspend fun getCurrentLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
    ): Response<WeatherResponse>
}