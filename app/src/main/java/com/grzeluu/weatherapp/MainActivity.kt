package com.grzeluu.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.grzeluu.weatherapp.databinding.ActivityMainBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.WeatherService
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!isLocationEnabled()) {
            showMessage("Your location provider is turned OFF. Please turn it ON")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        } else {
            showMessage("Your location provider is turned ON")
            checkLocationPermission()
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val latitude = mLastLocation.latitude
            val longitude = mLastLocation.longitude
            Log.i("[lat, lon] ", "[$latitude, $longitude]")
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (Constants.isNetworkAvailable(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
            )

            showProgress()
            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherList: WeatherResponse = response.body()!!
                        Log.i("Response Result", "$weatherList")
                        setUp(weatherList)
                    } else {
                        when (response.code()) {
                            400 -> Log.e("Error 400", "Bad Connection")
                            404 -> Log.e("Error 404", "Not Found")
                            else -> Log.e("Error", "Generic Error")
                        }
                    }
                    hideProgress()
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                    hideProgress()
                }
            })

        } else {
            showMessage("No internet connection available")
        }
    }

    private fun setUp(weatherList: WeatherResponse) {
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
                "09d", "09n" -> binding.ivCurrentWeather.setImageResource(R.drawable.ic_partly_rainy)
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

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private fun checkLocationPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        requestLocationData()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showMessage("You have denied location permission. Please enable them")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalPermissionsDialog()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalPermissionsDialog() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off location permission. It can be enabled under Application Settings")
            .setPositiveButton("Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

    private fun showProgress() {
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }
}