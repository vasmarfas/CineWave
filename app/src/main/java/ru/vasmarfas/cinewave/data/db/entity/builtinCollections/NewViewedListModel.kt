package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewViewedListModel(
    @PrimaryKey
    @ColumnInfo(name = "viewedFilmId")
    var viewedFilmId: Int
)
