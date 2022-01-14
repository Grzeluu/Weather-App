package com.grzeluu.weatherapp.util

import android.content.Context
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.model.Current
import com.grzeluu.weatherapp.model.Daily

class PrecipitationUtils {
    companion object {
        fun getPrecipitationDescription(current: Current, context: Context): String{
            return when {
                current.rain != null -> context.getString(
                    R.string.rain,
                    current.rain.hourly,
                )
                current.snow != null -> context.getString(
                    R.string.snow,
                    current.snow.hourly
                )
                else -> context.getString(R.string.no_precipitation)
            }
        }

        fun getPrecipitationDescription(daily: Daily, context: Context): String{
            return when {
                daily.rain != null -> context.getString(
                    R.string.rain,
                    daily.rain
                )
                daily.snow != null -> context.getString(
                    R.string.snow,
                    daily.snow
                )
                else -> context.getString(R.string.no_precipitation)
            }
        }
    }
}