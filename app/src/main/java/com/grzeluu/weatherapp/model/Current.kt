package com.grzeluu.weatherapp.model

import java.io.Serializable

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<Weather>,
    val rain: Rain?,
    val snow: Snow?
): Serializable
