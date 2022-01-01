package com.grzeluu.weatherapp.model

import java.io.Serializable

data class Wind(
    val speed: Double,
    val deg: Double
): Serializable
