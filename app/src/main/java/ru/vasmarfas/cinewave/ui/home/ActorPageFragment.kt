package ru.vasmarfas.cinewave.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.NewFilmPersonInfoModel
import ru.vasmarfas.cinewave.data.db.entity.NewInterestModel
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.ActorsBestFilm
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.databinding.FragmentActorPageBinding
import ru.vasmarfas.cinewave.databinding.FragmentItemShowAllBinding
import ru.vasmarfas.cinewave.ui.adapters.FilmItemAdapter
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_STAFF_ID = "staffId"

/**
 * A simple [Fragment] subclass.
 * Use the [ActorPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ActorPageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var staffId: Int? = null
    private var _binding: FragmentActorPageBinding? = null
    private val binding get() = _binding!!
    private val bestFilmsAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                bestFilmsAdapter.data,
                binding.bestFilms.text.toString()
            )
        }
    } // Объект Adapter

    @Inject
    lateinit var dao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            staffId = it.getInt(ARG_STAFF_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentActorPageBinding.inflate(inflater, container, false)
        val actorsBestFilmsManager = LinearLayoutManager(context) // LayoutManager
        actorsBestFilmsManager.orientation = RecyclerView.HORIZONTAL
        binding.bestFilmsRecycler.layoutManager = actorsBestFilmsManager
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.bestFilmsAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), bestFilmsAdapter.data, binding.bestFilms.text.toString()
            )
        }

        binding.filmographyAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), bestFilmsAdapter.constantData, binding.filmography.text.toString()
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.staffByPersonId(
                    staffId!!
                )
            }.fold(
                onSuccess = {
                    val filmStaff = it.body()
                    if (filmStaff != null) {
                        val staffName: String = filmStaff.nameRu
                            ?: filmStaff.nameEn ?: getString(R.string.no_name)
                        if (dao.getInterestById(filmStaff.personId) == null) {
                            dao.saveInterest(
                                NewInterestModel(
                                    kinopoiskId = filmStaff.personId,
                                    isFilm = false,
                                    isPerson = true,
                                    posterUrl = filmStaff.posterUrl,
                                    title = staffName
                                )
                            )
                        }
                        if (dao.getFilmPersonInfoById(filmStaff.personId) == null) {

                            dao.saveFilmPersonInfo(
                                NewFilmPersonInfoModel(
                                    id = filmStaff.personId,
                                    isFilm = false,
                                    isPerson = true,
                                    posterUrl = filmStaff.posterUrl,
                                    title = staffName
                                )
                            )
                        }
                        requireActivity().runOnUiThread {
                            Glide
                                .with(binding.actorPhoto.context)
                                .load(filmStaff.posterUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.actorPhoto)

                            binding.actorName.text = filmStaff.nameRu ?: filmStaff.nameEn ?: ""
                            binding.actorRole.text = filmStaff.profession

                            if (filmStaff.films != null) {
                                bestFilmsAdapter.data = filmStaff.films
                                binding.bestFilmsRecycler.adapter = bestFilmsAdapter
                                var filmCounter = 0
                                for (film in filmStaff.films) {
                                    filmCounter++
                                }
                                binding.filmographyText.text =
                                    getString(R.string.fulm_count_with_var, filmCounter.toString())
                            } else {
                                Log.e("DEBUG ACTOR", it.message())
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
        return binding.root
    }

    fun onFilmItemClick(item: Any, data: List<Any>, title: String) {
        //Log.d("DEBUG", "$item --- $data")
        when (item) {
            is Premiere -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid), item.kinopoiskId)
                //Log.d("DEBUG", "Premiere")

                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_actorPageFragment_to_allFilmsFragment,
                        bundle
                    )
                }
            }

            is Film -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid), item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                if (item.type == "TV_SERIES" || item.type == "MINI_SERIES" || item.type == "TV_SHOW") {
                    val navController = view?.findNavController()
                    if (navController != null) {
                        //Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }

                        navController.navigate(
                            R.id.action_actorPageFragment_to_allFilmsFragment,
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
                            R.id.action_actorPageFragment_to_allFilmsFragment,
                            bundle
                        )
                    }
                }
            }

            is ActorsBestFilm -> {
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid), item.kinopoiskId)
                //Log.d("DEBUG", "Film")
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_actorPageFragment_to_filmPageFragment,
                        bundle
                    )
                }
            }

            is FilmItemAdapter.ShowAllItemViewHolder -> {
                //Log.d("DEBUG", "ShowAll")
                val bundle = Bundle()
                val dataType = data[0]::class.java.simpleName

                val serializedData = when (dataType) {
                    "Film" -> Json.encodeToString(data.filterIsInstance<Film>())
                    "Premiere" -> Json.encodeToString(data.filterIsInstance<Premiere>())
                    "ActorsBestFilm" -> Json.encodeToString(data.filterIsInstance<ActorsBestFilm>())
                    else -> ""
                }
                bundle.apply {
                    putString(getString(R.string.bundle_var_data), serializedData)
                    putString(getString(R.string.bundle_var_datatype), dataType)
                    putString(getString(R.string.bundle_var_title), title)
                    val navController = view?.findNavController()
                    if (navController != null) {
                        //Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }

                        navController.navigate(
                            R.id.action_actorPageFragment_to_allFilmsFragment,
                            bundle
                        )
//                                navBarHideListener?.hideNavBar()

                    }


                }

            }

            else -> {

                Log.e("DEBUG", "Not type. Type is ${item::class.simpleName}")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActorPageFragment.
         */
        // TODO: Rename and change types and number of parameters


        @JvmStatic
        fun newInstance(staffId: Int) =
            ActorPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_STAFF_ID, staffId)
                }
            }
    }
}