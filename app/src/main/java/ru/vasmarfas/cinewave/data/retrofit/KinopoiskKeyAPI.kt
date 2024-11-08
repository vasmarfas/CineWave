package ru.vasmarfas.cinewave.data.retrofit

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import ru.vasmarfas.cinewave.data.retrofit.entity.KeyInfo

private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"
class KinopoiskKeyAPI {

    object RetrofitInstance {

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val getKeyAPI: GetKeyAPI = retrofit.create(
            GetKeyAPI::class.java
        )
    }

    interface GetKeyAPI {
        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v1/api_keys/{key}")
        suspend fun getApiKeyInfo(
            @Path("key") key: String,
            @Header("X-API-KEY") api_key: String
        ): Response<KeyInfo>
    }
}

