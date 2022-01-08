package com.grzeluu.weatherapp.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grzeluu.weatherapp.model.Coord

@Entity(tableName = "cities")
data class DBCity(
    @PrimaryKey
    val id: String,

    val name: String,

    val state: String,

    val country: String,

    @Embedded
    val coord: Coord
)