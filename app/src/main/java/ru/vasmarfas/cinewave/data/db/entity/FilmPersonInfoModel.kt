package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filmPersonInfoModel")
data class FilmPersonInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    var itemId: Int = 0,
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "posterUrl")
    var posterUrl: String,
    @ColumnInfo(name = "isFilm")
    var isFilm: Boolean,
    @ColumnInfo(name = "isPerson")
    var isPerson: Boolean,
    @ColumnInfo(name = "title")
    var title: String


)
