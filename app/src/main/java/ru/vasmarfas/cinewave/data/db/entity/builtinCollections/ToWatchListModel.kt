package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toWatchListModel")
data class ToWatchListModel(
    @PrimaryKey
    @ColumnInfo(name = "toWatchFilmId")
    var toWatchFilmId: Int
)
