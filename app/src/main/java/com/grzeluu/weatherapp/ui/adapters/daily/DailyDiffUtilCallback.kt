package com.grzeluu.weatherapp.ui.adapters.daily

import androidx.recyclerview.widget.DiffUtil
import com.grzeluu.weatherapp.model.Daily

class DailyDiffUtilCallback : DiffUtil.ItemCallback<Daily>(){
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }
}
