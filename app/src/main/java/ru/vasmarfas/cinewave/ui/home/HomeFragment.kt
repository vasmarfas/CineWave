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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Response
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.databinding.FragmentHomeBinding
import ru.vasmarfas.cinewave.databinding.FragmentItemShowAllBinding
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.FilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.NewFilmFiltersModel
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters
import ru.vasmarfas.cinewave.ui.adapters.FilmItemAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var dao: UserDao
    private val filmsAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                filmsAdapter.constantData,
                binding.premieres.text.toString()
            )
        }
    }// Объект Adapter
    private val popularAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                popularAdapter.constantData,
                binding.popular.text.toString()
            )
        }
    } // Объект Adapter
    private val dynamic1Adapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                dynamic1Adapter.constantData,
                binding.dynamic1.text.toString()
            )
        }
    } // Объект Adapter
    private val top250Adapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                top250Adapter.constantData,
                binding.top250.text.toString()
            )
        }
    } // Объект Adapter
    private val dynamic2Adapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                dynamic2Adapter.constantData,
                binding.dynamic2.text.toString()
            )
        }
    } // Объект Adapter
    private val seriesAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                seriesAdapter.constantData,
                binding.series.text.toString()
            )
        }
    } // Объект Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.dynamic1.visibility = View.GONE
        binding.dynamic1All.visibility = View.GONE
        binding.dynamic2.visibility = View.GONE
        binding.dynamic2All.visibility = View.GONE
        binding.swiperefresh.setOnRefreshListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, HomeFragment.newInstance())
                .commit()
        }

//        dao = App().getInstance(requireContext()).userDao()
        val filmManager = LinearLayoutManager(context) // LayoutManager
        val popularManager = LinearLayoutManager(context) // LayoutManager
        val dynamic1Manager = LinearLayoutManager(context) // LayoutManager
        val top250Manager = LinearLayoutManager(context) // LayoutManager
        val dynamic2Manager = LinearLayoutManager(context) // LayoutManager
        val seriesManager = LinearLayoutManager(context) // LayoutManager
        filmManager.orientation = RecyclerView.HORIZONTAL
        popularManager.orientation = RecyclerView.HORIZONTAL
        dynamic1Manager.orientation = RecyclerView.HORIZONTAL
        top250Manager.orientation = RecyclerView.HORIZONTAL
        dynamic2Manager.orientation = RecyclerView.HORIZONTAL
        seriesManager.orientation = RecyclerView.HORIZONTAL
