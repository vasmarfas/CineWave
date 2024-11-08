package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffSinglePerson(
    @Json(name ="personId") val personId: Int,
    @Json(name ="webUrl") val webUrl: String?,
    @Json(name ="nameRu") val nameRu: String?,
    @Json(name ="nameEn") val nameEn: String?,
    @Json(name ="sex") val sex: String?,
    @Json(name ="posterUrl") val posterUrl: String,
    @Json(name ="profession") val profession: String?,
    @Json(name ="facts") val facts: List<String>?,
    @Json(name ="films") val films: List<ActorsBestFilm>?,

    )
