package ru.vasmarfas.cinewave.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.domain.MainRepositoryInterface
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: UserDao
): MainRepositoryInterface {
    override suspend fun getCollections(): List<CollectionsModel> {
//        Log.e("DEBUG REPOSITORY - collections", dao.getCollections().toString())
        return dao.getCollections()
    }
    override suspend fun getViewedFilmIds(): List<Int> {
        Log.e("DEBUG REPOSITORY - getAllViewedFilmIds", dao.getAllViewedFilmIds().toString())
        return dao.getAllViewedFilmIds()
    }
    override fun getCollectionsLiveData(): LiveData<List<CollectionsModel>> {
        return dao.getCollectionsLiveData()
    }
    override fun getSingleCollectionsLiveData(): LiveData<CollectionsModel> {
        return dao.getSingleCollectionsLiveData()
    }
    override fun getAllCollectionsMutableLiveData(): MutableLiveData<List<Any>> {
        val returnValue: MutableList<List<Any>> = mutableListOf()
        dao.getAllFavouriteFilmModelsLiveData().value?.let { returnValue.add(it) }
        dao.getAllToWatchFilmModelsLiveData().value?.let { returnValue.add(it) }
        dao.getAllViewedFilmModelsLiveData().value?.let { returnValue.add(it) }
        dao.getCollectionsLiveData().value?.let { returnValue.add(it) }
        val outList = returnValue.toList()
        val outValue = MutableLiveData<List<Any>>(outList)
        return outValue
    }
}