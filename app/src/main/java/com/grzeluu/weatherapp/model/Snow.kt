package com.grzeluu.weatherapp.model

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("1h")
    val hourly: Double
)
