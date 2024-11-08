package ru.vasmarfas.cinewave.data.retrofit

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.vasmarfas.cinewave.data.retrofit.entity.Collection
import ru.vasmarfas.cinewave.data.retrofit.entity.CollectionSeriesSeasons
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters
import ru.vasmarfas.cinewave.data.retrofit.entity.ImagesResult
import ru.vasmarfas.cinewave.data.retrofit.entity.PremieresResult
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilmsResult
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffSinglePerson


private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

class KinopoiskAPI {

    object RetrofitInstance {
        val client = OkHttpClient.Builder()
            .addInterceptor(APIKeyInterceptor())
            .build()
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
        val getKinoAPI: GetKinoAPI = retrofit.create(
            GetKinoAPI::class.java
        )
    }

    // Нужно описать выходные данные
    interface GetKinoAPI {
        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/premieres")
        suspend fun premieres(
            @Query("year") year: Int,
            @Query("month") month: String,
        ): Response<PremieresResult>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/collections?type=TOP_POPULAR_MOVIES")
        suspend fun popularFilms(
            @Query("page") page: Int = 1,
        ): Response<Collection>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films")
        suspend fun films(
            @Query("page") page: Int = 1,
            @Query("countries") countries: Array<Int>? = null,
            @Query("genres") genres: Array<Int>? = null,
            @Query("order") order: String = "RATING",
            @Query("type") type: String = "ALL",
            @Query("ratingFrom") ratingFrom: Int = 0,
            @Query("ratingTo") ratingTo: Int = 10,
            @Query("yearFrom") yearFrom: Int = 1000,
            @Query("yearTo") yearTo: Int = 3000,
            @Query("imdbId") imdbId: String? = null,
            @Query("keyword") keyword: String? = null,
            ): Response<Collection>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/filters")
        suspend fun filters(
        ): Response<Filters>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/collections")
        suspend fun top250List(
            @Query("page") page: Int = 1,
            @Query("type") type: String = "TOP_250_MOVIES",
        ): Response<Collection>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films?type=TV_SERIES")
        suspend fun series(
            @Query("page") page: Int = 1,
            @Query("type") type: String = "TV_SERIES",
            @Query("order") order: String = "RATING",
        ): Response<Collection>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/{id}")
        suspend fun filmById(
            @Path("id") id: Int,
        ): Response<Film>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v1/staff")
        suspend fun staffByFilmId(
            @Query("filmId") filmId: Int,
        ): Response<List<StaffPerson>>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v1/staff/{id}")
        suspend fun staffByPersonId(
            @Path("id") id: Int,
        ): Response<StaffSinglePerson>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/{id}/images")
        suspend fun imagesByFilmId(
            @Path("id") id: Int,
            @Query("page") page: Int = 1,
            @Query("type") type: String = "STILL",
        ): Response<ImagesResult>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/{id}/seasons")
        suspend fun seasons(
            @Path("id") id: Int,
        ): Response<CollectionSeriesSeasons>

        @Headers(
            "Accept: application/json",
            "Content-type: application/json"
        )
        @GET("/api/v2.2/films/{id}/similars")
        suspend fun similarFilmsByFilmId(
            @Path("id") id: Int,
        ): Response<SimilarFilmsResult>

    }
}

