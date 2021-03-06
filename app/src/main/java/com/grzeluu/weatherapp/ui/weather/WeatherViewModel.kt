package com.grzeluu.weatherapp.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grzeluu.weatherapp.repository.WeatherRepository
import com.grzeluu.weatherapp.util.MyResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.model.Coord
import com.grzeluu.weatherapp.model.CurrentCityResponse
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.source.ApiConstants.APP_ID
import com.grzeluu.weatherapp.source.ApiConstants.EXCLUDE
import com.grzeluu.weatherapp.util.LocationLiveData
import com.grzeluu.weatherapp.util.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val weatherRepository: WeatherRepository = WeatherRepository(application)

    val locationData = LocationLiveData(application)
    val weatherData: MutableLiveData<MyResult<Pair<WeatherResponse, CurrentCityResponse>>> =
        MutableLiveData()

    fun refreshLocation() = locationData.locationUpdate()

    fun getWeather(coord: Coord, units: String) = viewModelScope.launch {
        fetchWeather(
            coord.lat,
            coord.lon,
            units,
            APP_ID,
            EXCLUDE
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
            if (NetworkUtils.isNetworkAvailable(getApplication<Application>())) {
                val weatherResponse = weatherRepository.getWeather(lat, lon, units, appid, exclude)
                val cityResponse = weatherRepository.getCurrentCity(lat, lon, appid)
                weatherData.postValue(handlePairResponse(weatherResponse, cityResponse))
            } else {
                weatherData.postValue(MyResult.Error(getApplication<Application>().getString(R.string.network_error)))
            }
        } catch (t: Throwable) {
            weatherData.postValue(MyResult.Error(t.message.toString()))
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
}

