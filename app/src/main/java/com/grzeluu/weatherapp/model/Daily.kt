package com.grzeluu.weatherapp.model

data class Daily(
    val dt: Int,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_gust: String,
    val wind_deg: Int,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val rain: Double,
    val snow: Double,
    val uvi: Double
)