//        filmsAdapter = FilmItemAdapter { film -> onFilmItemClick(film, filmsAdapter.constantData, binding.premieres.text.toString()) }
//        popularAdapter = FilmItemAdapter { film -> onFilmItemClick(film, popularAdapter.constantData, binding.popular.text.toString()) }
//        dynamic1Adapter = FilmItemAdapter { film -> onFilmItemClick(film,dynamic1Adapter.constantData, binding.dynamic1.text.toString()) }
//        top250Adapter = FilmItemAdapter { film -> onFilmItemClick(film,top250Adapter.constantData, binding.top250.text.toString()) }
//        dynamic2Adapter = FilmItemAdapter { film -> onFilmItemClick(film, dynamic2Adapter.constantData, binding.dynamic2.text.toString()) }
//        seriesAdapter = FilmItemAdapter { film -> onFilmItemClick(film, seriesAdapter.constantData, binding.series.text.toString()) }
        binding.premieresRecycler.layoutManager = filmManager
        binding.popularRecycler.layoutManager = popularManager
        binding.dynamic1Recycler.layoutManager = dynamic1Manager
        binding.top250Recycler.layoutManager = top250Manager
        binding.dynamic2Recycler.layoutManager = dynamic2Manager
        binding.seriesRecycler.layoutManager = seriesManager


        binding.premieresAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), filmsAdapter.constantData, binding.premieres.text.toString()
            )
        }
        binding.popularAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), popularAdapter.constantData, binding.popular.text.toString()
            )
        }
        binding.dynamic1All.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), dynamic1Adapter.constantData, binding.dynamic1.text.toString()
            )
        }
        binding.top250All.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), top250Adapter.constantData, binding.top250.text.toString()
            )
        }
        binding.dynamic2All.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), dynamic2Adapter.constantData, binding.dynamic2.text.toString()
            )
        }
        binding.seriesAll.setOnClickListener {
            onFilmItemClick(
                FilmItemAdapter.ShowAllItemViewHolder(
                    FragmentItemShowAllBinding.inflate(
                        layoutInflater
                    )
                ), seriesAdapter.constantData, binding.series.text.toString()
            )
        }

        //Log.d("DEBug", SimpleDateFormat("MMMM", Locale.US).format(Date()).uppercase())

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.premieres(
                    Calendar.getInstance().get(Calendar.YEAR),
                    SimpleDateFormat("MMMM", Locale.US).format(Date()).uppercase()
                )
            }.fold(
                onSuccess = {
                    Log.e("DEBUG", it.toString() ?: "")
                    Log.e("DEBUG", it.message() ?: "")
                    Log.e("DEBUG", (it.body() ?: "").toString())
                    Log.e("DEBUG", (it.errorBody() ?: "").toString())
                    filmsAdapter.data = it.body()?.items ?: emptyList()
//                    filmsAdapter.constantData = it.body()?.items ?: emptyList()
                    requireActivity().runOnUiThread {
                        binding.premieresRecycler.adapter = filmsAdapter
                    }
                },
                onFailure = { Log.e("DEBUG", it.message ?: "")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                }

            )

            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.popularFilms(

                )
            }.fold(
                onSuccess = {
                    //Log.d("DEBUG popularFilms", it.body().toString())
                    popularAdapter.data = it.body()?.items ?: emptyList()
                    requireActivity().runOnUiThread {
                        binding.popularRecycler.adapter = popularAdapter
                    }
                },
                onFailure = { Log.e("DEBUG", it.message ?: it.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                }

            )
            val daoFiltersAll = dao.getFilters()

            daoFiltersAll.last().countries
            var daoFilters: Filters = if (daoFiltersAll.isNotEmpty()) {
                daoFiltersAll.last()
            } else {
                runCatching {
                    KinopoiskAPI.RetrofitInstance.getKinoAPI.filters()
                }.fold(
                    onSuccess = { response ->
                        response.body()?.let { filters ->
                            if (filters.countries != null && filters.genres != null) {
                                val currentDao = dao.getFilters()
                                if (currentDao.isNotEmpty()) {
                                    dao.updateFilters(FilmFiltersModel(filters = filters))
                                } else {
                                    dao.saveFilters(NewFilmFiltersModel(filters = filters))
                                }
                                return@fold filters
                            }
                        }
                        response.body()!! // Если нет, просто возвращаем тело ответа
                    },
                    onFailure = { error ->
                        Log.e("DEBUG", error.message ?: "")
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(),
                                getString(R.string.an_error_occured_while_loading_data, error.message), Toast.LENGTH_LONG).show()
                        }
                        null
                    }
                ) ?: Filters(emptyList(), emptyList()) // Если произошла ошибка, возвращаем пустой объект Filters
            }



            //Log.d("DEBUG filters", daoFilters.toString())
//                    val randomCountry1 = it.body()?.countries?.random()
            val randomCountry1 = (1..16).random()
            val country1 = daoFilters.countries!![randomCountry1 - 1].country
//                    val randomGenre1 = it.body()?.genres?.random()
            val randomGenre1 = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 17, 18).random()
            val genre1 = daoFilters.genres!![randomGenre1 - 1].genre

//                    val randomCountry2 = it.body()?.countries?.random()
            val randomCountry2 = (1..16).random()
            val country2 = daoFilters.countries!![randomCountry2 - 1].country

