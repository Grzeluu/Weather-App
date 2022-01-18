package com.grzeluu.weatherapp.util

import android.content.Context
import com.grzeluu.weatherapp.source.ApiConstants.METRIC_UNITS


class SharedPreferencesUtils {
    companion object {
        private const val PREFERENCE = "PREF"
        private const val UNITS_SETTINGS = "UNITS_SETTINGS"
        private const val DEF_UNITS = METRIC_UNITS

        fun setUnits(context: Context, units: String) {
            val settings = context.getSharedPreferences(PREFERENCE, 0)
            val editor = settings.edit()
            editor.putString(UNITS_SETTINGS, units)
            editor.apply()
        }

        fun getUnits(context: Context): String {
            val settings = context.getSharedPreferences(PREFERENCE, 0)
            return settings.getString(UNITS_SETTINGS, DEF_UNITS)!!
        }
    }
}