package com.grzeluu.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ActivityMainBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.ui.adapters.HourlyAdapter
import com.grzeluu.weatherapp.util.Constants
import com.grzeluu.weatherapp.util.MyResult
import com.grzeluu.weatherapp.util.Utils
import com.grzeluu.weatherapp.util.Utils.setWeatherIcon
import com.grzeluu.weatherapp.viewmodel.ViewModelProviderFactory
import com.grzeluu.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var hourlyAdapter: HourlyAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        init()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshWeather()
        }

        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvHourly.adapter = null
    }

    private fun init() {
        setUpAdapters()
        setUpViewModel()
        prepWeatherUpdate()
    }

    private fun setUpAdapters() {
        binding.rvHourly.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //linearLayoutManager.stackFromEnd = false

        binding.rvHourly.layoutManager = linearLayoutManager
        hourlyAdapter = HourlyAdapter()

        // hourlyAdapter.event.observe()

        binding.rvHourly.adapter = hourlyAdapter
    }

    private fun setUpInterface(weatherResponse: WeatherResponse) {
        val current = weatherResponse.current
        for (i in current.weather.indices) {

            binding.tvCurrentWeather.text = current.weather[i].main

            var description = current.weather[i].description
            description = description.substring(0, 1).uppercase() + description.substring(1)
            binding.tvCurrentWeatherDescription.text = description

            binding.tvTemperature.text =
                getString(
                    R.string.temperature,
                    current.temp.toInt(),
                    Utils.getTemperatureUnit(application)
                )
            binding.tvFeelsLike.text =
                getString(
                    R.string.feels_like,
                    current.feels_like.toInt(),
                    Utils.getTemperatureUnit(application)
                )

            binding.tvWind.text = getString(R.string.wind, current.wind_speed)
            binding.tvHumidity.text = getString(R.string.humidity, current.humidity)
            binding.tvPressure.text = getString(R.string.pressure, current.pressure)

            binding.tvSunrise.text = Utils.unixTime(current.sunrise)
            binding.tvSunset.text = Utils.unixTime(current.sunset)

            binding.ivCurrentWeather.setWeatherIcon(current.weather[i].icon)
        }
    }

    private fun prepWeatherUpdate() {
        if (ContextCompat.checkSelfPermission(
                baseContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionRequest = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, Constants.LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLocationWeather()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepWeatherUpdate()
                } else {
                    showMessage("Unable to update location without permission")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun setUpViewModel() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)
        viewModel.refreshWeather()
    }

    private fun getLocationWeather() {
        viewModel.locationData.observe(this, { location ->
            viewModel.getWeather(location.latitude, location.longitude)
            Log.i("Location", location.toString())
        })

        viewModel.weatherData.observe(this,
            { response ->
                when (response) {
                    is MyResult.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        setUpInterface(response.data!!)
                        hourlyAdapter.submitList(response.data.hourly)
                        binding.rvHourly.adapter = hourlyAdapter
                    }
                    is MyResult.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        response.message?.let { message ->
                            showMessage(message)
                        }
                    }
                    is MyResult.Loading -> {}
                }
            })
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
            .show()
    }
}