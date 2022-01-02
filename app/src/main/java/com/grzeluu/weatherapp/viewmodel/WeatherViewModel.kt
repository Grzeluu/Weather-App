package com.grzeluu.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.ApiConstants
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.util.MyResult
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.grzeluu.weatherapp.app.MyApplication


class WeatherViewModel(
    app: Application,
    val appRepository: AppRepository,
) : AndroidViewModel(app) {

    @SuppressLint("MissingPermission")
    fun getWeatherLocationData(): LiveData<MyResult<WeatherResponse>> {
        var longitude = 0.0
        var latitude = 0.0

        //TODO get current location

        return appRepository.getWeather(
            latitude,
            longitude,
            ApiConstants.METRIC_UNIT,
            ApiConstants.APP_ID
        )
    }
}