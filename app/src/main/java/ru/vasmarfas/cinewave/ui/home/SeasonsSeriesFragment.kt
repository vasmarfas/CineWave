package ru.vasmarfas.cinewave.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.ActorsBestFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.CollectionSeriesSeasons
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentSeasonsSeriesBinding
import ru.vasmarfas.cinewave.ui.adapters.SerieSeasonItemAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [SeasonsSeriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_KINOPOSK_ID = "kinopoiskId"
private const val ARG_TITLE = "title"

@AndroidEntryPoint
class SeasonsSeriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var data: List<Any>? = null
    private var dataType: String? = null
    private var pageTitle: String? = null
    private var _binding: FragmentSeasonsSeriesBinding? = null
    private var kinopoiskId: Int? = null
    private var seasonSeriesData: CollectionSeriesSeasons? = null
    private val binding get() = _binding!!
    private val seriesAdapter: SerieSeasonItemAdapter by lazy {
        SerieSeasonItemAdapter(requireContext())
    }// Объект Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.getString(ARG_TITLE) != null) {
                pageTitle = it.getString(ARG_TITLE)
            }
            kinopoiskId = it.getInt(ARG_KINOPOSK_ID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Log.d("AllFilmsFragment", data?.size.toString())
        _binding = FragmentSeasonsSeriesBinding.inflate(inflater, container, false)
//        dao = App().getInstance(requireContext()).userDao()
        val seriesManager = LinearLayoutManager(context)
        seriesManager.orientation = RecyclerView.VERTICAL
        if (pageTitle != null) {
            binding.textPageTitle.text = pageTitle
        }
//        allFilmsAdapter = FilmItemAdapter { film -> onFilmItemClick(film) }
        val newData = data?.toMutableList()
        newData?.add(0, "allFilmsFragment")

//        binding.allFilmsRecycler.addItemDecoration(SpacesItemDecoration(16))
        binding.seriesRecycler.layoutManager = seriesManager
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        /*binding.chipActor.setOnCheckedChangeListener { chip, isChecked ->
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
        }*/
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.seasons(
                    kinopoiskId!!
                )
            }.fold(
                onSuccess = {
                    val collectionSeriesSeasons = it.body()
                    seasonSeriesData = collectionSeriesSeasons
                    val seriesSeasons = collectionSeriesSeasons?.items  ?: emptyList()
                    val seasonsCount = collectionSeriesSeasons?.total  ?: 0
                    //Log.d("SeasonsSeries DEBUG", seasonsCount.toString())
                    //Log.d("SeasonsSeries DEBUG", seriesSeasons.toString())
                    //Log.d("SeasonsSeries DEBUG", seriesSeasons[0].toString())
                    //Log.d("SeasonsSeries DEBUG", seriesSeasons[0].episodes.toString())
                    if (seriesSeasons.isNotEmpty()) {
                        seriesAdapter.data = seriesSeasons[0].episodes
                    }

                    requireActivity().runOnUiThread {
                        binding.seriesRecycler.adapter = seriesAdapter
                        binding.textSeasonSeries.text = getString(
                            R.string.season_number_and_series_count_with_var,
                            1.toString(),
                            seriesSeasons[0].episodes.size.toString()
                        )

                        val chipGroup = binding.chipGroup
                        var firstChipSelected = false
                        for (item in 1..seasonsCount) {
                            val chip = Chip(requireContext())
                            chip.text = item.toString()
                            chip.isCheckable = true
                            if (!firstChipSelected) {
                                chip.isChecked = true
                                firstChipSelected = true
                                // Здесь также установите seriesAdapter.data и обновите RecyclerView
                            }
                            chipGroup.addView(chip)
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked) {
                                    chipGroup.children.forEach { siblingView ->
                                        if (siblingView is Chip && siblingView != buttonView) {
                                            siblingView.isChecked = false
                                        }
                                    }
                                    seriesAdapter.data = seriesSeasons[chip.text.toString().toInt() - 1].episodes
                                    binding.seriesRecycler.adapter = seriesAdapter
                                    binding.textSeasonSeries.text = getString(
                                        R.string.season_number_and_series_count_with_var,
                                        chip.text.toString(),
                                        seriesSeasons[chip.text.toString().toInt() - 1].episodes.size.toString()
                                    )
                                }
                                else {
                                    seriesAdapter.data = emptyList()
                                    binding.seriesRecycler.adapter = seriesAdapter
                                    binding.textSeasonSeries.text = ""

                                }
                            }
                        }

                    }
                },
                onFailure = { Log.e("DEBUG", it.message ?: "")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                }
            )

        }


        // Inflate the layout for this fragment
        return binding.root
    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment AllFilmsFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SeasonsSeriesFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_DATATYPE, param1)
//                    putString(ARG_DATA, param2)
//                }
//            }
//    }

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