package com.grzeluu.weatherapp.ui.adapters.daily

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.model.Daily
import com.grzeluu.weatherapp.util.DateUtils.Companion.unixDay
import com.grzeluu.weatherapp.util.NetworkUtils
import com.grzeluu.weatherapp.util.WeatherIconProvider.Companion.setWeatherIcon
import kotlinx.android.synthetic.main.item_daily_forecast.view.*

class DailyAdapter : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtilCallback()) {

    class DailyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val minTemp = root.tv_min_temp
        val maxTemp = root.tv_max_temp
        val pop = root.tv_probability_of_precipitation
        val icon = root.iv_daily_weather
        val date = root.tv_date
        val item_view = root.item_daily
        val ll_expandable = root.ll_expandable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DailyViewHolder(inflater.inflate(R.layout.item_daily_forecast, parent, false))
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        getItem(position).let { daily ->
            holder.maxTemp.text = "${daily.temp.max.toInt()}°C"
            holder.minTemp.text = "${daily.temp.min.toInt()}°C"
            holder.date.text = unixDay(daily.dt)
            holder.icon.setWeatherIcon(daily.weather[0].icon)

            if (daily.pop > 0) {
                holder.pop.text = "${(daily.pop * 100).toInt()}%"
                holder.pop.visibility = View.VISIBLE
            } else {
                holder.pop.visibility = View.GONE
            }

            holder.item_view.setOnClickListener {
                holder.ll_expandable.visibility = View.VISIBLE
            }
        }
    }


}