//                    val randomGenre2 = it.body()?.genres?.random()
            val randomGenre2 = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 17, 18).random()
            val genre2 = daoFilters.genres!![randomGenre2 - 1].genre



            lifecycleScope.launch(Dispatchers.IO) {
                runCatching {
                    KinopoiskAPI.RetrofitInstance.getKinoAPI.films(
                        countries = arrayOf(randomCountry1),
                        genres = arrayOf(randomGenre1),
//                                    genres = arrayOf(randomGenre1.id),
                        keyword = null,
                        imdbId = null
                    )
                }.fold(
                    onSuccess = {
//                        Log.d(
//                            "DEBUG dynamic1",
//                            it.body().toString()
//                        )
//                        Log.d(
//                            "DEBUG dynamic1",
//                            "$randomCountry1 - $country1, $randomGenre1 - $genre1"
//                        )
                        dynamic1Adapter.data = it.body()?.items ?: emptyList()
                        requireActivity().runOnUiThread {
                            binding.dynamic1.visibility = View.VISIBLE
                            binding.dynamic1All.visibility = View.VISIBLE
                            binding.dynamic1Recycler.adapter = dynamic1Adapter
                            binding.dynamic1.text = "$country1, ${capitalizeFirstLetter(genre1)}"
                        }
                    },
                    onFailure = { Log.e("DEBUG", it.message ?: it.toString())
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(),
                                getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                        }
                    }

                )
            }
            if (randomCountry2 != null && randomGenre2 != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    runCatching {
                        KinopoiskAPI.RetrofitInstance.getKinoAPI.films(
                            countries = arrayOf(randomCountry2),
//                                    genres = arrayOf(randomGenre2.id),
                            genres = arrayOf(randomGenre2),
                            keyword = null,
                            imdbId = null
                        )
                    }.fold(
                        onSuccess = {
//                            Log.d(
//                                "DEBUG dynamic2",
//                                it.body().toString()
//                            )
//                            Log.d(
//                                "DEBUG dynamic2",
//                                "$randomCountry2 - $country2, $randomGenre2 - $genre2"
//                            )
                            dynamic2Adapter.data = it.body()?.items ?: emptyList()
                            requireActivity().runOnUiThread {
                                binding.dynamic2.visibility = View.VISIBLE
                                binding.dynamic2All.visibility = View.VISIBLE
                                binding.dynamic2Recycler.adapter = dynamic2Adapter
                                binding.dynamic2.text =
                                    "$country2, ${capitalizeFirstLetter(genre2)}"

                            }
                        },
                        onFailure = { Log.e("DEBUG", it.message ?: it.toString())
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(),
                                    getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                            }
                        }

                    )
                }
            }



            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.top250List(

                )
            }.fold(
                onSuccess = {
                    //Log.d("DEBUG top250List", it.body().toString())
                    top250Adapter.data = it.body()?.items ?: emptyList()
                    requireActivity().runOnUiThread {
                        binding.top250Recycler.adapter = top250Adapter
                    }
                },
                onFailure = { Log.e("DEBUG", it.message ?: it.toString())
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                }

            )

            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.series(

                )
            }.fold(
                onSuccess = {
                    //Log.d("DEBUG series", it.body().toString())
                    seriesAdapter.data = it.body()?.items ?: emptyList()
                    requireActivity().runOnUiThread {
                        binding.seriesRecycler.adapter = seriesAdapter
                    }
                },
                onFailure = { Log.e("DEBUG", it.message ?: it.toString())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {

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
                        R.id.action_navigation_home_to_filmPageFragment,
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
                        R.id.action_navigation_home_to_filmPageFragment,
                        bundle
                    )
                }

//                if (item.type == "TV_SERIES" || item.type == "MINI_SERIES" || item.type == "TV_SHOW") {
//                    val navController = view?.findNavController()
//                    if (navController != null) {
//                        Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }
//
//                        navController.navigate(
//                            R.id.action_navigation_home_to_allFilmsFragment,
//                            bundle
//                        )
//                    }
//                } else {
//                    val navController = view?.findNavController()
//                    if (navController != null) {
//                        Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }
//
//                        navController.navigate(
//                            R.id.action_navigation_home_to_seriesPageFragment,
//                            bundle
//                        )
//                    }
//                }
            }

            is FilmItemAdapter.ShowAllItemViewHolder -> {
                //Log.d("DEBUG", "ShowAll")
                val bundle = Bundle()
                val dataType = data[0]::class.java.simpleName

                val serializedData = when (dataType) {
                    "Film" -> Json.encodeToString(data.filterIsInstance<Film>())
                    "Premiere" -> Json.encodeToString(data.filterIsInstance<Premiere>())
                    else -> ""
                }
                bundle.apply {
                    putString(getString(R.string.bundle_var_data),  serializedData)
                    putString(getString(R.string.bundle_var_datatype),  dataType)
                    putString(getString(R.string.bundle_var_title),  title)
                    val navController = view?.findNavController()
                    if (navController != null) {
                        //Log.d("BUNDLE2", bundle.toString())
//                        bundle.getString(getString(R.string.bundle_var_data))
//                            ?.let { it1 -> Log.d("BUNDLE2", it1) }

                        navController.navigate(
                            R.id.action_navigation_home_to_allFilmsFragment,
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

    fun capitalizeFirstLetter(str: String): String {
        if (str.isEmpty()) {
            return str
        }
        val firstChar = str[0]
        val capitalizedFirstChar = Character.toUpperCase(firstChar)
        return capitalizedFirstChar + str.substring(1)
    }


}