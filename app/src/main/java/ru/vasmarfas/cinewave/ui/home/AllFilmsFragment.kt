package ru.vasmarfas.cinewave.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.entity.ActorsBestFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentAllFilmsBinding
import ru.vasmarfas.cinewave.ui.adapters.FilmItemAdapter
import ru.vasmarfas.cinewave.ui.serviceItems.SpacesItemDecoration
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"
private const val ARG_DATATYPE = "dataType"
private const val ARG_TITLE = "title"

/**
 * A simple [Fragment] subclass.
 * Use the [AllFilmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AllFilmsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var data: List<Any>? = null
    private var dataType: String? = null
    private var pageTitle: String? = null
    private var _binding: FragmentAllFilmsBinding? = null
    private val binding get() = _binding!!
    private val allFilmsAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film
            )
        }
    }// Объект Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.getString(ARG_TITLE) != null) {
                pageTitle = it.getString(ARG_TITLE)
            }
            if (it.getString(ARG_DATATYPE) != null) {
                dataType = it.getString(ARG_DATATYPE)
                //Log.d("Allfilms Debug", dataType!!)

                when (dataType) {
                    "Film" -> {
                        if (it.getString(ARG_DATA) != null) {
                            data = Json.decodeFromString<List<Film>>(it.getString(ARG_DATA)!!)
                            //Log.d("Allfilms Debug", data.toString())
                        }
                    }

                    "Premiere" -> {
                        if (it.getString(ARG_DATA) != null) {
                            data = Json.decodeFromString<List<Premiere>>(it.getString(ARG_DATA)!!)
                            //Log.d("Allfilms Debug", data.toString())
                        }
                    }

                    "ActorsBestFilm" -> {
                        if (it.getString(ARG_DATA) != null) {
                            data =
                                Json.decodeFromString<List<ActorsBestFilm>>(it.getString(ARG_DATA)!!)
                            //Log.d("Allfilms Debug", data.toString())
                        }

                    }
                    "SimilarFilm" -> {
                        if (it.getString(ARG_DATA) != null) {
                            data =
                                Json.decodeFromString<List<SimilarFilm>>(it.getString(ARG_DATA)!!)
                            //Log.d("Allfilms Debug", data.toString())
                        }

                    }
                    "Interests" -> {
                    if (it.getString(ARG_DATA) != null) {
                        val deserializedList = Json.decodeFromString<List<String>>(it.getString(ARG_DATA)!!)
                        val deserializedSimilarFilms = Json.decodeFromString<List<SimilarFilm>>(deserializedList[0])
                        //Log.d("Allfilms Debug Similar", deserializedSimilarFilms.toString())

                        val deserializedStaff = Json.decodeFromString<List<StaffPerson>>(deserializedList[1])
                        //Log.d("Allfilms Debug Staff", deserializedStaff.toString())
                        val finalMutList = mutableListOf<Any>()
                        finalMutList.addAll(deserializedSimilarFilms)
                        finalMutList.addAll(deserializedStaff)
                        data = finalMutList
                        //Log.d("Allfilms Debug", data.toString())
                    }

                }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Log.d("AllFilmsFragment", data?.size.toString())
        _binding = FragmentAllFilmsBinding.inflate(inflater, container, false)
//        dao = App().getInstance(requireContext()).userDao()
        val filmManager = GridLayoutManager(context, 2)
        filmManager.orientation = RecyclerView.VERTICAL
        if (pageTitle != null) {
            binding.textPageTitle.text = pageTitle
        }
//        allFilmsAdapter = FilmItemAdapter { film -> onFilmItemClick(film) }
        if (dataType == "ActorsBestFilm") binding.horizontalScrollView.visibility = View.VISIBLE
        val newData = data?.toMutableList()
        newData?.add(0, "allFilmsFragment")

        if (newData != null) {
            allFilmsAdapter.data = newData.toList()
            //Log.d("ALLFILMS", newData.toString())
        } else {
            allFilmsAdapter.data = emptyList()
        }
        binding.allFilmsRecycler.addItemDecoration(SpacesItemDecoration(16))
        binding.allFilmsRecycler.layoutManager = filmManager
        binding.allFilmsRecycler.adapter = allFilmsAdapter
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.chipActor.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipActorSelf.isChecked  = false
                binding.chipProducer.isChecked  = false
                binding.chipActorVoice.isChecked  = false
                val filteredData = data?.filter { item ->
                    if (item is ActorsBestFilm) {
                        item.professionKey == "ACTOR"
                    } else {
                        false
                    }
                }
                if (filteredData != null) {
                    allFilmsAdapter.data = filteredData
                    binding.allFilmsRecycler.adapter = allFilmsAdapter
                }
            } else {
                if (newData != null) {
                    allFilmsAdapter.data = newData.toList()
                }
                binding.allFilmsRecycler.adapter = allFilmsAdapter

            }
        }

        binding.chipProducer.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipActor.isChecked  = false
                binding.chipActorSelf.isChecked  = false
                binding.chipActorVoice.isChecked  = false
                val filteredData = data?.filter { item ->
                    if (item is ActorsBestFilm) {
                        item.professionKey == "PRODUCER"
                    } else {
                        false
                    }
                }
                if (filteredData != null) {
                    allFilmsAdapter.data = filteredData
                    binding.allFilmsRecycler.adapter = allFilmsAdapter
                }
            } else {
                if (newData != null) {
                    allFilmsAdapter.data = newData.toList()
                }
                binding.allFilmsRecycler.adapter = allFilmsAdapter

            }
        }
        binding.chipActorSelf.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipActor.isChecked  = false
                binding.chipProducer.isChecked  = false
                binding.chipActorVoice.isChecked  = false
                val filteredData = data?.filter { item ->
                    if (item is ActorsBestFilm) {
                        if (item.description != null) {
                            item.description.toLowerCase(Locale.ROOT).contains("играет самого")
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
                if (filteredData != null) {
                    allFilmsAdapter.data = filteredData
                    binding.allFilmsRecycler.adapter = allFilmsAdapter
                }
            } else {
                if (newData != null) {
                    allFilmsAdapter.data = newData.toList()
                }
                binding.allFilmsRecycler.adapter = allFilmsAdapter

            }
        }
        binding.chipActorVoice.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipActor.isChecked  = false
                binding.chipProducer.isChecked  = false
                binding.chipActorSelf.isChecked  = false
                val filteredData = data?.filter { item ->
                    if (item is ActorsBestFilm) {
                        if (item.description != null) {
                            if (item.description.toLowerCase(Locale.ROOT).contains("озвучк")) {
                                true
                            } else if (item.professionKey != null) {
                                if (item.professionKey.toLowerCase(Locale.ROOT).contains("voice")) {
                                    true
                                } else false
                            } else{
                                false
                            }
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
                if (filteredData != null) {
                    allFilmsAdapter.data = filteredData
                    binding.allFilmsRecycler.adapter = allFilmsAdapter
                }
            } else {
                if (newData != null) {
                    allFilmsAdapter.data = newData.toList()
                }
                binding.allFilmsRecycler.adapter = allFilmsAdapter

            }
        }


        // Inflate the layout for this fragment
        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllFilmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllFilmsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATATYPE, param1)
                    putString(ARG_DATA, param2)
                }
            }
    }

    fun onFilmItemClick(item: Any) {
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
                        R.id.action_allFilmsFragment_to_filmPageFragment,
                        bundle
                    )
                }
            }

            is Film -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_allFilmsFragment_to_filmPageFragment,
                        bundle
                    )
                }
            }

            is ActorsBestFilm -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_allFilmsFragment_to_filmPageFragment,
                        bundle
                    )
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
                        R.id.action_allFilmsFragment_to_filmPageFragment,
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
                        R.id.action_allFilmsFragment_to_actorPageFragment,
                        bundle
                    )
                }
            }
        }
    }
}