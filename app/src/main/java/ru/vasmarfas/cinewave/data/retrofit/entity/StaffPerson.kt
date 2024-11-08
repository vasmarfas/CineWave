package ru.vasmarfas.cinewave.data.retrofit.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class StaffPerson(
    @Json(name ="staffId") val staffId: Int,
    @Json(name ="nameRu") val nameRu: String? = null,
    @Json(name ="nameEn") val nameEn: String? = null,
    @Json(name ="description") val description: String?= null,
    @Json(name ="posterUrl") val posterUrl: String,
    @Json(name ="professionText") val professionText: String,
    @Json(name ="professionKey") val professionKey: String,

)
