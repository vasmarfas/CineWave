package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class ImageItem(
    @Json(name ="imageUrl") val imageUrl: String,
    @Json(name ="previewUrl") val previewUrl: String
)
