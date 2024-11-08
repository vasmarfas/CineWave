package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favouriteListModel")
data class FavouriteListModel(
    @PrimaryKey
    @ColumnInfo(name = "favouriteFilmId")
    var favouriteFilmId: Int
)
