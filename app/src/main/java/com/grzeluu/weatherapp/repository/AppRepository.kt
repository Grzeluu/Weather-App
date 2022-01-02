package com.grzeluu.weatherapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.RetrofitInstance
import com.grzeluu.weatherapp.util.MyResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository {

    var client = RetrofitInstance.weatherApi

    fun getWeather(
        latitude: Double, longitude: Double,
        unit: String, appid: String
    ): LiveData<MyResult<WeatherResponse>> {

        val liveData = MutableLiveData<MyResult<WeatherResponse>>()
        liveData.postValue(MyResult.Loading())

        client.getWeather(latitude, longitude, unit, appid)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.i("Response", response.body().toString())
                        liveData.value = MyResult.Success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    liveData.postValue(MyResult.Error(t.message.toString()))
                }
            })

        return liveData
    }
}