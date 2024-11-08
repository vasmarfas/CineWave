package ru.vasmarfas.cinewave.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.databinding.FragmentItemSearchResultBinding

class SearchItemAdapter(
    context: Context,
    private val onClick: (Film) -> Unit
) : RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>() {
    //    @Inject
//    lateinit var dao: UserDao
    var data: MutableList<Film> = mutableListOf()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    //        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }
    fun submitList(newData: List<Film>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    class SearchItemViewHolder(val binding: FragmentItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root)
//        val buttonShowAll: ImageButton = itemView.findViewById(R.id.buttonShowAll)
//        val textShowAll: TextView = itemView.findViewById(R.id.textShowAll)
//
//    }


    override fun getItemCount(): Int = data.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemSearchResultBinding.inflate(inflater, parent, false)

        return SearchItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        //Log.d("Debug Adapter", data.last().toString())
        val film = data[position]
        with(holder.binding) {
            filmName.text = when {
                film.nameRu != null -> film.nameRu
                film.nameEn != null -> film.nameEn
                film.nameOriginal != null -> film.nameOriginal
                else -> "Film"
            }
            if (film.genres.isNotEmpty()){
                filmGenre.text = film.genres[0].genre
            } else {
                filmGenre.text = ""
            }

            if (film.ratingKinopoisk != null) {
                filmRating.text = film.ratingKinopoisk.toString()
            } else if (film.ratingImdb != null) {
                filmRating.text = film.ratingImdb.toString()

            } else {
                filmRating.text = ""
                filmRatingVector.visibility = android.view.View.GONE
            }
            Glide
                .with(filmCover.context)
                .load(film.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(filmCover)

            root.setOnClickListener {
                onClick(film)
            }
        }

    }
}