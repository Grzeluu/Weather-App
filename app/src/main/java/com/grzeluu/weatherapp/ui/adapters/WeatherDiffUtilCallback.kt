package com.grzeluu.weatherapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.grzeluu.weatherapp.model.WeatherResponse

class WeatherDiffUtilCallback : DiffUtil.ItemCallback<WeatherResponse>() {
    override fun areItemsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
        return oldItem.current.dt == newItem.current.dt
    }

    override fun areContentsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
        return oldItem.current.dt == newItem.current.dt
    }
}