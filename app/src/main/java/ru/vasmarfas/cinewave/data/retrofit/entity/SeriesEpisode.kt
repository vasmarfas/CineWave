package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class SeriesEpisode(
    @Json(name = "seasonNumber") val seasonNumber: Int,
    @Json(name = "episodeNumber") val episodeNumber: Int,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "synopsis") val synopsis: String?,
    @Json(name = "releaseDate") val releaseDate: String,


    )