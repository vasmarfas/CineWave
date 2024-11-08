package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimilarFilmsResult(
    @Json(name ="total") val total: Int,
    @Json(name ="items") val items: List<SimilarFilm>,
)
