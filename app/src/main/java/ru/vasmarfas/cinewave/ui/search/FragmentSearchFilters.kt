package ru.vasmarfas.cinewave.ui.search

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.databinding.FragmentSearchBinding
import ru.vasmarfas.cinewave.databinding.FragmentSearchFiltersBinding
import ru.vasmarfas.cinewave.ui.serviceItems.SearchViewModel
import ru.vasmarfas.cinewave.ui.serviceItems.State
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSearchFilters.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FragmentSearchFilters : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSearchFiltersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SearchViewModel>()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchFiltersBinding.inflate(inflater, container, false)
        binding.showFilterToggleGroup.check(R.id.button1)
        if (viewModel.curCountry != null) {
            binding.searchCountry.text = viewModel.idCountryMap[viewModel.curCountry!!.value]
        } else {
            binding.searchCountry.text = getString(R.string.any_fem)
        }
        if (viewModel.curGenre != null) {
            binding.searchGenre.text = viewModel.idGenresMap[viewModel.curGenre!!.value]
        } else {
            binding.searchGenre.text = getString(R.string.any_male)
        }
        binding.searchYear.text = "с ${viewModel.curYearFrom.value} до ${viewModel.curYearTo.value}"
        binding.searchRatingSlider.values = listOf(viewModel.curRatingFrom.value?.toFloat() ?: 0f, viewModel.curRatingTo.value?.toFloat() ?: 10f)
