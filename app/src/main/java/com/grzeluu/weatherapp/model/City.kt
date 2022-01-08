package com.grzeluu.weatherapp.model

data class City(
    val id: String,
    val name: String,
    val state: String,
    val country: String,
    val coord: Coord
)
