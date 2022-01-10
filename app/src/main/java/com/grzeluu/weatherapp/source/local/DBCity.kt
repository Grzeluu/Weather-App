package com.grzeluu.weatherapp.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grzeluu.weatherapp.model.Coord

@Entity(tableName = "city_data_table")
data class DBCity(
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    val id: String,

    @ColumnInfo(name = "city_name")
    val name: String,

    @ColumnInfo(name = "city_state")
    val state: String,

    @ColumnInfo(name = "city_state")
    val country: String,

    @Embedded
    @ColumnInfo(name = "city_coord")
    val coord: Coord
)