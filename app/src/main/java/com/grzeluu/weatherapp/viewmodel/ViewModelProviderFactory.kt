package com.grzeluu.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grzeluu.weatherapp.ui.weather.WeatherViewModel

class ViewModelProviderFactory(
    val app: Application,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}