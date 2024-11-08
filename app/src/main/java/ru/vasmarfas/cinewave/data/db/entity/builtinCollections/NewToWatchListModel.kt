package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewToWatchListModel(
    @PrimaryKey
    @ColumnInfo(name = "toWatchFilmId")
    var toWatchFilmId: Int
)
