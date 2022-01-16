package com.grzeluu.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ActivityMainBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.ui.adapters.daily.DailyAdapter
import com.grzeluu.weatherapp.ui.adapters.hourly.HourlyAdapter
import com.grzeluu.weatherapp.util.Constants
import com.grzeluu.weatherapp.util.MyResult
import com.grzeluu.weatherapp.util.TemperatureUtils.Companion.getTemperatureUnit
import com.grzeluu.weatherapp.util.TimeUtils.Companion.unixTime
import com.grzeluu.weatherapp.util.IconProvider.Companion.setWeatherIcon
import com.grzeluu.weatherapp.util.LocationUtils.Companion.isLocationEnabled
import com.grzeluu.weatherapp.util.PrecipitationUtils.Companion.getPrecipitationDescription
import com.grzeluu.weatherapp.util.TextFormat.Companion.formatDescription
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
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        binding.srlContainer.setOnRefreshListener {
             prepareWeatherUpdate()
        }

        showProgress()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvHourly.adapter = null
        binding.rvDaily.adapter = null
    }

    private fun init() {
        setUpAdapters()
        setUpViewModel()
        prepareWeatherUpdate()
    }

    private fun setUpAdapters() {
        binding.rvHourly.setHasFixedSize(true)
        binding.rvDaily.setHasFixedSize(true)

        val hourlyLinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

        with(binding) {
            ivCurrentWeather.setWeatherIcon(current.weather[0].icon)
            tvCurrentWeatherDescription.text =
                formatDescription(current.weather[0].description)
            tvTemperature.text =
                getString(
                    R.string.temperature,
                    current.temp.toInt(),
                    getTemperatureUnit(application)
                )
            tvFeelsLike.text =
                getString(
                    R.string.feels_like,
                    current.feels_like.toInt(),
                    getTemperatureUnit(application)
                )
            tvPrecipitation.text = getPrecipitationDescription(current, baseContext)

            tvWind.text = getString(R.string.wind, current.wind_speed)
            tvHumidity.text = getString(R.string.percent_of, current.humidity)
            tvPressure.text = getString(R.string.pressure, current.pressure)

            tvSunrise.text = unixTime(current.sunrise)
            tvSunset.text = unixTime(current.sunset)

            hourlyAdapter.submitList(weatherResponse.hourly)
            rvHourly.adapter = hourlyAdapter

            dailyAdapter.submitList(weatherResponse.daily)
            rvDaily.adapter = dailyAdapter
        }
    }

    private fun prepareWeatherUpdate() {
        if (ContextCompat.checkSelfPermission(
                baseContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionRequest = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, Constants.LOCATION_PERMISSION_REQUEST_CODE)
        } else if (!isLocationEnabled(this)) {
            showError(getString(R.string.location_off_error))
        } else {
            viewModel.refreshWeather()
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
                    prepareWeatherUpdate()
                } else {
                    showError(getString(R.string.location_permission_error))
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun setUpViewModel() {
        val factory = ViewModelProviderFactory(application)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)
        getLocationWeather()
    }

    private fun getLocationWeather() {
        viewModel.locationData.observe(this, { location ->
            Log.i("Location Data", location.toString())
            viewModel.getWeather(location)
        })

        viewModel.weatherData.observe(this,
            { result ->
                when (result) {
                    is MyResult.Success -> {
                        hideProgress()
                        setUpInterface(result.data!!)
                    }
                    is MyResult.Error -> {
                        hideProgress()
                        result.message?.let { message ->
                            showError(message)
                        }
                    }
                    is MyResult.Loading -> {}
                }
            })
    }

    private fun showError(message: String) {
        hideViews()
        binding.pbLoading.visibility = View.GONE
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        binding.ivError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvMessage.visibility = View.GONE
        binding.ivError.visibility = View.GONE
    }

    private fun showProgress() {
        hideViews()
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvMessage.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        showViews()
        binding.pbLoading.visibility = View.GONE
        binding.tvMessage.visibility = View.GONE
        binding.srlContainer.isRefreshing = false
    }

    private fun hideViews() {
        binding.srlContainer.visibility = View.INVISIBLE
        binding.appBarLayout.visibility = View.INVISIBLE
    }

    private fun showViews() {
        binding.srlContainer.visibility = View.VISIBLE
        binding.appBarLayout.visibility = View.VISIBLE
    }
}