package com.grzeluu.weatherapp.ui.weather

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
import com.grzeluu.weatherapp.model.CurrentCityResponse
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.source.ApiConstants.IMPERIAL_UNITS
import com.grzeluu.weatherapp.source.ApiConstants.METRIC_UNITS
import com.grzeluu.weatherapp.ui.weather.adapters.daily.DailyAdapter
import com.grzeluu.weatherapp.ui.weather.adapters.hourly.HourlyAdapter
import com.grzeluu.weatherapp.util.Constants
import com.grzeluu.weatherapp.util.MyResult
import com.grzeluu.weatherapp.util.UnitsUtils.Companion.getTemperatureUnits
import com.grzeluu.weatherapp.util.TimeUtils.Companion.unixTime
import com.grzeluu.weatherapp.util.IconProvider.Companion.setWeatherIcon
import com.grzeluu.weatherapp.util.LocationUtils.Companion.isLocationEnabled
import com.grzeluu.weatherapp.util.PrecipitationUtils.Companion.getPrecipitationDescription
import com.grzeluu.weatherapp.util.SharedPreferencesUtils.Companion.getUnits
import com.grzeluu.weatherapp.util.SharedPreferencesUtils.Companion.setUnits
import com.grzeluu.weatherapp.util.TextFormat.Companion.formatDescription
import com.grzeluu.weatherapp.util.UnitsUtils.Companion.getSpeedUnits
import com.grzeluu.weatherapp.viewmodel.ViewModelProviderFactory

class WeatherActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        initUnitSettings()
        initButtons()
        setUpAdapters()
        setUpViewModel()
        prepareWeatherUpdate()
    }

    private fun initButtons() {
        binding.btRefresh.setOnClickListener{
            prepareWeatherUpdate()
        }
    }

    private fun initUnitSettings() {
        when (getUnits(this)) {
            METRIC_UNITS -> binding.toolbar.rbMetric.isChecked = true
            IMPERIAL_UNITS -> binding.toolbar.rbImperial.isChecked = true
        }

        binding.toolbar.rbMetric.setOnClickListener {
            setUnits(this, METRIC_UNITS)
            viewModel.refreshLocation()
        }
        binding.toolbar.rbImperial.setOnClickListener {
            setUnits(this, IMPERIAL_UNITS)
            viewModel.refreshLocation()
        }
    }

    private fun setUpAdapters() {
        with(binding) {
            rvHourly.setHasFixedSize(true)
            rvDaily.setHasFixedSize(true)

            val hourlyLinearLayoutManager =
                LinearLayoutManager(this@WeatherActivity, LinearLayoutManager.HORIZONTAL, false)
            rvHourly.layoutManager = hourlyLinearLayoutManager
            hourlyAdapter = HourlyAdapter()
            rvHourly.adapter = hourlyAdapter

            val dailyLinearLayoutManager = LinearLayoutManager(this@WeatherActivity)
            rvDaily.layoutManager = dailyLinearLayoutManager
            dailyAdapter = DailyAdapter()
            rvDaily.adapter = dailyAdapter
        }
    }

    private fun setUpInterface(response: Pair<WeatherResponse, CurrentCityResponse>) {
        val weather = response.first
        val currentWeather = response.first.current
        val city = response.second

        with(binding) {
            toolbar.tvCurrentLocation.text = city.name

            ivCurrentWeather.setWeatherIcon(currentWeather.weather[0].icon)
            tvCurrentWeatherDescription.text =
                formatDescription(currentWeather.weather[0].description)

            tvTemperature.text =
                getString(
                    R.string.temperature_value,
                    currentWeather.temp.toInt(),
                    getTemperatureUnits(application)
                )
            tvFeelsLike.text =
                getString(
                    R.string.feels_like,
                    currentWeather.feels_like.toInt(),
                    getTemperatureUnits(this@WeatherActivity)
                )

            tvPrecipitation.text = getPrecipitationDescription(currentWeather, baseContext)

            tvHumidity.text = getString(R.string.percent_of, currentWeather.humidity)
            tvClouds.text = getString(R.string.percent_of, currentWeather.clouds)
            tvWind.text =
                getString(
                    R.string.wind_value,
                    currentWeather.wind_speed,
                    getSpeedUnits(this@WeatherActivity)
                )
            tvPressure.text = getString(R.string.pressure_value, currentWeather.pressure)
            tvSunrise.text = unixTime(currentWeather.sunrise)
            tvSunset.text = unixTime(currentWeather.sunset)

            setUpRecyclerViews(weather)
        }
    }

    private fun ActivityMainBinding.setUpRecyclerViews(weather: WeatherResponse) {
        hourlyAdapter.submitList(weather.hourly)
        rvHourly.adapter = hourlyAdapter

        dailyAdapter.submitList(weather.daily)
        rvDaily.adapter = dailyAdapter
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
            viewModel.refreshLocation()
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
            viewModel.getWeather(location, getUnits(this))
        })

        viewModel.weatherData.observe(this,
            { result ->
                when (result) {
                    is MyResult.Success -> {
                        setUpInterface(result.data!!)
                        hideProgress()
                    }
                    is MyResult.Error -> {
                        result.message?.let { message ->
                            showError(message)
                        }
                    }
                    is MyResult.Loading -> {
                        if (!binding.srlContainer.isRefreshing) {
                            showProgress()
                        }
                    }
                }
            })
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

    private fun showError(message: String) {
        hideProgress()
        hideViews()
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        binding.ivError.visibility = View.VISIBLE
        binding.btRefresh.visibility = View.VISIBLE
    }

    private fun showProgress() {
        hideViews()
        binding.tvMessage.text = getString(R.string.loading_your_forecast)
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvMessage.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        showViews()
        binding.pbLoading.visibility = View.INVISIBLE
        binding.tvMessage.visibility = View.INVISIBLE
        binding.srlContainer.isRefreshing = false
    }

    private fun hideViews() {
        binding.srlContainer.visibility = View.INVISIBLE
        binding.toolbar.container.visibility = View.INVISIBLE
        binding.tvMessage.visibility = View.INVISIBLE
        binding.ivError.visibility = View.INVISIBLE
        binding.btRefresh.visibility = View.INVISIBLE
    }

    private fun showViews() {
        binding.srlContainer.visibility = View.VISIBLE
        binding.toolbar.container.visibility = View.VISIBLE
    }
}