package ru.vasmarfas.cinewave.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.vasmarfas.cinewave.App
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.databinding.FragmentItemColletionToAddBinding
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.FavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewFavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewViewedListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.ViewedListModel
import ru.vasmarfas.cinewave.ui.serviceItems.CreateNewCollectionDialogFragment
import javax.inject.Inject


private const val FAVOURITE_ITEM = 0
private const val VIEWED_ITEM = 1
private const val TO_WATCH_ITEM = 2
private const val COLLECTION_ITEM = 3
private const val CREATE_NEW_COLLECTION_ITEM = 4
private const val EMPTY_LIST = 10

class AddToCollectionItemAdapter(
    context: Context,
    filmId: Int,
    favouriteButton: ImageButton?,
    bookMarkButton: ImageButton?,
    hideButton: ImageButton?,
    parentFragmentManager: FragmentManager,
    usdao: UserDao
) :
    RecyclerView.Adapter<AddToCollectionItemAdapter.AddToCollectionViewHolder>() {
    //    var data: List<Any> = emptyList()
//        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }
//    @Inject
//    lateinit var dao: UserDao
//    val dao = App().getInstance(context).userDao()
//    var data: List<List<Any>> = emptyList()
//        set(newValue) {
//            field = newValue
//            notifyDataSetChanged()
//        }
    val data = mutableListOf<Any>()

    fun submitList(newData: List<Any>) {
        data.clear()
        data.addAll(newData)
        data.add(CreateNewCollectionDialogFragment())
        notifyDataSetChanged()
    }
//    var data: List<Any> = emptyList()
//        set(newValue) {
//            val myData: MutableList<Any> = newValue.toMutableList()
//            myData.add(CreateNewCollectionDialogFragment())
//            field = myData
//            notifyDataSetChanged()
//        }


//    var data: List<List<Any>> = runBlocking {
//        val myData: MutableList<List<Any>> = mutableListOf()
//        launch(Dispatchers.IO) {
//            val favList = dao.getAllFavouriteFilmModels()
//            val toWatchList = dao.getAllToWatchFilmModels()
//            val viewedList = dao.getAllViewedFilmModels()
//            val colList = dao.getCollections()
//            myData.add(favList)
//            myData.add(toWatchList)
//            myData.add(viewedList)
//            for (item in colList) {
//                myData.add(listOf(item))
//            }
//            val singleList: List<Fragment> = listOf(CreateNewCollectionDialogFragment(), CreateNewCollectionDialogFragment())
//            myData.add(singleList)
////            myData.add(colList)
//
////            myData.add(dao.getAllFavouriteFilmModels())
////            myData.add(dao.getAllToWatchFilmModels())
////            myData.add(dao.getAllViewedFilmModels())
////            myData.add(dao.getCollections())
//        }
//        return@runBlocking myData
//    }

    val filmMyId = filmId
    val bookButton = bookMarkButton
    val favButton = favouriteButton
    val hidButton = hideButton
    val dao = usdao

    val fragManager = parentFragmentManager
    class AddToCollectionViewHolder(val binding: FragmentItemColletionToAddBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size
    override fun getItemViewType(position: Int): Int {
        //Log.d("DEBUG ADDADAPTER", data.toString())
        return if (data[position] == null) {
            10
        } else {
            when (data[position]) {
                is FavouriteListModel -> FAVOURITE_ITEM
                is ViewedListModel -> VIEWED_ITEM
                is ToWatchListModel -> TO_WATCH_ITEM
                is CollectionsModel -> COLLECTION_ITEM
                is CreateNewCollectionDialogFragment -> CREATE_NEW_COLLECTION_ITEM
                else -> throw IllegalArgumentException("Invalid item type. Type is ${data[position]::class.simpleName}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToCollectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentItemColletionToAddBinding.inflate(inflater, parent, false)
        //Log.d("DEBUG ADDCOLADAPTER", filmMyId.toString())

        return AddToCollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddToCollectionViewHolder, position: Int) {

        val item = data[position]

        when (holder.itemViewType) {
            VIEWED_ITEM -> processViewedItem(holder, position)
            FAVOURITE_ITEM -> processFavouriteItem(holder, position)
            TO_WATCH_ITEM -> processToWatchItem(holder, position)
            COLLECTION_ITEM -> processCollectionItem(holder, position)
            CREATE_NEW_COLLECTION_ITEM -> processCreateNewCollectionItem(holder, position)
            EMPTY_LIST -> when (position) {
                VIEWED_ITEM -> processViewedItem(holder, position)
                FAVOURITE_ITEM -> processFavouriteItem(holder, position)
                TO_WATCH_ITEM -> processToWatchItem(holder, position)
                COLLECTION_ITEM -> processCollectionItem(holder, position)
                CREATE_NEW_COLLECTION_ITEM -> processCreateNewCollectionItem(holder, position)
                else -> Log.e("DEBUG ADDADAPTER", "List is empty")
            }
        }
    }

    fun processFavouriteItem(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = data[position] as List<FavouriteListModel>
        val itemHolder = holder as AddToCollectionViewHolder
        itemHolder.binding.collectionName.text = "Любимое"


        CoroutineScope(Dispatchers.IO).launch {

            val itemCount = dao.getAllFavouriteFilmIds().size
            launch(Dispatchers.Main) {
                itemHolder.binding.itemsCount.text = itemCount.toString()
            }
            if (filmMyId in dao.getAllFavouriteFilmIds()) {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = true
                }
            } else {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = false
                }
            }
        }
        with(itemHolder.binding) {
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.addToFavouriteFilmIds(NewFavouriteListModel(filmMyId))
                    }
                    favButton?.setImageResource(R.drawable.icon_like_dislike_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() + 1).toString()

                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteFromFavouriteFilmIds(filmMyId)
                    }
                    favButton?.setImageResource(R.drawable.icon_like_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() - 1).toString()
                }
            }

        }
    }

    fun processToWatchItem(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as AddToCollectionViewHolder
        itemHolder.binding.collectionName.text = "К просмотру"


        CoroutineScope(Dispatchers.IO).launch {
            val itemCount = dao.getAllToWatchFilmIds().size
            launch(Dispatchers.Main) {
                itemHolder.binding.itemsCount.text = itemCount.toString()
            }
            if (filmMyId in dao.getAllToWatchFilmIds()) {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = true
                }
            } else {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = false
                }
            }
        }
        with(itemHolder.binding) {
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.addToToWatchFilmIds(NewToWatchListModel(filmMyId))
                    }
                    bookButton?.setImageResource(R.drawable.icon_bookmark_filled_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() + 1).toString()

                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteFromToWatchFilmIds(filmMyId)
                    }
                    bookButton?.setImageResource(R.drawable.icon_bookmark_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() - 1).toString()

                }
            }

        }
    }

    fun processViewedItem(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as AddToCollectionViewHolder
        itemHolder.binding.collectionName.text = "Просмотренное"

        CoroutineScope(Dispatchers.IO).launch {
            val itemCount = dao.getAllViewedFilmIds().size
            launch(Dispatchers.Main) {
                itemHolder.binding.itemsCount.text = itemCount.toString()
            }
            if (filmMyId in dao.getAllViewedFilmIds()) {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = true
                }
            } else {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = false
                }
            }
        }
        with(itemHolder.binding) {
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.addToViewedFilmIds(NewViewedListModel(filmMyId))
                    }
                    hidButton?.setImageResource(R.drawable.icon_hide_show_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() + 1).toString()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteFromViewedFilmIds(filmMyId)
                    }
                    hidButton?.setImageResource(R.drawable.icon_hide_button)
                    itemHolder.binding.itemsCount.text = (itemHolder.binding.itemsCount.text.toString().toInt() - 1).toString()

                }
            }

        }
    }

    fun processCollectionItem(holder: RecyclerView.ViewHolder, position: Int) {
        val dataList = data[position]  as CollectionsModel
        val item = listOf<CollectionsModel>(dataList)
        val itemHolder = holder as AddToCollectionViewHolder
        itemHolder.binding.collectionName.text = item.first().collectionName
        itemHolder.binding.itemsCount.text = item.first().filmIds.size.toString()


        CoroutineScope(Dispatchers.IO).launch {
            if (filmMyId in item.first().filmIds) {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = true
                }
            } else {
                launch(Dispatchers.Main) {
                    itemHolder.binding.checkbox.isChecked = false
                }
            }
        }
        with(itemHolder.binding) {
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val newCollection = item.first()
                        val newList = newCollection.filmIds.toMutableList()
                        newList.add(filmMyId)
                        newCollection.filmIds = newList.toList()
                        dao.updateCollection((newCollection))
                    }
                    itemsCount.text = (itemsCount.text.toString().toInt() + 1 ).toString()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val newCollection = item.first()
                        val newList = newCollection.filmIds.toMutableList()
                        newList.remove(filmMyId)
                        newCollection.filmIds = newList.toList()
                        dao.updateCollection((newCollection))
                    }
                    itemsCount.text = (itemsCount.text.toString().toInt() - 1 ).toString()

                }
            }

        }
    }

    fun processCreateNewCollectionItem(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as AddToCollectionViewHolder

        with(itemHolder.binding) {
            collectionName.text = "Создать свою коллекцию"
            itemsCount.visibility = View.INVISIBLE
            checkbox.visibility = View.INVISIBLE
            plusIcon.visibility = View.VISIBLE
            root.setOnClickListener {
                CreateNewCollectionDialogFragment().show(fragManager, "TEST")
            }
        }
    }
}