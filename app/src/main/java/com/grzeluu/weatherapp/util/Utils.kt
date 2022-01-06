package com.grzeluu.weatherapp.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ImageView
import com.grzeluu.weatherapp.R
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun isNetworkAvailable(application: Application): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun unixTime(timestamp: Long): String? {
        val date = Date(timestamp * 1000L)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.UK)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    fun unixDay(timestamp: Long): String? {
        val date = Date(timestamp * 1000L)
        val dateFormat = SimpleDateFormat("EEEE", Locale.UK)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    fun ImageView.setWeatherIcon(id: String) {
        when (id) {
            "01d" -> this.setImageResource(R.drawable.ic_clear_sky)
            "01n" -> this.setImageResource(R.drawable.ic_clear_sky_night)

            "02d" -> this.setImageResource(R.drawable.ic_partly_cloudy)
            "02n" -> this.setImageResource(R.drawable.ic_night_partly_cloudy)

            "03d", "03n" -> this.setImageResource(R.drawable.ic_cloudy)
            "04d", "04n" -> this.setImageResource(R.drawable.ic_broken_clouds)
            "09d" -> this.setImageResource(R.drawable.ic_partly_rainy)
            "10d", "10n" -> this.setImageResource(R.drawable.ic_rainy)
            "11d", "11n" -> this.setImageResource(R.drawable.ic_thunderstorm)
            "13d", "13n" -> this.setImageResource(R.drawable.ic_snow)
            "50d", "50n" -> this.setImageResource(R.drawable.ic_fog)
        }
    }

    fun getTemperatureUnit(application: Application) =
        getUnit(application.resources.configuration.toString())

    private fun getUnit(value: String): String {
        var unit = "°C"
        if (value == "US" || value == "LR" || "MM" == value) {
            unit = "°F"
        }
        return unit
    }
}