package com.grzeluu.weatherapp.ui.weather.adapters.hourly

import androidx.recyclerview.widget.DiffUtil
import com.grzeluu.weatherapp.model.Hourly

class HourlyDiffUtilCallback : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}