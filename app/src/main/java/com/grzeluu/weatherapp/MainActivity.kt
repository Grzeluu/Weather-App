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
import com.grzeluu.weatherapp.databinding.DialogProgressBinding
import com.grzeluu.weatherapp.model.WeatherResponse
import com.grzeluu.weatherapp.network.WeatherService
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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