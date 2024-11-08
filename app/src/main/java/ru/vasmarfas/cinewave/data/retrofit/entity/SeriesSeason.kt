package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class SeriesSeason(
    @Json(name = "number") val number: Int,
    @Json(name = "episodes") val episodes: List<SeriesEpisode>

    )