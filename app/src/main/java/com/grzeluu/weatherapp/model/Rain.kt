package com.grzeluu.weatherapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Rain(
    @SerializedName("1h")
    val hourly: Double
): Serializable
