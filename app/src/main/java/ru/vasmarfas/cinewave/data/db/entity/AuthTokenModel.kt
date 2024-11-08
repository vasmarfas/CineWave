package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authTokenModel")
data class AuthTokenModel(
    @PrimaryKey
    @ColumnInfo(name = "token")
    var token: String
)
