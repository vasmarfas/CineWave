package ru.vasmarfas.cinewave.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.App
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.ActorsBestFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentItemFilmBinding
import ru.vasmarfas.cinewave.databinding.FragmentItemShowAllBinding

private const val PREMIERE_ITEM = 0
private const val FILM_ITEM = 1
private const val SHOW_ALL_ITEM = 2
private const val SIMILAR_FILM_ITEM = 3
private const val ACTORS_BEST_FILM_ITEM = 4
private const val STAFF_ITEM = 5

class FilmItemAdapter(context:  Context,
    private val onClick: (Any) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val dao = App().getInstance(context).userDao()
    val curContext = context
//    @Inject
//    lateinit var dao: UserDao
    data class ShowAllItemPlaceholder(val text: String)
    var constantData: List<Any> = emptyList()
    var data: List<Any> = emptyList()
        set(newValue) {
            if (constantData.isEmpty()) {
                constantData = newValue
            }
            if (newValue.isEmpty()) {
                field = emptyList()
            }
            else {


                if (newValue[0] == "allFilmsFragment") {
                    val tempvar = newValue.toMutableList()
                    tempvar.removeAt(0)
                    field = tempvar.toList()
                } else if (newValue.last() is ActorsBestFilm) {

                    val tempvar = mutableListOf<ActorsBestFilm>()
                    for (film in newValue) {
                        if (film is ActorsBestFilm &&  film.rating!=null) {
                            tempvar.add(film)
                        }
                    }
                    //Log.d("DEBUG SORTING", tempvar.toString())
                    tempvar.sortByDescending {
                        it.rating!!.toDouble()
                    }
                    //Log.d("DEBUG SORTING", tempvar.toString())
                    val outVar = if (tempvar.size>10) tempvar.take(10).toList() else tempvar.toList()

                    field = outVar

                } else {
                        field =
                            if (newValue.isNotEmpty() && newValue.last() != ShowAllItemPlaceholder("test") && newValue.size > 20) {
                                newValue.take(20).plus(ShowAllItemPlaceholder("test"))
                            } else if (newValue.isNotEmpty() && newValue.last() != ShowAllItemPlaceholder(
                                    "test"
                                )
                            ) {
                                newValue.plus(ShowAllItemPlaceholder("test"))
                            } else {
                                newValue
                            }
                    }
                }

//            notifyDataSetChanged()

        }
//        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }



    class FilmViewHolder(val binding: FragmentItemFilmBinding) :
        RecyclerView.ViewHolder(binding.root)
    class ShowAllItemViewHolder(val binding: FragmentItemShowAllBinding) :
        RecyclerView.ViewHolder(binding.root)
//    {
//        val buttonShowAll: ImageButton = itemView.findViewById(R.id.buttonShowAll)
//        val textShowAll: TextView = itemView.findViewById(R.id.textShowAll)
//
//    }



    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Premiere -> PREMIERE_ITEM
            is Film -> FILM_ITEM
            is ShowAllItemPlaceholder -> SHOW_ALL_ITEM
            is SimilarFilm -> SIMILAR_FILM_ITEM
            is ActorsBestFilm -> ACTORS_BEST_FILM_ITEM
            is StaffPerson -> STAFF_ITEM
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PREMIERE_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentItemFilmBinding.inflate(inflater, parent, false)
                FilmViewHolder(binding)
            }
            FILM_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentItemFilmBinding.inflate(inflater, parent, false)
                FilmViewHolder(binding)
            }
            SHOW_ALL_ITEM -> {
                //Log.d("SHOWALLITEM DEBUG", "CreateView")

                val inflater = LayoutInflater.from(parent.context)
                val itemView = FragmentItemShowAllBinding.inflate(inflater, parent, false)
                ShowAllItemViewHolder(itemView)
            }
            SIMILAR_FILM_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentItemFilmBinding.inflate(inflater, parent, false)
                FilmViewHolder(binding)
            }
            ACTORS_BEST_FILM_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentItemFilmBinding.inflate(inflater, parent, false)
                FilmViewHolder(binding)
            }
            STAFF_ITEM -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = FragmentItemFilmBinding.inflate(inflater, parent, false)
                FilmViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }



     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Log.d("Debug Adapter", data.last().toString())

        when (holder.itemViewType) {
            PREMIERE_ITEM -> processPremiereItem(holder, position)
            FILM_ITEM ->  processFilmItem(holder, position)
            SIMILAR_FILM_ITEM ->  processSimilarFilmItem(holder, position)
            ACTORS_BEST_FILM_ITEM -> processActorsBestFilmItem(holder, position)
            STAFF_ITEM -> processStaffItem(holder, position)

            SHOW_ALL_ITEM -> {
                val showAllItemHolder = holder as ShowAllItemViewHolder
                showAllItemHolder.binding.root.setOnClickListener {
                    onClick(showAllItemHolder)
                }
                showAllItemHolder.binding.buttonShowAll.setOnClickListener {
                    onClick(showAllItemHolder)
                }
            }
        }

    }
    fun processPremiereItem(holder: RecyclerView.ViewHolder, position: Int) {
        val premiere = data[position] as Premiere
        val premiereHolder = holder as FilmViewHolder
        with(premiereHolder.binding) {
            filmName.text = premiere.nameRu
            filmGenre.text = premiere.genres[0].genre
            filmRating.text = ""
            filmRatingVector.visibility = View.GONE


            Glide
                .with(filmCover.context)
                .load(premiere.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(filmCover)
            CoroutineScope(Dispatchers.IO).launch {
                if (dao.getAllViewedFilmIds().contains(premiere.kinopoiskId)){
                    launch(Dispatchers.Main) {
                        gradientMask.visibility = View.VISIBLE

                    }
                }
            }
        }
        premiereHolder.binding.root.setOnClickListener{
            onClick(premiere)
        }

    }
    fun processFilmItem(holder: RecyclerView.ViewHolder, position: Int) {
        val film = data[position] as Film
        val filmHolder = holder as FilmViewHolder
        with(filmHolder.binding) {
            filmName.text = when {
                film.nameRu!=null -> film.nameRu
                film.nameEn!=null -> film.nameEn
                film.nameOriginal!=null -> film.nameOriginal
                else -> "Film"
            }
            filmGenre.text = film.genres[0].genre

            if (film.ratingKinopoisk != null) {
                filmRating.text = film.ratingKinopoisk.toString()
            } else if (film.ratingImdb != null) {
                filmRating.text = film.ratingImdb.toString()

            } else{
                filmRating.text = ""
                filmRatingVector.visibility = View.GONE
            }
            Glide
                .with(filmCover.context)
                .load(film.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(filmCover)

            CoroutineScope(Dispatchers.IO).launch {
                if (dao.getAllViewedFilmIds().contains(film.kinopoiskId)){
                    launch(Dispatchers.Main) {
                        gradientMask.visibility = View.VISIBLE

                    }
                }
            }
        }

        filmHolder.binding.root.setOnClickListener{
            onClick(film)
        }
    }
    fun processSimilarFilmItem(holder: RecyclerView.ViewHolder, position: Int) {
        val film = data[position] as SimilarFilm
        val filmHolder = holder as FilmViewHolder
        with(filmHolder.binding) {
            filmName.text = when {
                film.nameRu!=null -> film.nameRu
                film.nameEn!=null -> film.nameEn
                film.nameOriginal!=null -> film.nameOriginal
                else -> "Film"
            }
            filmGenre.visibility = View.GONE


            filmRating.text = ""
            filmRatingVector.visibility = View.GONE

            Glide
                .with(filmCover.context)
                .load(film.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(filmCover)
            CoroutineScope(Dispatchers.IO).launch {
                if (dao.getAllViewedFilmIds().contains(film.kinopoiskId)){
                    launch(Dispatchers.Main) {
                        gradientMask.visibility = View.VISIBLE

                    }
                }
            }
        }

        filmHolder.binding.root.setOnClickListener{
            onClick(film)
        }
    }
    fun processActorsBestFilmItem(holder: RecyclerView.ViewHolder, position: Int) {
        val film = data[position] as ActorsBestFilm
        val filmHolder = holder as FilmViewHolder
        //Log.d("DEBUG BestFilmItem", film.toString())
        with(filmHolder.binding) {
            filmName.text = when {
                film.nameRu!=null -> film.nameRu
                film.nameEn!=null -> film.nameEn
                else -> "Film"
            }
            filmGenre.text = when {
                film.description !=null -> film.description
                film.professionKey!=null -> film.professionKey
                else -> ""
            }
            if (filmGenre.text =="") {
                filmGenre.visibility = View.GONE
            }


            filmRating.text = film.rating ?: ""
            if (filmRating.text =="") {
                filmRating.visibility = View.GONE
                filmRatingVector.visibility = View.GONE
            }
            if (filmCover.drawable ==  null)  {
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching {
                        KinopoiskAPI.RetrofitInstance.getKinoAPI.filmById(
                            film.kinopoiskId
                        )
                    }.fold(
                        onSuccess = {
                            val film2 = it.body()
                            if (film2 != null) {
                                launch(Dispatchers.Main) {
                                    Glide
                                        .with(filmCover.context)
                                        .load(film2.posterUrl)     //картинка из апи
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(filmCover)
                                }
                            }
                        },
                        onFailure = { Log.e("DEBUG", it.message ?: "")
                            launch(Dispatchers.Main) {
                                Toast.makeText(curContext,
                                    curContext.applicationContext.getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                            }

                        }
                    )
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                if (dao.getAllViewedFilmIds().contains(film.kinopoiskId)){
                    launch(Dispatchers.Main) {
                        gradientMask.visibility = View.VISIBLE

                    }
                }
            }

        }

        filmHolder.binding.root.setOnClickListener{
            onClick(film)
        }
    }
    fun processStaffItem(holder: RecyclerView.ViewHolder, position: Int) {
        val person = data[position] as StaffPerson
        val filmHolder = holder as FilmViewHolder
        //Log.d("DEBUG StaffItem", person.toString())
        with(filmHolder.binding) {
            filmName.text = when {
                person.nameRu!=null -> person.nameRu
                person.nameEn!=null -> person.nameEn
                else -> "Film"
            }
            filmGenre.text = when {
                person.description !=null -> person.description
                person.professionKey!=null -> person.professionKey
                else -> ""
            }
            if (filmGenre.text =="") {
                filmGenre.visibility = View.GONE
            }


            filmRating.visibility = View.GONE
            filmRatingVector.visibility = View.GONE

            if (filmCover.drawable ==  null)  {
                Glide
                    .with(filmCover.context)
                    .load(person.posterUrl)     //картинка из апи
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(filmCover)
            }
        }

        filmHolder.binding.root.setOnClickListener{
            onClick(person)
        }
    }

}
