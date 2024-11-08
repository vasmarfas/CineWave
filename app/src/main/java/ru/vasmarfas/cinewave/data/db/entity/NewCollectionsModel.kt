package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewCollectionsModel(
    @PrimaryKey
    @ColumnInfo(name = "collectionName")
    var collectionName: String,
    @ColumnInfo(name = "filmIds")
    var filmIds: List<Int>
)
