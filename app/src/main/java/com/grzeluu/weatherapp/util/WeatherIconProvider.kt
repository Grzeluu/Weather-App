package com.grzeluu.weatherapp.util

import android.widget.ImageView
import com.grzeluu.weatherapp.R

class WeatherIconProvider {
    companion object {
        fun ImageView.setWeatherIcon(id: String) {
            when (id) {
                "01d" -> this.setImageResource(R.drawable.ic_clear_sky)
                "01n" -> this.setImageResource(R.drawable.ic_clear_sky_night)

                "02d" -> this.setImageResource(R.drawable.ic_partly_cloudy)
                "02n" -> this.setImageResource(R.drawable.ic_night_partly_cloudy)

                "03d", "03n" -> this.setImageResource(R.drawable.ic_cloudy)
                "04d", "04n" -> this.setImageResource(R.drawable.ic_broken_clouds)
                "09d" -> this.setImageResource(R.drawable.ic_partly_rainy)
                "10d", "10n" -> this.setImageResource(R.drawable.ic_rainy)
                "11d", "11n" -> this.setImageResource(R.drawable.ic_thunderstorm)
                "13d", "13n" -> this.setImageResource(R.drawable.ic_snow)
                "50d", "50n" -> this.setImageResource(R.drawable.ic_fog)
            }
        }
    }
}