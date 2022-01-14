package com.grzeluu.weatherapp.util

class TextFormat {
    companion object {
        fun doubleToPercent(value: Double) =
            (value * 100).toInt()

        fun formatDescription(description: String) =
            description.substring(0, 1).uppercase() + description.substring(1)
    }
}