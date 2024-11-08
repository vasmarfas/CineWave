package ru.vasmarfas.cinewave.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.databinding.FragmentItemProfileCollectionBinding

class ProfileCollectionsAdapter(
    val dao: UserDao,
    private val onClick: (CollectionsModel) -> Unit
) : RecyclerView.Adapter<ProfileCollectionsAdapter.ProfileCollectionsViewHolder>() {
    var constantData: List<CollectionsModel> = emptyList()
//    var data: List<CollectionsModel> = emptyList()
//        set(newValue) {
//            if (constantData.isEmpty()) {
//                constantData = newValue
//            }
//            field = newValue
//            notifyDataSetChanged()
//        }
    val data = mutableListOf<CollectionsModel>()

    fun submitList(newData: List<CollectionsModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    class ProfileCollectionsViewHolder(val binding: FragmentItemProfileCollectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCollectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemProfileCollectionBinding.inflate(inflater, parent, false)

        return ProfileCollectionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileCollectionsViewHolder, position: Int) {
        //Log.d("DEBUG StaffAdapter", data.size.toString())
        //Log.d("DEBUG StaffAdapter", data.toString())

        val collection =  data[position]


        with(holder.binding)  {
            collectionName.text = collection.collectionName
            collectionItemCounter.text = collection.filmIds.size.toString()
            if (collection.collectionName == "Любимые") {
                collectionCover.setImageResource(R.drawable.icon_like_button)
                collectionRemove.visibility = View.GONE
            } else if (collection.collectionName == "Хочу посмотреть") {
                collectionCover.setImageResource(R.drawable.icon_bookmark_button)
                collectionRemove.visibility = View.GONE
            }
            collectionRemove.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext((Dispatchers.IO)) { dao.deleteCollection(collectionName.text as String) }
                    val newData = data.toMutableList()
                    newData.remove(collection)
                    submitList(newData)
                }
            }
        }

        holder.binding.root.setOnClickListener {
            onClick(collection)
        }


    }
}