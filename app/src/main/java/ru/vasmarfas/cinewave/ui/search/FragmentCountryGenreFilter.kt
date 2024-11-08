package ru.vasmarfas.cinewave.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.databinding.FragmentCountryGenreFilterBinding
import ru.vasmarfas.cinewave.ui.serviceItems.SearchViewModel
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_IS_COUNTRY = "isCountry"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCountryGenreFilter.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FragmentCountryGenreFilter : Fragment() {
    // TODO: Rename and change types of parameters
    private var isCountry: Boolean? = null
    private var _binding: FragmentCountryGenreFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SearchViewModel>()

    @Inject
    lateinit var dao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            isCountry = it.getBoolean(ARG_PARAM_IS_COUNTRY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryGenreFilterBinding.inflate(inflater, container, false)
        val arrayAdapter: ArrayAdapter<*>
        val data: List<String>
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        if (isCountry == true) {
            binding.textPageTitle.text = getString(R.string.enter_country)
            data = viewModel.idCountryMap.values.toList()
            arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
            binding.countryGenreFilterListView.adapter =  arrayAdapter
        } else  {
            binding.textPageTitle.text = getString(R.string.enter_genre)
            data = viewModel.idGenresMap.values.toList()
            arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
            binding.countryGenreFilterListView.adapter =  arrayAdapter
        }
        binding.countryGenreFilterListView.setOnItemClickListener { adapterView, view, position, l ->
            if (isCountry == true) {
                val item = adapterView.getItemAtPosition(position) as String
                val keys = viewModel.idCountryMap.filterValues { it == item }.keys
                viewModel.curCountry.value = keys.first()
            } else{
                val item = adapterView.getItemAtPosition(position) as String
                val keys = viewModel.idGenresMap.filterValues { it == item }.keys
                viewModel.curGenre.value = keys.first()
            }
            parentFragmentManager.popBackStack()
        }
        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Log.d("DEBUG FILTERS", query.toString())
                if (data.contains(query)) {
                    arrayAdapter.filter.filter(query)

                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.no_match_found), Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //    adapter.getFilter().filter(newText);
                arrayAdapter.filter.filter(newText)

                return false
            }
        })

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
         * @return A new instance of fragment FragmentCountryGenreFilter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            FragmentCountryGenreFilter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_IS_COUNTRY, param1)
                }
            }
    }
}