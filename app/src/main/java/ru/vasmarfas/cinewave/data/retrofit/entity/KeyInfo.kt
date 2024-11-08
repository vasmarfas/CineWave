package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class KeyInfo(
    @Json(name ="totalQuota") val totalQuota: KeyQuota?,
    @Json(name ="dailyQuota") val dailyQuota: KeyQuota?,
    @Json(name ="accountType") val accountType: String,
    )
