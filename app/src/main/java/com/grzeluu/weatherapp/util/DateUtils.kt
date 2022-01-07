package com.grzeluu.weatherapp.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        fun unixDay(timestamp: Long): String? {
            val date = Date(timestamp * 1000L)
            val dateFormat = SimpleDateFormat("EEEE", Locale.UK)
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(date)
        }
    }
}