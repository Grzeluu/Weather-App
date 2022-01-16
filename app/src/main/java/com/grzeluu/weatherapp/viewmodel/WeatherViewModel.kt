package com.grzeluu.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grzeluu.weatherapp.source.ApiConstants
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.util.MyResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grzeluu.weatherapp.app.MyApplication
import com.grzeluu.weatherapp.model.Coord
import com.grzeluu.weatherapp.model.CurrentCityResponse
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.util.LocationLiveData
import com.grzeluu.weatherapp.util.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository(application)

    val locationData = LocationLiveData(application)
    val weatherData: MutableLiveData<MyResult<Pair<WeatherResponse, CurrentCityResponse>>> =
        MutableLiveData()

    fun refreshWeather() = locationData.locationUpdate()

    fun getWeather(coord: Coord) = viewModelScope.launch {
        fetchWeather(
            coord.lat,
            coord.lon,
            ApiConstants.METRIC_UNIT,
            ApiConstants.APP_ID,
            ApiConstants.EXCLUDE
        )
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
                val weatherResponse = repository.getWeather(lat, lon, units, appid, exclude)
                val cityResponse = repository.getCurrentCity(lat, lon, appid)
                weatherData.postValue(handlePairResponse(weatherResponse, cityResponse))
            } else {
                weatherData.postValue(MyResult.Error("Network unavailable"))
            }
        } catch (t: Throwable) {
            weatherData.postValue(MyResult.Error(t.message.toString()))
        }
    }
}

private fun handlePairResponse(
    weather: Response<WeatherResponse>,
    city: Response<CurrentCityResponse>
): MyResult<Pair<WeatherResponse, CurrentCityResponse>> {
    if (weather.isSuccessful && city.isSuccessful) {
        if (weather.body() != null && city.body() != null) {
            return MyResult.Success(Pair(weather.body()!!, city.body()!!))
        }
    }
    return MyResult.Error(weather.message())
}