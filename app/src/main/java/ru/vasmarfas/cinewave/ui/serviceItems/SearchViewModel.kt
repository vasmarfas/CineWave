package ru.vasmarfas.cinewave.ui.serviceItems

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.vasmarfas.cinewave.domain.MainRepositoryInterface
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MainRepositoryInterface) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.NoTextEnough)
    val state = _state.asStateFlow()
    var searchAdapterData: MutableLiveData<List<Film>> = MutableLiveData(emptyList())
    val searchString = MutableStateFlow("")
    val isResultsEmpty = MutableLiveData<Boolean>(false)
    var curType: MutableLiveData<String> = MutableLiveData("ALL")
    var curCountry: MutableLiveData<Int?> = MutableLiveData(null)
    var curGenre:  MutableLiveData<Int?> = MutableLiveData(null)
    var curYearFrom:  MutableLiveData<Int> = MutableLiveData(1850)
    var curYearTo: MutableLiveData<Int> = MutableLiveData(Calendar.getInstance().get(Calendar.YEAR))
    var curRatingFrom: MutableLiveData<Int> = MutableLiveData(0)
    var curRatingTo: MutableLiveData<Int> = MutableLiveData(10)
    var curOrderBy: MutableLiveData<String> = MutableLiveData("YEAR")
    var hideViewed: MutableLiveData<Boolean> = MutableLiveData(false)
    val idGenresMap = mapOf(
        null to "любой",
        1 to "триллер",
        2 to "драма",
        3 to "криминал",
        4 to "мелодрама",
        5 to "детектив",
        6 to "фантастика",
        7 to "приключения",
        8 to "биография",
        9 to "фильм-нуар",
        10 to "вестерн",
        11 to "боевик",
        12 to "фэнтези",
        13 to "комедия",
        14 to "военный",
        15 to "история",
        16 to "музыка",
        17 to "ужасы",
        18 to "мультфильм",
        19 to "семейный",
        20 to "мюзикл",
        21 to "спорт",
        22 to "документальный",
        23 to "короткометражка",
        24 to "аниме",
        26 to "новости",
        27 to "концерт",
        28 to "для взрослых",
        29 to "церемония",
        30 to "реальное ТВ",
        31 to "игра",
        32 to "ток-шоу",
        33 to "детский"
    )
    val idCountryMap = mapOf(
        null to "любая",
        1 to "США",
        2 to "Швейцария",
        3 to "Франция",
        4 to "Польша",
        5 to "Великобритания",
        6 to "Швеция",
        7 to "Индия",
        8 to "Испания",
        9 to "Германия",
        10 to "Италия",
        11 to "Гонконг",
        12 to "Германия (ФРГ)",
        13 to "Австралия",
        14 to "Канада",
        15 to "Мексика",
        16 to "Япония",
        17 to "Дания",
        18 to "Чехия",
        19 to "Ирландия",
        20 to "Люксембург",
        21 to "Китай",
        22 to "Норвегия",
        23 to "Нидерланды",
        24 to "Аргентина",
        25 to "Финляндия",
        26 to "Босния и Герцеговина",
        27 to "Австрия",
        28 to "Тайвань",
        29 to "Новая Зеландия",
        30 to "Бразилия",
        31 to "Чехословакия",
        32 to "Мальта",
        33 to "СССР",
        34 to "Россия",
        35 to "Югославия",
        36 to "Португалия",
        37 to "Румыния",
        38 to "Хорватия",
        39 to "ЮАР",
        40 to "Куба",
        41 to "Колумбия",
        42 to "Израиль",
        43 to "Намибия",
        44 to "Турция",
        45 to "Бельгия",
        46 to "Сальвадор",
        47 to "Исландия",
        48 to "Венгрия",
        49 to "Корея Южная",
        50 to "Лихтенштейн",
        51 to "Болгария",
        52 to "Филиппины",
        53 to "Доминикана",
        55 to "Марокко",
        56 to "Таиланд",
        57 to "Кения",
        58 to "Пакистан",
        59 to "Иран",
        60 to "Панама",
        61 to "Аруба",
        62 to "Ямайка",
        63 to "Греция",
        64 to "Тунис",
        65 to "Кыргызстан",
        66 to "Пуэрто Рико",
        67 to "Казахстан",
        69 to "Алжир",
        71 to "Сингапур"
    )


    init {
        searchString.debounce(500).onEach {
            //Log.d("ViewModel Debug", it)
            if(it.length >= 3){
                viewModelScope.launch {
                    _state.value = State.Loading
                    val type: String = curType.value?: "ALL"
                    val country = curCountry.value
                    val countries: Array<Int>? = if (country!=null) arrayOf(country) else null
                    val genre = curGenre.value
                    val genres: Array<Int>? = if (genre!=null) arrayOf(genre) else null
                    runCatching {
                        KinopoiskAPI.RetrofitInstance.getKinoAPI.films(
                            keyword = it,
                            type = type,
                            countries = countries,
                            genres = genres,
                            yearFrom = curYearFrom.value?: 1850,
                            yearTo = curYearTo.value?: Calendar.getInstance().get(Calendar.YEAR),
                            ratingFrom = curRatingFrom.value?: 0,
                            ratingTo = curRatingTo.value?: 10,
                            order = curOrderBy.value?: "YEAR"

                        )
                    }.fold(
                        onSuccess = {
                            val filmList = it.body()?.items ?: emptyList()
                            if (hideViewed.value == true) {
                                val newList: MutableList<Film> = mutableListOf()
                                val viewedIds = getViewedFilms
                                //Log.d("DEBUG SEARCHVIEWMODEL", viewedIds.toString())
                                for (film in filmList) {
                                    if (!viewedIds.contains(film.kinopoiskId)) {
                                        newList.add(film)
                                    }
                                }
                                searchAdapterData.value = newList.toList()
                            } else {
                                searchAdapterData.value = filmList
                            }

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

    val getViewedFilms: List<Int>
        get() = runBlocking {
            return@runBlocking withContext(Dispatchers.IO) {
                repository.getViewedFilmIds()
            }
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
