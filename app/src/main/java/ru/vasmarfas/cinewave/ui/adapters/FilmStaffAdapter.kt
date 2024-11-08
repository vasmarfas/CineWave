package ru.vasmarfas.cinewave.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentItemFilmStaffBinding

class FilmStaffAdapter(
    private val onClick: (StaffPerson) -> Unit
) : RecyclerView.Adapter<FilmStaffAdapter.FilmStaffViewHolder>() {
    var data: List<StaffPerson> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    class FilmStaffViewHolder(val binding: FragmentItemFilmStaffBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmStaffViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemFilmStaffBinding.inflate(inflater, parent, false)

        return FilmStaffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmStaffViewHolder, position: Int) {
        //Log.d("DEBUG StaffAdapter", data.size.toString())
        //Log.d("DEBUG StaffAdapter", data.toString())

        val actor =  data[position]


        with(holder.binding)  {
            staffName.text =
                if (actor.nameRu!=null) {actor.nameRu} else {actor.nameEn}
            staffRole.text =
                if (actor.description!=null) {actor.description} else {actor.professionText}

            Glide
                .with(staffPhoto.context)
                .load(actor.posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(staffPhoto)
        }

        holder.binding.root.setOnClickListener {
            onClick(actor)
        }


    }
}