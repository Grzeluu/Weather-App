package com.grzeluu.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.ApiConstants
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.util.MyResult
import androidx.lifecycle.LiveData

import com.grzeluu.weatherapp.repository.LocationLiveData

class WeatherViewModel(
    application: Application,
    val appRepository: AppRepository,
) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun refreshWeather() = locationData.refresh()

    fun getLocationData() = locationData

    fun getWeatherLocationData(
        latitude: Double,
        longitude: Double
    ): LiveData<MyResult<WeatherResponse>> {
        Log.i("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "")
        return appRepository.getWeather(
            latitude,
            longitude,
            ApiConstants.METRIC_UNIT,
            ApiConstants.APP_ID
        )
    }
}