package ru.vasmarfas.cinewave.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.vasmarfas.cinewave.data.retrofit.entity.ImageItem
import ru.vasmarfas.cinewave.databinding.FragmentItemFilmGalleryBinding

class FilmGalleryAdapter(
    private val onClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<FilmGalleryAdapter.FilmGalleryViewHolder>() {
    var constantData: List<Any> = emptyList()
    var data: List<ImageItem> = emptyList()
        set(newValue) {
            if (constantData.isEmpty()) {
                constantData = newValue
            }
            field = newValue
            notifyDataSetChanged()
        }

    class FilmGalleryViewHolder(val binding: FragmentItemFilmGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemFilmGalleryBinding.inflate(inflater, parent, false)

        return FilmGalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmGalleryViewHolder, position: Int) {
        //Log.d("DEBUG StaffAdapter", data.size.toString())
        //Log.d("DEBUG StaffAdapter", data.toString())

        val picture =  data[position]


        with(holder.binding)  {

            Glide
                .with(galleryPicture.context)
                .load(picture.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(galleryPicture)
        }

        holder.binding.root.setOnClickListener {
            onClick(picture)
        }


    }
}