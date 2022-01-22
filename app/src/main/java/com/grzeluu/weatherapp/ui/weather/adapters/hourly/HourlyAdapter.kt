package com.grzeluu.weatherapp.ui.weather.adapters.hourly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ItemHourlyForecastBinding
import com.grzeluu.weatherapp.model.Hourly
import com.grzeluu.weatherapp.util.IconProvider.Companion.setWeatherIcon
import com.grzeluu.weatherapp.util.TextViewUtils.Companion.setPoP
import com.grzeluu.weatherapp.util.TimeUtils.Companion.unixTime
import com.grzeluu.weatherapp.util.UnitsUtils

class HourlyAdapter :
    ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val binding = ItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        getItem(position).let { hourly ->
            holder.bind(hourly)
        }
    }

    class HourlyViewHolder(private val binding: ItemHourlyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(hourly: Hourly) {
            with(binding) {
                tvTemp.text = context.getString(
                    R.string.temperature_value,
                    hourly.temp.toInt(),
                    UnitsUtils.getTemperatureUnits(context)
                )
                tvTime.text = unixTime(hourly.dt)
                ivWeather.setWeatherIcon(hourly.weather[0].icon)
                if (hourly.pop > 0) {
                    tvProbabilityOfPrecipitation.setPoP(hourly.pop)
                    tvProbabilityOfPrecipitation.visibility = View.VISIBLE
                } else {
                    tvProbabilityOfPrecipitation.visibility = View.GONE
                }
            }
        }
    }
}