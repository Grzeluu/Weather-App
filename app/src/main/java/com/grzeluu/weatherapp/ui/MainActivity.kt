package com.grzeluu.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ActivityMainBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.ui.adapters.daily.DailyAdapter
import com.grzeluu.weatherapp.ui.adapters.hourly.HourlyAdapter
import com.grzeluu.weatherapp.util.Constants
import com.grzeluu.weatherapp.util.MyResult
import com.grzeluu.weatherapp.util.TemperatureUtils.Companion.getTemperatureUnit
import com.grzeluu.weatherapp.util.TimeUtils.Companion.unixTime
import com.grzeluu.weatherapp.util.WeatherIconProvider.Companion.setWeatherIcon
import com.grzeluu.weatherapp.viewmodel.ViewModelProviderFactory
import com.grzeluu.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        showProgress()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        init()

        binding.srlContainer.setOnRefreshListener {
            viewModel.refreshWeather()
        }

        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvHourly.adapter = null
        binding.rvDaily.adapter = null
    }

    private fun init() {
        setUpAdapters()
        setUpViewModel()
        prepWeatherUpdate()
    }

    private fun setUpAdapters() {
        binding.rvHourly.setHasFixedSize(true)
        binding.rvDaily.setHasFixedSize(true)

        val hourlyLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourly.layoutManager = hourlyLinearLayoutManager
        hourlyAdapter = HourlyAdapter()
        binding.rvHourly.adapter = hourlyAdapter

        val dailyLinearLayoutManager = LinearLayoutManager(this)
        binding.rvDaily.layoutManager = dailyLinearLayoutManager
        dailyAdapter = DailyAdapter()
        binding.rvDaily.adapter = dailyAdapter

    }

    private fun setUpInterface(weatherResponse: WeatherResponse) {
        val current = weatherResponse.current
        for (i in current.weather.indices) {
            var description = current.weather[i].description
            description = description.substring(0, 1).uppercase() + description.substring(1)
            binding.tvCurrentWeatherDescription.text = description

            binding.tvTemperature.text =
                getString(
                    R.string.temperature,
                    current.temp.toInt(),
                    getTemperatureUnit(application)
                )
            binding.tvFeelsLike.text =
                getString(
                    R.string.feels_like,
                    current.feels_like.toInt(),
                    getTemperatureUnit(application)
                )

            binding.tvWind.text = getString(R.string.wind, current.wind_speed)
            binding.tvHumidity.text = getString(R.string.percent_of, current.humidity)
            binding.tvPressure.text = getString(R.string.pressure, current.pressure)

            binding.tvSunrise.text = unixTime(current.sunrise)
            binding.tvSunset.text = unixTime(current.sunset)

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
                        hideProgress()
                        setUpInterface(response.data!!)

                        hourlyAdapter.submitList(response.data.hourly)
                        binding.rvHourly.adapter = hourlyAdapter

                        dailyAdapter.submitList(response.data.daily)
                        binding.rvDaily.adapter = dailyAdapter
                    }
                    is MyResult.Error -> {
                        hideProgress()
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

    private fun showProgress(){
        hideViews()
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        showViews()
        binding.pbLoading.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
        binding.srlContainer.isRefreshing = false
    }

    private fun hideViews(){
        binding.srlContainer.visibility = View.INVISIBLE
        binding.appBarLayout.visibility = View.INVISIBLE
    }

    private fun showViews(){
        binding.srlContainer.visibility = View.VISIBLE
        binding.appBarLayout.visibility = View.VISIBLE
    }
}