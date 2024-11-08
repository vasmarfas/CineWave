package ru.vasmarfas.cinewave.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters
import ru.vasmarfas.cinewave.data.db.entity.AuthTokenModel
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.db.entity.FilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.FilmPersonInfoModel
import ru.vasmarfas.cinewave.data.db.entity.InterestModel
import ru.vasmarfas.cinewave.data.db.entity.NewCollectionsModel
import ru.vasmarfas.cinewave.data.db.entity.NewFilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.NewFilmPersonInfoModel
import ru.vasmarfas.cinewave.data.db.entity.NewInterestModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.FavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewFavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewViewedListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ViewedListModel

@Dao
interface UserDao {
    @Update
    suspend fun updateAuthToken(authToken: AuthTokenModel)
    @Insert(entity = AuthTokenModel::class)
    suspend fun saveAuthToken(authToken: AuthTokenModel)
    @Query("SELECT * FROM authTokenModel")
    fun getAuthToken(): List<AuthTokenModel>

    @Update
    suspend fun updateFilters(filters: FilmFiltersModel)
    @Insert(entity = FilmFiltersModel::class)
    suspend fun saveFilters(filters: NewFilmFiltersModel)
    @Query("SELECT * FROM filmfiltersmodel")
    fun getFilters(): List<Filters>

    @Update
    suspend fun updateCollection(collection: CollectionsModel)
    @Insert(entity = CollectionsModel::class)
    suspend fun saveCollection(collection: NewCollectionsModel)
    @Query("SELECT * FROM collectionsModel")
    fun getCollections(): List<CollectionsModel>
    @Query("SELECT * FROM collectionsModel")
    fun getCollectionsLiveData(): LiveData<List<CollectionsModel>>
    @Query("SELECT * FROM collectionsModel")
    fun getSingleCollectionsLiveData(): LiveData<CollectionsModel>
    @Query("DELETE FROM collectionsModel WHERE collectionName = :collectionName")
    fun deleteCollection(collectionName: String)

    @Insert(entity = ViewedListModel::class)
    suspend fun addToViewedFilmIds(filmId: NewViewedListModel)
    @Query("SELECT * FROM viewedListModel")
    fun getAllViewedFilmIds(): List<Int>

    @Query("SELECT * FROM viewedListModel")
    fun getAllViewedFilmModels(): List<ViewedListModel>
    @Query("SELECT * FROM viewedListModel")
    fun getAllViewedFilmModelsLiveData(): LiveData<List<ViewedListModel>>

    @Query("DELETE FROM viewedListModel WHERE viewedFilmId = :filmId")
    fun deleteFromViewedFilmIds(filmId: Int)

    @Insert(entity = ToWatchListModel::class)
    suspend fun addToToWatchFilmIds(filmId: NewToWatchListModel)
    @Query("SELECT * FROM toWatchListModel")
    fun getAllToWatchFilmIds(): List<Int>
    @Query("SELECT * FROM toWatchListModel")
    fun getAllToWatchFilmModels(): List<ToWatchListModel>
    @Query("SELECT * FROM toWatchListModel")
    fun getAllToWatchFilmModelsLiveData(): LiveData<List<ToWatchListModel>>
    @Query("DELETE FROM toWatchListModel WHERE toWatchFilmId = :filmId")
    fun deleteFromToWatchFilmIds(filmId: Int)

    @Insert(entity = FavouriteListModel::class)
    suspend fun addToFavouriteFilmIds(filmId: NewFavouriteListModel)
    @Query("SELECT * FROM favouriteListModel")
    fun getAllFavouriteFilmIds(): List<Int>

    @Query("SELECT * FROM favouriteListModel")
    fun getAllFavouriteFilmModels(): List<FavouriteListModel>
    @Query("SELECT * FROM favouriteListModel")
    fun getAllFavouriteFilmModelsLiveData(): LiveData<List<FavouriteListModel>>
    @Query("DELETE FROM favouriteListModel WHERE favouriteFilmId = :filmId")
    fun deleteFromFavouriteFilmIds(filmId: Int)

    @Update
    suspend fun updateInterest(collection: InterestModel)
    @Insert(entity = InterestModel::class)
    suspend fun saveInterest(collection: NewInterestModel)
    @Query("SELECT * FROM interestModel")
    fun getInterest(): List<InterestModel>

    @Query("SELECT * FROM interestModel ORDER BY itemId DESC")
    fun getInterestDesc(): List<InterestModel>

    @Query("SELECT * FROM interestModel WHERE kinopoiskId = :kinopoiskId")
    fun getInterestById(kinopoiskId: Int): InterestModel?

    @Update
    suspend fun updateFilmPersonInfo(collection: FilmPersonInfoModel)
    @Insert(entity = FilmPersonInfoModel::class)
    suspend fun saveFilmPersonInfo(collection: NewFilmPersonInfoModel)
    @Query("SELECT * FROM filmPersonInfoModel")
    fun getFilmPersonInfo(): List<FilmPersonInfoModel>
    @Query("SELECT * FROM filmPersonInfoModel WHERE id = :infoId")
    fun getFilmPersonInfoById(infoId: Int): FilmPersonInfoModel?
}
