package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class KeyQuota(
    @Json(name ="value") val value: Int,
    @Json(name ="used") val used: Int,
    )
