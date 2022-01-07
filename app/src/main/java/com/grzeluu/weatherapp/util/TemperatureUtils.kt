package com.grzeluu.weatherapp.util

import android.app.Application

class TemperatureUtils {
    companion object {
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
}