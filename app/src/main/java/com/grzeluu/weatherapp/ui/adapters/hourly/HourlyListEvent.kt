package com.grzeluu.weatherapp.ui.adapters.hourly

sealed class HourlyListEvent<T> {
    data class onItemClick(val position: Int) : HourlyListEvent<Any?>()
    object onStart : HourlyListEvent<Any?>()
}