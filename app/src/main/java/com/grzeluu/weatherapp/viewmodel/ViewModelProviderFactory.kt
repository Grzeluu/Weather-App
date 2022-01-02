package com.grzeluu.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.grzeluu.weatherapp.repository.AppRepository

class ViewModelProviderFactory(
    val app: Application,
    val appRepository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(app, appRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}