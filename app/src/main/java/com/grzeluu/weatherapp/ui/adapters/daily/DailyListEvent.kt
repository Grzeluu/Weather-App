package com.grzeluu.weatherapp.ui.adapters.daily


sealed class DailyListEvent<T> {
    data class onItemClick(val position: Int) : DailyListEvent<Any?>()
    object onStart : DailyListEvent<Any?>()
}
