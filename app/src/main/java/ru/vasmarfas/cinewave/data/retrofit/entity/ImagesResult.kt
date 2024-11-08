package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImagesResult(
    @Json(name ="total") val total: Int,
    @Json(name ="totalPages") val totalPages: Int,
    @Json(name ="items") val items: List<ImageItem>
)
