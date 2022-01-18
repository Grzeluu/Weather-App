package com.grzeluu.weatherapp.util

import android.content.Context
import com.grzeluu.weatherapp.source.ApiConstants.IMPERIAL_UNITS
import com.grzeluu.weatherapp.source.ApiConstants.METRIC_UNITS
import com.grzeluu.weatherapp.util.SharedPreferencesUtils.Companion.getUnits

class UnitsUtils {
    companion object {
        fun getTemperatureUnits(context: Context): String {
            return when (getUnits(context)) {
                METRIC_UNITS -> "°C"
                IMPERIAL_UNITS -> "°F"
                else -> " "
            }
        }

        fun getSpeedUnits(context: Context): String {
            return when (getUnits(context)) {
                METRIC_UNITS -> " m/s"
                IMPERIAL_UNITS -> "mph"
                else -> " "
            }
        }
    }
}
