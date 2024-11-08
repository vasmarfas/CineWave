package ru.vasmarfas.cinewave.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vasmarfas.cinewave.App
import ru.vasmarfas.cinewave.data.retrofit.entity.SeriesEpisode
import ru.vasmarfas.cinewave.databinding.FragmentItemSerieSeasonBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SerieSeasonItemAdapter(context: Context) : RecyclerView.Adapter<SerieSeasonItemAdapter.SerieSeasonViewHolder>() {
    val dao = App().getInstance(context).userDao()

//    @Inject
//    lateinit var dao: UserDao
    var constantData: List<Any> = emptyList()
    var data: List<SeriesEpisode> = emptyList()
        set(newValue) {
            if (constantData.isEmpty()) {
                constantData = newValue
            }
            field = if (newValue.isEmpty()) {
                emptyList()
            } else {
                newValue
            }
            notifyDataSetChanged()
        }
//        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }



    class SerieSeasonViewHolder(val binding: FragmentItemSerieSeasonBinding) :
        RecyclerView.ViewHolder(binding.root)
//        val buttonShowAll: ImageButton = itemView.findViewById(R.id.buttonShowAll)
//        val textShowAll: TextView = itemView.findViewById(R.id.textShowAll)
//
//    }



    override fun getItemCount(): Int = data.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieSeasonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemSerieSeasonBinding.inflate(inflater, parent, false)

        return SerieSeasonViewHolder(binding)
    }



     override fun onBindViewHolder(holder: SerieSeasonViewHolder, position: Int) {
         //Log.d("Debug Adapter", data.last().toString())
         val serie = data[position]
         val premiereHolder = holder
         val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
         val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
         val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(serie.releaseDate)
             ?.let { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it) }
         val name = serie.nameRu ?: serie.nameEn ?:  ""
         with(premiereHolder.binding) {
             serieName.text = "${serie.episodeNumber}. ${name}"
             serieDate.text = date
         }
    }

}