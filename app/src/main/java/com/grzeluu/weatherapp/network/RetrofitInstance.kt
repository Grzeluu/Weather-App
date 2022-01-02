package com.grzeluu.weatherapp.network

import com.grzeluu.weatherapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofitWeather by lazy {
            Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val weatherApi by lazy {
            retrofitWeather.create(WeatherService::class.java)
        }
    }
}