//        binding.searchRatingSlider.valueFrom = viewModel.curRatingFrom.value?.toFloat() ?: 0f
//        binding.searchRatingSlider.valueTo = viewModel.curRatingTo.value?.toFloat() ?: 10f
        if ((viewModel.curRatingTo.value!! - viewModel.curRatingFrom.value!!) == 10) {
            binding.searchRating.text = getString(R.string.any_male)
        } else {
            binding.searchRating.text =
                "от ${viewModel.curRatingFrom.value} до ${viewModel.curRatingTo.value}"
        }
        binding.sortFilterToggleGroup.check(R.id.buttonSort1)
        binding.viewedImage
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.showFilterToggleGroup.addOnButtonCheckedListener { materialButtonToggleGroup, checkedId, isChecked ->
            when(checkedId) {
                R.id.button1 ->  {viewModel.curType.value = "ALL"}
                R.id.button2 ->  {viewModel.curType.value = "FILM"}
                R.id.button3 ->  {viewModel.curType.value = "TV_SERIES"}
            }
        }
        binding.searchCountry.setOnClickListener {
            val navController = view?.findNavController()
            if (navController != null) {
                val bundle = Bundle()
                bundle.putBoolean("isCountry", true)
                navController.navigate(R.id.action_fragmentSearchFilters_to_fragmentCountryGenreFilter, bundle)

            }
        }
        binding.searchGenre.setOnClickListener {
            val navController = view?.findNavController()
            if (navController != null) {
                val bundle = Bundle()
                bundle.putBoolean("isCountry", false)
                navController.navigate(R.id.action_fragmentSearchFilters_to_fragmentCountryGenreFilter, bundle)

            }

        }

        binding.searchYear.setOnClickListener {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)


            val numberPicker = NumberPicker(requireContext())
            numberPicker.minValue = 1850 // Минимальный год
            numberPicker.maxValue = currentYear // Максимальный возможный год
            numberPicker.value = viewModel.curYearFrom.value!! // Установка текущего года

            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle(getString(R.string.search_in_period_from))
            dialog.setPositiveButton(getString(R.string.OK)) { _, _ ->
                val selectedYear = numberPicker.value
                viewModel.curYearFrom.value = selectedYear
            }
            dialog.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            dialog.setView(numberPicker)
            val alertDialog = dialog.create()
            alertDialog.setOnDismissListener {
                val numberPicker2 = NumberPicker(requireContext())
                numberPicker2.minValue = viewModel.curYearFrom.value!! // Минимальный год
                numberPicker2.maxValue = currentYear // Максимальный возможный год
                numberPicker2.value = viewModel.curYearTo.value!! // Установка текущего года

                val dialog2 = AlertDialog.Builder(requireContext())
                dialog2.setTitle(getString(R.string.search_in_period_to))
                dialog2.setPositiveButton(getString(R.string.OK)) { _, _ ->
                    val selectedYear = numberPicker2.value
                    viewModel.curYearTo.value = selectedYear
                }
                dialog2.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                dialog2.setView(numberPicker2)

                dialog2.show()
            }

            alertDialog.show()
        }
        binding.searchRatingSlider.addOnChangeListener { rangeSlider, fl, b ->
            viewModel.curRatingFrom.value = rangeSlider.values.first().toInt()
            viewModel.curRatingTo.value = rangeSlider.values.last().toInt()
        }
        binding.sortFilterToggleGroup.addOnButtonCheckedListener { materialButtonToggleGroup, checkedId, isChecked ->
            when(checkedId) {
                R.id.buttonSort1 ->  {viewModel.curOrderBy.value = "YEAR"}
                R.id.buttonSort2 ->  {viewModel.curOrderBy.value = "NUM_VOTE"}
                R.id.buttonSort3 ->  {viewModel.curOrderBy.value = "RATING"}
            }
        }
        binding.viewedImage.setOnClickListener {
            if (viewModel.hideViewed.value == true) {
                viewModel.hideViewed.value = false
            } else {
                viewModel.hideViewed.value = true

            }
        }
        binding.searchViewed.setOnClickListener {
            if (viewModel.hideViewed.value == true) {
                viewModel.hideViewed.value = false
            } else {
                viewModel.hideViewed.value = true

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
//            viewModel.curType.observe(viewLifecycleOwner) {
//                when (it){
//                    "ALL" -> binding.showFilterToggleGroup.check(R.id.button1)
//                    "FILM" -> binding.showFilterToggleGroup.check(R.id.button2)
//                    "TV_SERIES" -> binding.showFilterToggleGroup.check(R.id.button3)
//                }
//            }
            viewModel.curCountry.observe(viewLifecycleOwner) {
                binding.searchCountry.text = viewModel.idCountryMap[it]
            }
            viewModel.curGenre.observe(viewLifecycleOwner) {
                binding.searchGenre.text = viewModel.idGenresMap[it]
            }
            viewModel.curYearFrom.observe(viewLifecycleOwner) {
                //Log.d("SEARCH DEBUG", viewModel.curYearFrom.value.toString())
                binding.searchYear.text = "с ${viewModel.curYearFrom.value} до ${viewModel.curYearTo.value}"
                if ((viewModel.curYearFrom.value == 1850) && (viewModel.curYearTo.value == Calendar.getInstance().get(Calendar.YEAR))) binding.searchYear.text = "любой"
            }
            viewModel.curYearTo.observe(viewLifecycleOwner) {
                //Log.d("SEARCH DEBUG", viewModel.curYearTo.value.toString())
                binding.searchYear.text = "с ${viewModel.curYearFrom.value} до ${viewModel.curYearTo.value}"
                if ((viewModel.curYearFrom.value == 1850) && (viewModel.curYearTo.value == Calendar.getInstance().get(Calendar.YEAR))) binding.searchYear.text = "любой"
            }

            viewModel.curRatingFrom.observe(viewLifecycleOwner) {
//                Log.d("SEARCH DEBUG", viewModel.curRatingFrom.value.toString())
//                Log.d("SEARCH DEBUG", viewModel.curRatingTo.value.toString())
//                binding.searchRatingSlider.valueFrom = viewModel.curRatingFrom.value?.toFloat() ?: 0f

                if ((viewModel.curRatingFrom.value == 0) && (viewModel.curRatingTo.value == 10)) binding.searchRating.text = getString(R.string.any_male)  else binding.searchRating.text = "от ${viewModel.curRatingFrom.value} до ${viewModel.curRatingTo.value}"
            }
            viewModel.curRatingTo.observe(viewLifecycleOwner) {
//                binding.searchRatingSlider.valueTo = viewModel.curRatingTo.value?.toFloat() ?: 10f
                if ((viewModel.curRatingFrom.value == 0) && (viewModel.curRatingTo.value == 10)) binding.searchRating.text = getString(R.string.any_male)  else binding.searchRating.text = "от ${viewModel.curRatingFrom.value} до ${viewModel.curRatingTo.value}"
            }
//            viewModel.curOrderBy.observe(viewLifecycleOwner) {
//                when (it){
//                    "YEAR" -> binding.sortFilterToggleGroup.check(R.id.buttonSort1)
//                    "NUM_VOTE" -> binding.sortFilterToggleGroup.check(R.id.buttonSort2)
//                    "RATING" -> binding.sortFilterToggleGroup.check(R.id.buttonSort3)
//                }
//            }
            viewModel.hideViewed.observe(viewLifecycleOwner) {
                if (viewModel.hideViewed.value == true) {
                    binding.searchViewed.text = getString(R.string.turn_on_viewed)
                    binding.viewedImage.setImageResource(R.drawable.icon_hide_show_button)
                } else {
                    binding.searchViewed.text = getString(R.string.turn_off_viewed)
                    binding.viewedImage.setImageResource(R.drawable.icon_hide_button)
                }
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
         * @return A new instance of fragment FragmentSearchFilters.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSearchFilters().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}