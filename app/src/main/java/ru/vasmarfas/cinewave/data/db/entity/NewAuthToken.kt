package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewAuthToken(
    @PrimaryKey
    @ColumnInfo(name = "token")
    var token: String

)
