package ru.vasmarfas.cinewave.ui.serviceItems

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import ru.vasmarfas.cinewave.domain.MainRepositoryInterface
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepositoryInterface) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.NoTextEnough)
    var searchAdapterData: MutableLiveData<List<Film>> = MutableLiveData(emptyList())
    val searchString = MutableStateFlow("")
    val isResultsEmpty = MutableLiveData<Boolean>(false)


    init {
        searchString.debounce(500).onEach {
            //Log.d("ViewModel Debug", it)
            if(it.length >= 3){
                viewModelScope.launch {
                    _state.value = State.Loading
                    runCatching {
                        KinopoiskAPI.RetrofitInstance.getKinoAPI.films(
                            keyword = it
                        )
                    }.fold(
                        onSuccess = {
                            val filmList = it.body()?.items ?: emptyList()
                            searchAdapterData.value = filmList

                            if (filmList.isEmpty()) {
                                isResultsEmpty.value= true
                            } else {
                                isResultsEmpty.value = false
                            }

                        },
                        onFailure = { Log.e("DEBUG", it.message ?: "") }
                    )
                    _state.value = State.Success
                }
            }
        }.launchIn(viewModelScope + Dispatchers.Default)
    }



    val getCollections: List<CollectionsModel>
        get() = runBlocking {
            var data: List<CollectionsModel> = emptyList()
            launch(Dispatchers.IO) {
                data = repository.getCollections()
            }
            return@runBlocking data
        }

    val getCollectionsLiveData: LiveData<List<CollectionsModel>>
        get() = repository.getCollectionsLiveData()
    val getSingleCollectionsLiveData: LiveData<CollectionsModel>
        get() = repository.getSingleCollectionsLiveData()

    val getAllCollections: MutableLiveData<List<Any>>
        get() = repository.getAllCollectionsMutableLiveData()
}
