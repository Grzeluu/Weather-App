package com.grzeluu.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grzeluu.weatherapp.source.network.ApiConstants
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.util.MyResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grzeluu.weatherapp.app.MyApplication
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.source.local.DBCity

import com.grzeluu.weatherapp.util.LocationLiveData
import com.grzeluu.weatherapp.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    application: Application,
    val appRepository: AppRepository,
) : AndroidViewModel(application) {

    val locationData = LocationLiveData(application)
    val weatherData: MutableLiveData<MyResult<WeatherResponse>> = MutableLiveData()
    val nearestCity: MutableLiveData<DBCity> = MutableLiveData()

    fun refreshWeather() = locationData.locationUpdate()

    fun getNearestCity(lat: Double, lon: Double) {
        viewModelScope.launch (Dispatchers.IO) {
            //nearestCity.postValue(appRepository.getNearestCity(lat, lon))
        }
    }

    fun getWeather(
        lat: Double,
        lon: Double
    ) = viewModelScope.launch {
        fetchWeather(lat, lon, ApiConstants.METRIC_UNIT, ApiConstants.APP_ID, ApiConstants.EXCLUDE)
    }

    private suspend fun fetchWeather(
        lat: Double,
        lon: Double,
        units: String,
        appid: String,
        exclude: String
    ) {
        weatherData.postValue(MyResult.Loading())
        try {
            if (NetworkUtils.isNetworkAvailable(getApplication<MyApplication>())) {
                val response = appRepository.getWeather(lat, lon, units, appid, exclude)
                weatherData.postValue(handleWeatherResponse(response))
            } else {
                weatherData.postValue(MyResult.Error("Network unavailable"))
            }
        } catch (t: Throwable) {
            weatherData.postValue(MyResult.Error(t.message.toString()))
        }
    }
}

private fun handleWeatherResponse(response: Response<WeatherResponse>): MyResult<WeatherResponse> {
    if (response.isSuccessful) {
        response.body()?.let { resultResponse ->
            return MyResult.Success(resultResponse)
        }
    }
    return MyResult.Error(response.message())
}