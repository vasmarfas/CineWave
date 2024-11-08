package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interestModel")
data class InterestModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    var itemId: Int = 0,
    @ColumnInfo(name = "kinopoiskId")
    var kinopoiskId: Int,
    @ColumnInfo(name = "isFilm")
    var isFilm: Boolean,
    @ColumnInfo(name = "isPerson")
    var isPerson: Boolean,
    @ColumnInfo(name = "posterUrl")
    var posterUrl: String,
    @ColumnInfo(name = "title")
    var title: String

)
