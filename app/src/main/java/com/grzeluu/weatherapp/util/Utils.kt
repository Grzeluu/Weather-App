package com.grzeluu.weatherapp.util

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.grzeluu.weatherapp.app.MyApplication

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
}