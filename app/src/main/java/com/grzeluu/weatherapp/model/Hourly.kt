package com.grzeluu.weatherapp.model

data class Hourly (
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_gust: String,
    val wind_deg: Int,
    val pop: Double,
    val weather: List<Weather>,
    val rain: Rain,
    val snow: Snow
)
