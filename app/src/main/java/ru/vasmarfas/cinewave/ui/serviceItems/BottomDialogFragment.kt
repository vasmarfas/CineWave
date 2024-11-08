package ru.vasmarfas.cinewave.ui.serviceItems

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.vasmarfas.cinewave.App
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.databinding.FragmentBottomDialogBinding
import ru.vasmarfas.cinewave.ui.adapters.AddToCollectionItemAdapter
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_KINOPOISK_ID = "kinopoiskId"
private const val ARG_RATING_NAME = "ratingName"
private const val ARG_YEAR_GENRE = "yearGenre"
private const val ARG_DRAWABLE_BYTES = "drawableBytes"
private const val ARG_FAVOURITE_BUTTON = "favButtom"
private const val ARG_BOOKMARK_BUTTON = "bookmarkButtom"
private const val ARG_HIDE_BUTTON = "viewedButtom"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BottomDialogFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var kinopoiskId: Int? = null
    private var ratingName: String? = null
    private var yearGenre: String? = null
    private var drawableBytes: ByteArray? = null
    private var favButton: ImageButton? = null
    private var bookmarkButton: ImageButton? = null
    private var hideButton: ImageButton? = null
    private var _binding: FragmentBottomDialogBinding? = null
    @Inject
    lateinit var dao: UserDao

    private val binding get() = _binding!!
    lateinit var _productAdapter: AddToCollectionItemAdapter

    val addToCollectionAdapter: AddToCollectionItemAdapter by lazy {
        AddToCollectionItemAdapter(requireContext(), kinopoiskId!!, favButton, bookmarkButton, hideButton, parentFragmentManager, dao)
    }

    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kinopoiskId = it.getInt(ARG_KINOPOISK_ID)
            ratingName = it.getString(ARG_RATING_NAME)
            yearGenre = it.getString(ARG_YEAR_GENRE)
            drawableBytes = it.getByteArray(ARG_DRAWABLE_BYTES)
            favButton = requireActivity().findViewById<ImageButton>(it.getInt(ARG_FAVOURITE_BUTTON))
            hideButton = requireActivity().findViewById<ImageButton>(it.getInt(ARG_HIDE_BUTTON))
            bookmarkButton = requireActivity().findViewById<ImageButton>(it.getInt(
                ARG_BOOKMARK_BUTTON
            ))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBottomDialogBinding.inflate(inflater, container, false)
        val ratingNameList = ratingName?.split(", ")
        if (ratingNameList != null && ratingNameList.isNotEmpty()) {
            if (ratingNameList.size == 2) {
                binding.filmName.text = ratingNameList[1]
            } else if (ratingNameList.size == 1) {
                binding.filmName.text = ratingNameList[0]
            }
        }
        val bitmap = drawableBytes?.let { BitmapFactory.decodeByteArray(drawableBytes, 0, it.size) }
        val img = binding.filmPoster as ImageView
        img.setImageBitmap(bitmap)
        binding.filmDescription.text = yearGenre
        _productAdapter = AddToCollectionItemAdapter(requireContext(), kinopoiskId!!, favButton, bookmarkButton, hideButton, parentFragmentManager, dao)

        val addToCollectionManager = LinearLayoutManager(context) // LayoutManager
        addToCollectionManager.orientation = RecyclerView.VERTICAL
        binding.collectionsRecycler.layoutManager = addToCollectionManager
        binding.collectionsRecycler.adapter = _productAdapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.getCollectionsLiveData.observe(viewLifecycleOwner) { collections ->
                Log.e("DEBUG BOTTOM - adapter", _productAdapter.data.toString())
                _productAdapter.submitList(collections)
                Log.e("DEBUG BOTTOM - adapter", _productAdapter.data.toString())
            }
        }

//        viewModel.getCollectionsLiveData.observe(viewLifecycleOwner) { collections ->
////            addToCollectionAdapter.data = collections
//            Log.e("DEBUG BOTTOM - adapter", addToCollectionAdapter.data.toString())
//            addToCollectionAdapter.submitList(collections)
//            binding.collectionsRecycler.adapter = addToCollectionAdapter
//            Log.e("DEBUG BOTTOM - adapter", addToCollectionAdapter.data.toString())
//
//        }

//        binding.collectionsRecycler.adapter =  addToCollectionAdapter



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BottomDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(kinopoiskId: String, param2: String) =
            BottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KINOPOISK_ID, kinopoiskId)
                }
            }
    }
}