package com.grzeluu.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.grzeluu.weatherapp.databinding.ActivityMainBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.repository.AppRepository
import com.grzeluu.weatherapp.util.Constants
import com.grzeluu.weatherapp.util.MyResult
import com.grzeluu.weatherapp.viewmodel.ViewModelProviderFactory
import com.grzeluu.weatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        init()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshWeather()
        }
    }

    private fun init() {
        setUpViewModel()
        prepWeatherUpdate()
    }

    private fun setUpViewModel() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)
        viewModel.refreshWeather()
    }

    private fun setUpInterface(weatherList: WeatherResponse) {

        for (i in weatherList.weather.indices) {
            binding.tvCurrentLocation.text = weatherList.name

            binding.tvCurrentWeather.text = weatherList.weather[i].main
            var description = weatherList.weather[i].description
            description = description.substring(0, 1).uppercase() + description.substring(1)
            binding.tvCurrentWeatherDescription.text = description
            binding.tvTemperature.text =
                getString(
                    R.string.temperature,
                    weatherList.main.temp.toInt(),
                    getTemperatureUnit()
                )
            binding.tvFeelsLike.text =
                getString(
                    R.string.feels_like,
                    weatherList.main.feels_like.toInt(),
                    getTemperatureUnit()
                )

            binding.tvWind.text = getString(R.string.wind, weatherList.wind.speed)
            binding.tvHumidity.text = getString(R.string.humidity, weatherList.main.humidity)
            binding.tvPressure.text = getString(R.string.pressure, weatherList.main.pressure)

            binding.tvSunrise.text = unixTime(weatherList.sys.sunrise)
            binding.tvSunset.text = unixTime(weatherList.sys.sunset)

            when (weatherList.weather[i].icon) {
                "01d" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_clear_sky)
                "01n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_clear_sky_night)

                "02d" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_partly_cloudy)
                "02n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_night_partly_cloudy)

                "03d", "04d", "03n", "04n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_cloudy)
                "09d" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_partly_rainy)
                "10d", "10n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_rainy)
                "11d", "11n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_thunderstorm)
                "13d", "13n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_snow)
                "50d", "50n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_fog)
            }
        }
    }

    private fun getTemperatureUnit() = getUnit(application.resources.configuration.toString())

    private fun getUnit(value: String): String {
        var unit = "°C"
        if (value == "US" || value == "LR" || "MM" == value) {
            unit = "°F"
        }
        return unit
    }

    private fun prepWeatherUpdate() {
        if (ContextCompat.checkSelfPermission(
                baseContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getWeather()
        } else {
            val permissionRequest = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, Constants.LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun getWeather() {
        viewModel.getLocationData().observe(this, { location ->
           viewModel.getWeatherLocationData(location.latitude, location.longitude).observe(this,
                { response ->
                    when (response) {
                        is MyResult.Success -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                            setUpInterface(response.data!!)
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
                    getWeather()
                } else {
                    showMessage("Unable to update location without permission")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun unixTime(timestamp: Long): String? {
        val date = Date(timestamp * 1000L)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.UK)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
            .show()
    }
}