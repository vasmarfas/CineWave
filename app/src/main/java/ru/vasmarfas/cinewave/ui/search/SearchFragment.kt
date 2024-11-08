package ru.vasmarfas.cinewave.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.databinding.FragmentSearchBinding
import ru.vasmarfas.cinewave.ui.adapters.SearchItemAdapter
import ru.vasmarfas.cinewave.ui.serviceItems.SearchViewModel
import ru.vasmarfas.cinewave.ui.serviceItems.State
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SearchViewModel>()

    @Inject
    lateinit var dao: UserDao
    private val searchAdapter: SearchItemAdapter by lazy {
        SearchItemAdapter(requireContext()) { film ->
            onFilmItemClick(film)
        }
    }// Объект Adapter
    private val interestAdapter: SearchItemAdapter by lazy {
        SearchItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film
            )
        }
    } // Объект Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchView.setupWithSearchBar(binding.searchBar)
        val filmManager = LinearLayoutManager(context) // LayoutManager
        filmManager.orientation = RecyclerView.VERTICAL
        binding.searchRecyclerView.layoutManager = filmManager
        val interestManager = LinearLayoutManager(context) // LayoutManager
        interestManager.orientation = RecyclerView.VERTICAL
        binding.searchInterestRecyclerView.layoutManager = interestManager

        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используем
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Не используем
            }
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchString.value = s.toString()
            }
        })

        val interestData: MutableList<Film> = mutableListOf()

        lifecycleScope.launch(Dispatchers.IO) {

            val interests = dao.getInterestDesc()
            for (interest in interests) {
                if (interest.isFilm) {
                    interestData.add(
                        Film(
                            kinopoiskId = interest.kinopoiskId,
                            posterUrl = interest.posterUrl,
                            posterUrlPreview = interest.posterUrl,
                            nameRu = interest.title,
                            countries = emptyList(),
                            genres = emptyList(),
                            type = "",
                            year = 0
                        )
                    )
                }
            }
            interestAdapter.data = interestData
            //Log.d("PROFILE", interests.toString())
            //Log.d("PROFILE", interestData.toString())
            requireActivity().runOnUiThread {
                binding.searchInterestRecyclerView.adapter = interestAdapter
            }
            binding.searchPropsButton.setOnClickListener {
                val navController = view?.findNavController()
                if (navController != null) {
                    navController.navigate(
                        R.id.action_navigation_search_to_fragmentSearchFilters)
                }

            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.searchAdapterData.observe(viewLifecycleOwner) { films ->
                lifecycleScope.launch(Dispatchers.IO) {
                    requireActivity().runOnUiThread {
                        searchAdapter.submitList(films)
                    }
                    //Log.d("SEARCH DEBUG",  films.toString())
                    requireActivity().runOnUiThread {
                        binding.searchRecyclerView.adapter = searchAdapter
                    }

                }
            }

            viewModel.isResultsEmpty.observe(viewLifecycleOwner) {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (it)  {
                        requireActivity().runOnUiThread {

                            binding.searchRecyclerView.visibility = View.GONE
                            binding.searchNotFoundText.visibility = View.VISIBLE
                        }

                    } else {
                        requireActivity().runOnUiThread {
                            binding.searchRecyclerView.visibility = View.VISIBLE
                            binding.searchNotFoundText.visibility = View.GONE
                        }
                    }
                }
            }
            viewModel.state.collect {
                when (it) {
                    State.Loading -> {
                        requireActivity().runOnUiThread {
                            binding.progressBarSearch.visibility = View.VISIBLE
                        }
                    }
                    State.Success -> {
                        requireActivity().runOnUiThread {
                            binding.progressBarSearch.visibility = View.GONE
                        }
                    }
                    State.NoTextEnough -> {

                    }

                }
            }
        }
    }
    fun onFilmItemClick(item: Film) {


        val bundle = Bundle()
        bundle.putInt(getString(R.string.bundle_var_kinopoiskid),  item.kinopoiskId)
        //Log.d("DEBUG", "Film")
        val navController = view?.findNavController()
        if (navController != null) {
            //Log.d("BUNDLE2", bundle.toString())
//            bundle.getString(getString(R.string.bundle_var_data))
//                ?.let { it1 -> Log.d("BUNDLE2", it1) }

            navController.navigate(
                R.id.action_navigation_search_to_filmPageFragment,
                bundle
            )
        }


    }
}