package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters

data class NewFilmFiltersModel(
    @PrimaryKey
    @ColumnInfo(name = "filters")
    var filters: Filters
)
