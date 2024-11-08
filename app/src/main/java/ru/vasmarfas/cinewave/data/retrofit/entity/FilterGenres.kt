package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class FilterGenres(
    @Json(name ="id") val id: Int,
    @Json(name ="genre") val genre: String
)
