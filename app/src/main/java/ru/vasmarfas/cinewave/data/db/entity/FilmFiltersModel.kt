package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters

@Entity(tableName = "filmFiltersModel")
data class FilmFiltersModel(
    @PrimaryKey
    @ColumnInfo(name = "filters")
    var filters: Filters
)
