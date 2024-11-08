package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewFavouriteListModel(
    @PrimaryKey
    @ColumnInfo(name = "favouriteFilmId")
    var favouriteFilmId: Int
)
