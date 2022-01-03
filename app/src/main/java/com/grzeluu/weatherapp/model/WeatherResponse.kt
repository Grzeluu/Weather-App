package com.grzeluu.weatherapp.model

import java.io.Serializable

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: String,

    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>
): Serializable
