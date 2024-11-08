package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class SimilarFilm(
    @Json(name ="filmId") val kinopoiskId: Int,
    @Json(name = "nameRu") val nameRu: String? = null,
    @Json(name = "nameEn") val nameEn: String? = null,
    @Json(name = "nameOriginal") val nameOriginal: String? = null,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "posterUrlPreview") val posterUrlPreview: String,
    @Json(name = "relationType") val relationType: String

    )