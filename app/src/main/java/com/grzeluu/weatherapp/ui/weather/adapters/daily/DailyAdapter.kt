package com.grzeluu.weatherapp.ui.weather.adapters.daily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ItemDailyForecastBinding
import com.grzeluu.weatherapp.model.Daily
import com.grzeluu.weatherapp.util.DateUtils.Companion.unixDay
import com.grzeluu.weatherapp.util.IconProvider.Companion.setExpandIcon
import com.grzeluu.weatherapp.util.TimeUtils
import com.grzeluu.weatherapp.util.IconProvider.Companion.setWeatherIcon
import com.grzeluu.weatherapp.util.PrecipitationUtils.Companion.getPrecipitationDescription
import com.grzeluu.weatherapp.util.TextFormat.Companion.formatDescription
import com.grzeluu.weatherapp.util.TextViewUtils.Companion.setPoP
import com.grzeluu.weatherapp.util.UnitsUtils.Companion.getSpeedUnits
import com.grzeluu.weatherapp.util.UnitsUtils.Companion.getTemperatureUnits

class DailyAdapter : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        getItem(position).let { daily ->
            holder.bind(daily)
        }
    }

    class DailyViewHolder(private val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(daily: Daily) {
            with(binding) {
                tvMaxTemp.text = context.getString(
                    R.string.temperature_value,
                    daily.temp.max.toInt(),
                    getTemperatureUnits(context)
                )
                tvMinTemp.text = context.getString(
                    R.string.temperature_value,
                    daily.temp.min.toInt(),
                    getTemperatureUnits(context)
                )
                tvDate.text = unixDay(daily.dt)
                ivDailyWeather.setWeatherIcon(daily.weather[0].icon)
                tvProbabilityOfPrecipitation.setPoP(daily.pop)

                ivWeatherExpand.setWeatherIcon(daily.weather[0].icon)
                tvWind.text = context.getString(
                    R.string.wind_value,
                    daily.wind_speed,
                    getSpeedUnits(context)
                )
                tvPressure.text = context.getString(
                    R.string.pressure_value,
                    daily.pressure
                )

                tvSunrise.text = TimeUtils.unixTime(daily.sunrise)
                tvSunset.text = TimeUtils.unixTime(daily.sunset)

                tvDescription.text = formatDescription(daily.weather[0].description)
                tvPrecipitation.text = getPrecipitationDescription(daily, context)

                itemDaily.setOnClickListener {
                    binding.changeExpandableVisibility()
                }
            }
        }

        private fun ItemDailyForecastBinding.changeExpandableVisibility() {
            with(this) {
                when (this.llExpandable.visibility) {
                    View.GONE, View.INVISIBLE -> {
                        this.llExpandable.visibility = View.VISIBLE
                        this.ivExpand.setExpandIcon(true)
                    }
                    View.VISIBLE -> {
                        this.llExpandable.visibility = View.GONE
                        this.ivExpand.setExpandIcon(false)
                    }
                }
            }
        }
    }
}