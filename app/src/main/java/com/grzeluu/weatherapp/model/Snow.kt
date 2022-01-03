package com.grzeluu.weatherapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Snow(
    @SerializedName("1h")
    val hourly: Double
): Serializable
