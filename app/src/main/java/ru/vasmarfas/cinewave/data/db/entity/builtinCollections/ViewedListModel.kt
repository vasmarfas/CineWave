package ru.vasmarfas.cinewave.data.db.entity.builtinCollections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewedListModel")
data class ViewedListModel(
    @PrimaryKey
    @ColumnInfo(name = "viewedFilmId")
    var viewedFilmId: Int
)
