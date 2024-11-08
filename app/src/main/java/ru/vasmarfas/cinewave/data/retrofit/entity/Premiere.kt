package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class Premiere(
    @Json(name = "kinopoiskId") val kinopoiskId: Int,
    @Json(name = "nameRu") val nameRu: String,
    @Json(name = "nameEn") val nameEn: String,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "posterUrlPreview") val posterUrlPreview: String,
    @Json(name = "year") val year: Int,
    @Json(name = "countries") val countries: List<FilmCountry>,
    @Json(name = "genres") val genres: List<FilmGenre>,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "premiereRu") val premiereRu: String

    )