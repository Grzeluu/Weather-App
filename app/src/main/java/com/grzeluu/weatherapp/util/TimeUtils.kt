package com.grzeluu.weatherapp.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    companion object {
        fun unixTime(timestamp: Long): String? {
            val date = Date(timestamp * 1000L)
            val dateFormat = SimpleDateFormat("HH:mm", Locale.UK)
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(date)
        }
    }
}