package ru.vasmarfas.cinewave.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.CollectionsModel
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentItemShowAllBinding
import ru.vasmarfas.cinewave.databinding.FragmentProfileBinding
import ru.vasmarfas.cinewave.ui.adapters.FilmItemAdapter
import ru.vasmarfas.cinewave.ui.adapters.ProfileCollectionsAdapter
import ru.vasmarfas.cinewave.ui.serviceItems.CreateNewCollectionDialogFragment
import ru.vasmarfas.cinewave.ui.serviceItems.MainViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()

    // TODO: Rename and change types of parameters
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var dao: UserDao

    private val viewedFilmsAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                viewedFilmsAdapter.data,
                binding.viewed.text.toString()
            )
        }
    } // Объект Adapter
    private val collectionsAdapter: ProfileCollectionsAdapter by lazy {
        ProfileCollectionsAdapter(dao) { collection ->
            onCollectionClick(
                collection,
                viewedFilmsAdapter.data,
                binding.viewed.text.toString()
            )
        }
    } // Объект Adapter

    private val interestAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                interestAdapter.data,
                binding.interest.text.toString()
            )
        }
    } // Объект Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val viewedFilmsManager = LinearLayoutManager(context) // LayoutManager
        viewedFilmsManager.orientation = RecyclerView.HORIZONTAL
        binding.viewedRecycler.layoutManager = viewedFilmsManager
        val collectionsManager = GridLayoutManager(context, 2) // LayoutManager
        collectionsManager.orientation = RecyclerView.VERTICAL
        binding.collectionsRecycler.layoutManager = collectionsManager
        val interestManager = LinearLayoutManager(context) // LayoutManager
        interestManager.orientation = RecyclerView.HORIZONTAL
        binding.interestRecycler.layoutManager = interestManager

        binding.interestAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), interestAdapter.constantData, binding.interest.text.toString()
            )
        }

        binding.viewedAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), viewedFilmsAdapter.constantData, binding.viewed.text.toString()
            )
        }

        binding.collectionsTextCreate.setOnClickListener {
            CreateNewCollectionDialogFragment().show(parentFragmentManager, "TEST")
            collectionsAdapter.notifyDataSetChanged()
        }
        binding.collectionsImagePlus.setOnClickListener {
            CreateNewCollectionDialogFragment().show(parentFragmentManager, "TEST")
            collectionsAdapter.notifyDataSetChanged()
        }
        val viewedFilmsData: MutableList<SimilarFilm> = mutableListOf()
        val interestData: MutableList<Any> = mutableListOf()

        lifecycleScope.launch(Dispatchers.IO) {
            val viewedFilms = dao.getAllViewedFilmIds()
            for (film in viewedFilms) {
                val curFilm = dao.getFilmPersonInfoById(film)
                if (curFilm != null) {
                    viewedFilmsData.add(
                        SimilarFilm(
                            kinopoiskId = curFilm.id,
                            posterUrl = curFilm.posterUrl,
                            posterUrlPreview = curFilm.posterUrl,
                            relationType = "SIMILAR",
                            nameRu = curFilm.title
                        )
                    )
                }

            }
            viewedFilmsAdapter.data = viewedFilmsData
            //Log.d("PROFILE", viewedFilms.toString())
            //Log.d("PROFILE", viewedFilmsData.toString())
            requireActivity().runOnUiThread {
                binding.viewedRecycler.adapter = viewedFilmsAdapter
            }

            val interests = dao.getInterestDesc()
            for (interest in interests) {
                if (interest.isFilm) {
                    interestData.add(
                        SimilarFilm(
                            kinopoiskId = interest.kinopoiskId,
                            posterUrl = interest.posterUrl,
                            posterUrlPreview = interest.posterUrl,
                            relationType = "SIMILAR",
                            nameRu = interest.title
                        )
                    )
                } else {
                    interestData.add(
                        StaffPerson(
                            staffId = interest.kinopoiskId,
                            posterUrl = interest.posterUrl,
                            nameRu = interest.title,
                            professionKey = "",
                            description = "",
                            professionText = ""
                        )
                    )
                }
            }
            interestAdapter.data = interestData
            //Log.d("PROFILE", interests.toString())
            //Log.d("PROFILE", interestData.toString())
            requireActivity().runOnUiThread {
                binding.interestRecycler.adapter = interestAdapter
            }
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.getCollectionsLiveData.observe(viewLifecycleOwner) { collections ->
                Log.e("DEBUG BOTTOM - adapter", collectionsAdapter.data.toString())
                lifecycleScope.launch(Dispatchers.IO) {

                    val favCollection = dao.getAllFavouriteFilmIds()
                    val toWatchCollection = dao.getAllToWatchFilmIds()
                    val outCollections = mutableListOf<CollectionsModel>()

                    outCollections.add(CollectionsModel(getString(R.string.favourite)
                        , favCollection))
                    outCollections.add(CollectionsModel(getString(R.string.towatch)
                        , toWatchCollection))
                    outCollections.addAll(collections)
                    requireActivity().runOnUiThread {
                        collectionsAdapter.submitList(outCollections)
                    }
                    //Log.d("PROFILE",  outCollections.toString())
                    requireActivity().runOnUiThread {
                        binding.collectionsRecycler.adapter = collectionsAdapter
                    }

                }
//                collectionsAdapter.submitList(collections)
                Log.e("DEBUG BOTTOM - adapter", collectionsAdapter.data.toString())
            }
        }
    }
    fun onFilmItemClick(item: Any, data: List<Any>, title: String) {
        //Log.d("DEBUG", "$item --- $data")
        when (item) {
            is Premiere -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
                //Log.d("DEBUG", "Premiere")

                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_navigation_profile_to_filmPageFragment,
                        bundle
                    )
                }
            }

            is Film -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                if (item.type == "TV_SERIES" || item.type == "MINI_SERIES" || item.type == "TV_SHOW") {
                    val navController = view?.findNavController()
                    if (navController != null) {
                        //Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }

                        navController.navigate(
                            R.id.action_navigation_profile_to_filmPageFragment,
                            bundle
                        )
                    }
                } else {
                    val navController = view?.findNavController()
                    if (navController != null) {
                        //Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }

                        navController.navigate(
                            R.id.action_navigation_profile_to_filmPageFragment,
                            bundle
                        )
                    }
                }
            }

            is SimilarFilm -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_navigation_profile_to_filmPageFragment,
                        bundle
                    )
                }

            }
            is StaffPerson -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_staffId), item.staffId)
                //Log.d("DEBUG", "Premiere")

                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_navigation_profile_to_actorPageFragment,
                        bundle
                    )
                }
            }
            is FilmItemAdapter.ShowAllItemViewHolder -> {
                //Log.d("DEBUG", "ShowAll")
                val bundle = Bundle()
                val dataTypes = mutableListOf<String>()
                for (curItem in  data){
                    dataTypes.add(curItem::class.java.simpleName)
                }
                //Log.d("Profile DEBUG", dataTypes.toString())

                if (dataTypes.contains("StaffPerson")) {
                    val dataType = "Interests"
                    //Log.d("Profile DEBUG", "True $dataType")
                    val serializableFilms  = Json.encodeToString(data.filterIsInstance<SimilarFilm>())
                    val serializablePerson  = Json.encodeToString(data.filterIsInstance<StaffPerson>())

                    val serializedData = Json.encodeToString(listOf(serializableFilms, serializablePerson ))
                    bundle.apply {
                        putString(getString(R.string.bundle_var_data),  serializedData)
                        putString(getString(R.string.bundle_var_datatype),  dataType)
                        putString(getString(R.string.bundle_var_title),  title)
                        val navController = view?.findNavController()
                        if (navController != null) {
                            //Log.d("BUNDLE2", bundle.toString())
//                            bundle.getString(getString(R.string.bundle_var_data))
//                                ?.let { it1 -> Log.d("BUNDLE2", it1) }

                            navController.navigate(
                                R.id.action_navigation_profile_to_allFilmsFragment,
                                bundle
                            )
//                                navBarHideListener?.hideNavBar()

                        }


                    }
                } else {
                    val dataType = data[0]::class.java.simpleName
                    //Log.d("Profile DEBUG", "False $dataType")

                    val serializedData = when (dataType) {
                        "Film" -> Json.encodeToString(data.filterIsInstance<Film>())
                        "Premiere" -> Json.encodeToString(data.filterIsInstance<Premiere>())
                        "SimilarFilm" -> Json.encodeToString(data.filterIsInstance<SimilarFilm>())
                        else -> ""
                    }
                    bundle.apply {
                        putString(getString(R.string.bundle_var_data),  serializedData)
                        putString(getString(R.string.bundle_var_datatype),  dataType)
                        putString(getString(R.string.bundle_var_title),  title)
                        val navController = view?.findNavController()
                        if (navController != null) {
                            //Log.d("BUNDLE2", bundle.toString())
//                            bundle.getString(getString(R.string.bundle_var_data))
//                                ?.let { it1 -> Log.d("BUNDLE2", it1) }

                            navController.navigate(
                                R.id.action_navigation_profile_to_allFilmsFragment,
                                bundle
                            )
//                                navBarHideListener?.hideNavBar()

                        }


                    }
                }

            }

            else -> {

                //Log.d("DEBUG", "Not type. Type is ${item::class.simpleName}")
            }
        }
    }

    fun onCollectionClick(item: CollectionsModel, data2: List<Any>, title: String) {

        //Log.d("DEBUG", "$item --- $data2")
        val filmCollectionsData = mutableListOf<SimilarFilm>()
        lifecycleScope.launch {

            for (film in item.filmIds) {
                val curFilm = withContext(Dispatchers.IO) { dao.getFilmPersonInfoById(film) }
                if (curFilm != null) {
                    filmCollectionsData.add(
                        SimilarFilm(
                            kinopoiskId = curFilm.id,
                            posterUrl = curFilm.posterUrl,
                            posterUrlPreview = curFilm.posterUrl,
                            relationType = "SIMILAR",
                            nameRu = curFilm.title
                        )
                    )
                }
            }



            if (filmCollectionsData.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.collection_empty_warning), Toast.LENGTH_LONG).show()
                return@launch
            }
            val dataType = filmCollectionsData[0]::class.java.simpleName
            //Log.d("Profile DEBUG", "False $dataType")

            val serializedData = when (dataType) {
                "Film" -> Json.encodeToString(filmCollectionsData.filterIsInstance<Film>())
                "Premiere" -> Json.encodeToString(filmCollectionsData.filterIsInstance<Premiere>())
                "SimilarFilm" -> Json.encodeToString(filmCollectionsData.filterIsInstance<SimilarFilm>())
                else -> ""
            }
            val bundle = Bundle()
            bundle.apply {
                putString(getString(R.string.bundle_var_data),  serializedData)
                putString(getString(R.string.bundle_var_datatype),  dataType)
                putString(getString(R.string.bundle_var_title),  title)
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_navigation_profile_to_allFilmsFragment,
                        bundle
                    )
//                                navBarHideListener?.hideNavBar()

                }


            }

        }
    }
}