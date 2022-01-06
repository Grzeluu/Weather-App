package com.grzeluu.weatherapp.ui.adapters.hourly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grzeluu.weatherapp.R
import com.grzeluu.weatherapp.model.Hourly
import com.grzeluu.weatherapp.util.Utils
import com.grzeluu.weatherapp.util.Utils.setWeatherIcon
import kotlinx.android.synthetic.main.item_hourly_forecast.view.*

class HourlyAdapter(val event: MutableLiveData<HourlyListEvent<Hourly>> = MutableLiveData()) :
    ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtilCallback()) {

    class HourlyViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val temp: TextView = root.tv_min_temp
        val time: TextView = root.tv_time
        val icon: ImageView = root.iv_weather
        val pop: TextView = root.tv_probability_of_precipitation
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HourlyViewHolder(inflater.inflate(R.layout.item_hourly_forecast, parent, false))
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        getItem(position).let { hourly ->
            holder.temp.text = "${hourly.temp.toInt()}°C"
            holder.time.text = Utils.unixTime(hourly.dt)
            holder.icon.setWeatherIcon(hourly.weather[0].icon)
            if (hourly.pop > 0) {
                holder.pop.text = "${(hourly.pop * 100).toInt()}%"
                holder.pop.visibility = View.VISIBLE
            } else {
                holder.pop.visibility = View.GONE
            }
        }
    }
}