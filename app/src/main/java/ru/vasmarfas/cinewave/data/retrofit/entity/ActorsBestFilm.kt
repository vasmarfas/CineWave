package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class ActorsBestFilm(
    @Json(name ="filmId") val kinopoiskId: Int,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "general") val general: Boolean?,
    @Json(name = "description") val description: String?,
    @Json(name = "professionKey") val professionKey: String?,
    )