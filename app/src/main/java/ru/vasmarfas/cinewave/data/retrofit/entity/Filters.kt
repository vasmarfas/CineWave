package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class Filters(
    @Json(name ="genres") val genres: List<FilterGenres>?,
    @Json(name ="countries") val countries: List<FilterCountries>?
)
