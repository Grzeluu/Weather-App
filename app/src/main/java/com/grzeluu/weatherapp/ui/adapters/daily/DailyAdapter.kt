package com.grzeluu.weatherapp.ui.adapters.daily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.databinding.ItemDailyForecastBinding
import com.grzeluu.weatherapp.model.Daily
import com.grzeluu.weatherapp.util.DateUtils.Companion.unixDay
import com.grzeluu.weatherapp.util.ExpandIconProvider.Companion.setExpandIcon
import com.grzeluu.weatherapp.util.TimeUtils
import com.grzeluu.weatherapp.util.WeatherIconProvider.Companion.setWeatherIcon

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
                    R.string.temperature,
                    daily.temp.max.toInt(),
                    "°C"
                )
                tvMinTemp.text = context.getString(
                    R.string.temperature,
                    daily.temp.min.toInt(),
                    "°C"
                )
                tvDate.text = unixDay(daily.dt)
                ivDailyWeather.setWeatherIcon(daily.weather[0].icon)
                setPoP(daily.pop)

                ivWeatherExpand.setWeatherIcon(daily.weather[0].icon)
                tvWind.text = context.getString(
                    R.string.wind,
                    daily.wind_speed
                )
                tvPressure.text = context.getString(
                    R.string.pressure,
                    daily.pressure
                )

                tvSunrise.text = TimeUtils.unixTime(daily.sunrise)
                tvSunset.text = TimeUtils.unixTime(daily.sunset)

                tvDescription.text = formatDescription(daily)
                tvPrecipitation.text =
                    when {
                        daily.rain != null -> context.getString(
                            R.string.rain,
                            daily.rain
                        )
                        daily.snow != null -> context.getString(
                            R.string.snow,
                            daily.snow
                        )
                        else -> context.getString(R.string.no_precipitation)
                    }

                itemDaily.setOnClickListener {
                    binding.changeExpandableVisibility()
                }
            }
        }

        private fun formatDescription(daily: Daily): String {
            var description = daily.weather[0].description
            description = description.substring(0, 1).uppercase() + description.substring(1)
            return description
        }

        private fun ItemDailyForecastBinding.setPoP(pop: Double) {
            with(this) {
                if (pop > 0) {
                    tvProbabilityOfPrecipitation.text = "${(pop * 100).toInt()}%"
                    tvProbabilityOfPrecipitation.visibility = View.VISIBLE
                } else {
                    tvProbabilityOfPrecipitation.visibility = View.GONE
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