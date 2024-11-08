package ru.vasmarfas.cinewave.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.photoview.dialog.PhotoViewDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.ImageItem
import ru.vasmarfas.cinewave.databinding.FragmentFilmGalleryBinding
import ru.vasmarfas.cinewave.ui.adapters.FilmGalleryAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"
private const val ARG_DATATYPE = "dataType"
private const val ARG_KINOPOISK_ID = "kinopoiskId"

/**
 * A simple [Fragment] subclass.
 * Use the [FilmGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FilmGalleryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var data: List<ImageItem>? = null
    private var dataType: String? = null
    private var kinopoiskId: Int? = null
    private var pageTitle: String? = null
    private var _binding: FragmentFilmGalleryBinding? = null
    private val binding get() = _binding!!
    private val galleryAdapter: FilmGalleryAdapter by lazy {
        FilmGalleryAdapter { picture ->
            onFilmGalleryItemClick(
                picture
            )
        }
    } // Объект Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.getString(ARG_DATATYPE) != null) {
                dataType = it.getString(ARG_DATATYPE)
                if (it.getString(ARG_DATA) != null) {
                    data = Json.decodeFromString<List<ImageItem>>(it.getString(ARG_DATA)!!)
                    //Log.d("Allfilms Debug", data.toString())
                }
            }
            kinopoiskId = it.getInt(ARG_KINOPOISK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Log.d("AllFilmsFragment", data?.size.toString())
        _binding = FragmentFilmGalleryBinding.inflate(inflater, container, false)
//        dao = App().getInstance(requireContext()).userDao()
//        val galleryManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
//            .apply {
//                spanSizeLookup = object : SpanSizeLookup() {
//                    override fun getSpanSize(position: Int): Int {
//                        return if (position % 3 == 0) 2 else 1
//                    }
//                }
//            }
        val galleryManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) // LayoutManager

        binding.filmGalleryRecycler.layoutManager = galleryManager
        binding.filmGalleryRecycler.setItemViewCacheSize(30)
        if (pageTitle != null) {
            binding.textPageTitle.text = pageTitle
        }
//        allFilmsAdapter = FilmItemAdapter { film -> onFilmItemClick(film) }
        if (dataType == "ActorsBestFilm") binding.horizontalScrollView.visibility = View.VISIBLE
        var stillData = listOf<ImageItem>()
        var shootingData = listOf<ImageItem>()
        var posterData = listOf<ImageItem>()
        var fanArtData = listOf<ImageItem>()
        var wallpaperData = listOf<ImageItem>()
        var screenshotData = listOf<ImageItem>()
        val fullData = mutableListOf<ImageItem>()

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "STILL"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    stillData = images
                    if (stillData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images
                            binding.chipFrames.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "SHOOTING"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    shootingData = images
                    if (shootingData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images
                            binding.chipBackstage.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "POSTER"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    posterData = images
                    if (posterData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images
                            binding.chipPosters.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "FAN_ART"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    fanArtData = images
                    if (fanArtData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images
                            binding.chipFanArts.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "WALLPAPER"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    wallpaperData = images
                    if (wallpaperData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images
                            binding.chipWallpapers.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!,
                    type = "SCREENSHOT"
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    screenshotData = images
                    if (screenshotData.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            fullData += images

                            binding.chipScreenshot.visibility = View.VISIBLE
                            galleryAdapter.notifyDataSetChanged()
//                            galleryAdapter.data = fullData
//                            binding.filmGalleryRecycler.adapter = galleryAdapter
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
//            galleryAdapter.data = fullData
//            binding.filmGalleryRecycler.adapter = galleryAdapter

        }

        galleryAdapter.data = fullData
//        binding.allFilmsRecycler.addItemDecoration(SpacesItemDecoration(16))
        binding.filmGalleryRecycler.adapter = galleryAdapter
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.chipFrames.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipPosters.isChecked = false
                binding.chipBackstage.isChecked = false
                binding.chipFanArts.isChecked = false
                binding.chipWallpapers.isChecked = false
                binding.chipScreenshot.isChecked = false
                galleryAdapter.data = stillData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            }
        }

        binding.chipBackstage.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipPosters.isChecked = false
                binding.chipFrames.isChecked = false
                binding.chipFanArts.isChecked = false
                binding.chipWallpapers.isChecked = false
                binding.chipScreenshot.isChecked = false
                galleryAdapter.data = shootingData
                binding.filmGalleryRecycler.adapter = galleryAdapter
            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            }
        }
        binding.chipPosters.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipFrames.isChecked = false
                binding.chipBackstage.isChecked = false
                binding.chipFanArts.isChecked = false
                binding.chipWallpapers.isChecked = false
                binding.chipScreenshot.isChecked = false
                galleryAdapter.data = posterData
                binding.filmGalleryRecycler.adapter = galleryAdapter
            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            }
        }
        binding.chipFanArts.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipFrames.isChecked = false
                binding.chipBackstage.isChecked = false
                binding.chipPosters.isChecked = false
                binding.chipWallpapers.isChecked = false
                binding.chipScreenshot.isChecked = false
                galleryAdapter.data = fanArtData
                binding.filmGalleryRecycler.adapter = galleryAdapter
            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            }
        }
        binding.chipWallpapers.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipFrames.isChecked = false
                binding.chipBackstage.isChecked = false
                binding.chipFanArts.isChecked = false
                binding.chipPosters.isChecked = false
                binding.chipScreenshot.isChecked = false
                galleryAdapter.data = wallpaperData
                binding.filmGalleryRecycler.adapter = galleryAdapter
            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

            }
        }
        binding.chipScreenshot.setOnCheckedChangeListener { chip, isChecked ->
            if (isChecked) {
                binding.chipFrames.isChecked = false
                binding.chipBackstage.isChecked = false
                binding.chipFanArts.isChecked = false
                binding.chipWallpapers.isChecked = false
                binding.chipPosters.isChecked = false
                galleryAdapter.data = screenshotData
                binding.filmGalleryRecycler.adapter = galleryAdapter
            } else {
                galleryAdapter.data = fullData
                binding.filmGalleryRecycler.adapter = galleryAdapter

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
         * @return A new instance of fragment FilmGalleryFragment.
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

    fun onFilmGalleryItemClick(picture: ImageItem) {
        val imageUrls = mutableListOf<String>()
        val newlist = galleryAdapter.data.subList(
            galleryAdapter.data.indexOf(picture),
            galleryAdapter.data.size
        )
        for (item in newlist) {
            imageUrls.add(item.imageUrl)
        }
        val index = galleryAdapter.data.indexOf(picture)
        val layoutManager = binding.filmGalleryRecycler.layoutManager
        val viewAtPosition = layoutManager?.findViewByPosition(index)
        val imageView2 = viewAtPosition?.findViewById<ImageView>(R.id.galleryPicture)

        PhotoViewDialog.Builder(context = requireContext(), images = imageUrls) { imageView, url ->
            Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
            .withTransitionFrom(imageView2)
            .build()
            .show()

    }
}