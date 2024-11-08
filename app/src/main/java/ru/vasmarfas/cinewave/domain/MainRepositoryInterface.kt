package ru.vasmarfas.cinewave.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel

interface MainRepositoryInterface {
    suspend fun getCollections(): List<CollectionsModel>
    suspend fun getViewedFilmIds(): List<Int>
    fun getCollectionsLiveData(): LiveData<List<CollectionsModel>>
    fun getAllCollectionsMutableLiveData(): MutableLiveData<List<Any>>
    fun getSingleCollectionsLiveData(): LiveData<CollectionsModel>
}