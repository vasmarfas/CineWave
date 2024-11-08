package ru.vasmarfas.cinewave.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.photoview.dialog.PhotoViewDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.vasmarfas.cinewave.R
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.data.retrofit.entity.Film
import ru.vasmarfas.cinewave.data.retrofit.entity.ImageItem
import ru.vasmarfas.cinewave.data.retrofit.entity.Premiere
import ru.vasmarfas.cinewave.data.retrofit.entity.StaffPerson
import ru.vasmarfas.cinewave.databinding.FragmentFilmPageBinding
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.NewFilmPersonInfoModel
import ru.vasmarfas.cinewave.data.db.entity.NewInterestModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewFavouriteListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewToWatchListModel
import ru.vasmarfas.cinewave.data.db.entity.builtinCollections.NewViewedListModel
import ru.vasmarfas.cinewave.data.retrofit.entity.SimilarFilm
import ru.vasmarfas.cinewave.ui.adapters.FilmGalleryAdapter
import ru.vasmarfas.cinewave.ui.adapters.FilmItemAdapter
import ru.vasmarfas.cinewave.ui.adapters.FilmStaffAdapter
import ru.vasmarfas.cinewave.ui.serviceItems.BottomDialogFragment
import java.io.ByteArrayOutputStream
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_KINOPOSK_ID = "kinopoiskId"

/**
 * A simple [Fragment] subclass.
 * Use the [FilmPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FilmPageFragment : Fragment() {
    private val MAX_LINES_COLLAPSED = 3
    private val INITIAL_IS_COLLAPSED = false
    private var isCollapsed = INITIAL_IS_COLLAPSED

    private var kinopoiskId: Int? = null
    private var _binding: FragmentFilmPageBinding? = null
    private val binding get() = _binding!!
    private val filmActorsAdapter: FilmStaffAdapter by lazy {
        FilmStaffAdapter { actor ->
            onFilmStaffItemClick(
                actor
            )
        }
    } // Объект Adapter
    private val filmWorkersAdapter: FilmStaffAdapter by lazy {
        FilmStaffAdapter { actor ->
            onFilmStaffItemClick(
                actor
            )
        }
    } // Объект Adapter
    private val galleryAdapter: FilmGalleryAdapter by lazy {
        FilmGalleryAdapter { picture ->
            onFilmGalleryItemClick(
                picture
            )
        }
    } // Объект Adapter
    private val similarFilmsAdapter: FilmItemAdapter by lazy {
        FilmItemAdapter(requireContext()) { film ->
            onFilmItemClick(
                film,
                similarFilmsAdapter.data,
                binding.similarFilms.text.toString()
            )
        }
    } // Объект Adapter
    private var currentFilm: Film? = null

    @Inject
    lateinit var dao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kinopoiskId = it.getInt(ARG_KINOPOSK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmPageBinding.inflate(inflater, container, false)
        val filmActorsManager = GridLayoutManager(context, 4) // LayoutManager
        val filmWorkersManager = GridLayoutManager(context, 2) // LayoutManager
        val galleryManager = LinearLayoutManager(context) // LayoutManager
        val similarFilmsManager = LinearLayoutManager(context) // LayoutManager

        filmActorsManager.orientation = RecyclerView.HORIZONTAL
        filmWorkersManager.orientation = RecyclerView.HORIZONTAL
        galleryManager.orientation = RecyclerView.HORIZONTAL
        similarFilmsManager.orientation = RecyclerView.HORIZONTAL

//        filmActorsAdapter = FilmStaffAdapter { actor -> onFilmStaffItemClick(actor) }
//        filmWorkersAdapter = FilmStaffAdapter { actor -> onFilmStaffItemClick(actor) }
//        galleryAdapter = FilmGalleryAdapter  {picture -> onFilmGalleryItemClick(picture)}
//        similarFilmsAdapter =  FilmItemAdapter { film ->  onFilmItemClick(film, similarFilmsAdapter.data, binding.similarFilms.text.toString()) }

        binding.filmActorsRecycler.layoutManager = filmActorsManager
        binding.filmWorkersRecycler.layoutManager = filmWorkersManager
        binding.filmGalleryRecycler.layoutManager = galleryManager
        binding.similarFilmsRecycler.layoutManager = similarFilmsManager

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (dao.getAllViewedFilmIds().contains(kinopoiskId)) {
                launch(Dispatchers.Main) {
                    binding.hideButton.setImageResource(R.drawable.icon_hide_show_button)
                }
            }
            if (dao.getAllFavouriteFilmIds().contains(kinopoiskId)) {
                launch(Dispatchers.Main) {
                    binding.favouriteButton.setImageResource(R.drawable.icon_like_dislike_button)
                }
            }
            if (dao.getAllToWatchFilmIds().contains(kinopoiskId)) {
                launch(Dispatchers.Main) {
                    binding.bookmarkButton.setImageResource(R.drawable.icon_bookmark_filled_button)
                }
            }
        }


        binding.descriptionText.setInterpolator(OvershootInterpolator())
        binding.descriptionText.expandInterpolator = OvershootInterpolator()
        binding.descriptionText.collapseInterpolator = OvershootInterpolator()
        binding.descriptionText.setOnClickListener {
            binding.descriptionText.toggle()
        }
        binding.seriesSeasonsAll.setOnClickListener {
            val bundle = Bundle()
            kinopoiskId?.let { it1 ->
                bundle.putInt(getString(R.string.bundle_var_kinopoiskid),
                    it1
                )
            }
            //Log.d("DEBUG", "seriesSeasonsAll filmpage")

            val navController = view?.findNavController()
            if (navController != null) {
                //Log.d("BUNDLE2", bundle.toString())
//                bundle.getString(getString(R.string.bundle_var_data))
//                    ?.let { it1 -> Log.d("BUNDLE2", it1) }

                navController.navigate(
                    R.id.action_filmPageFragment_to_seasonsSeriesFragment,
                    bundle
                )
            }
        }

        binding.favouriteButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val favouriteFilms = dao.getAllFavouriteFilmIds()
                if (favouriteFilms.contains(kinopoiskId)) {
                    kinopoiskId?.let { it1 -> dao.deleteFromFavouriteFilmIds(it1) }
                    launch(Dispatchers.Main) {
                        binding.favouriteButton.setImageResource(R.drawable.icon_like_button)
                    }
                } else {
                    kinopoiskId?.let { it1 -> NewFavouriteListModel(favouriteFilmId = it1) }
                        ?.let { it2 -> dao.addToFavouriteFilmIds(it2) }
                    launch(Dispatchers.Main) {
                        binding.favouriteButton.setImageResource(R.drawable.icon_like_dislike_button)
                    }
                }

            }
        }
        binding.bookmarkButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val toWatchFilms = dao.getAllToWatchFilmIds()
                if (toWatchFilms.contains(kinopoiskId)) {
                    kinopoiskId?.let { it1 -> dao.deleteFromToWatchFilmIds(it1) }
                    launch(Dispatchers.Main) {
                        binding.bookmarkButton.setImageResource(R.drawable.icon_bookmark_button)
                    }
                } else {
                    kinopoiskId?.let { it1 -> NewToWatchListModel(toWatchFilmId = it1) }
                        ?.let { it2 -> dao.addToToWatchFilmIds(it2) }
                    launch(Dispatchers.Main) {
                        binding.bookmarkButton.setImageResource(R.drawable.icon_bookmark_filled_button)
                    }
                }

            }
        }
        binding.hideButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val viewedFilms = dao.getAllViewedFilmIds()
                if (viewedFilms.contains(kinopoiskId)) {
                    kinopoiskId?.let { it1 -> dao.deleteFromViewedFilmIds(it1) }
                    launch(Dispatchers.Main) {
                        binding.hideButton.setImageResource(R.drawable.icon_hide_button)
                    }
                } else {
                    kinopoiskId?.let { it1 -> NewViewedListModel(viewedFilmId = it1) }
                        ?.let { it2 -> dao.addToViewedFilmIds(it2) }
                    launch(Dispatchers.Main) {
                        binding.hideButton.setImageResource(R.drawable.icon_hide_show_button)
                    }
                }

            }
        }
        fun FragmentActivity.showDialog2(dialog: BottomDialogFragment, tag: String? = null) =
            dialog.show(this.supportFragmentManager, tag)

        binding.shareButton.setOnClickListener {
            if (currentFilm != null) {
                val currentUrl = when {
                    currentFilm!!.imdbId != null -> "https://www.imdb.com/title/${currentFilm!!.imdbId}"
                    currentFilm!!.webUrl != null -> currentFilm!!.webUrl
                    else -> ""
                }
                val currentName = when {
                    currentFilm!!.nameRu != null -> currentFilm!!.nameRu
                    currentFilm!!.nameEn != null -> currentFilm!!.nameEn
                    currentFilm!!.nameOriginal != null -> currentFilm!!.nameOriginal
                    else -> null
                }
                if (currentUrl != "") {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, currentUrl)
                        putExtra(Intent.EXTRA_TITLE, currentName)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, currentName)
                    startActivity(shareIntent)
                }
            }
        }
        binding.moreButton.setOnClickListener {
            val bundle = Bundle()

            bundle.apply {
                kinopoiskId?.let { it1 -> putInt("kinopoiskId", it1) }
                putString(getString(R.string.bundle_var_ratingname), binding.textFirstLineRatingName.text.toString())
                putString(getString(R.string.bundle_var_yeargenre), binding.textSecondLineYearGenre.text.toString())

                bundle.putInt(getString(R.string.bundle_var_favbuttom), binding.favouriteButton.id)
                bundle.putInt(getString(R.string.bundle_var_bookmarkbuttom), binding.bookmarkButton.id)
                bundle.putInt(getString(R.string.bundle_var_viewedbuttom), binding.hideButton.id)


                val bitmap = (binding.filmPoster.drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bitmapdata = stream.toByteArray()

                putByteArray(getString(R.string.bundle_var_drawablebytes), bitmapdata)


            }
            val bottomDiag = BottomDialogFragment()
            bottomDiag.arguments = bundle
            requireActivity().showDialog2(bottomDiag)


        }

        binding.filmGalleryAll.setOnClickListener {

            //Log.d("DEBUG", "ShowAll")
            val bundle = Bundle()
            val dataType = galleryAdapter.constantData[0]::class.java.simpleName

            val serializedData =
                Json.encodeToString(galleryAdapter.constantData.filterIsInstance<ImageItem>())

            bundle.apply {
                putString(getString(R.string.bundle_var_data), serializedData)
                putString(getString(R.string.bundle_var_datatype), dataType)
                kinopoiskId?.let { it1 -> putInt("kinopoiskId", it1) }
                val navController = view?.findNavController()
                if (navController != null) {
                    //Log.d("BUNDLE2", bundle.toString())
//                    bundle.getString(getString(R.string.bundle_var_data))
//                        ?.let { it1 -> Log.d("BUNDLE2", it1) }

                    navController.navigate(
                        R.id.action_filmPageFragment_to_filmGalleryFragment,
                        bundle
                    )
//                                navBarHideListener?.hideNavBar()

                }


            }
        }


        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.staffByFilmId(
                    kinopoiskId!!
                )
            }.fold(
                onSuccess = {
                    val filmActors: MutableList<StaffPerson> =
                        emptyList<StaffPerson>().toMutableList()
                    val filmWorkers: MutableList<StaffPerson> =
                        emptyList<StaffPerson>().toMutableList()
                    val filmStaff = it.body() ?: emptyList()
                    for (person in filmStaff) {
                        if (person.professionKey == "ACTOR") filmActors.add(person) else filmWorkers.add(
                            person
                        )
                    }

                    //Log.d("DEBUG", filmActors.toString())
                    //Log.d("DEBUG", filmWorkers.toString())
                    filmActorsAdapter.data = filmActors.toList().take(20)
                    filmWorkersAdapter.data = filmWorkers.toList().take(6)
                    requireActivity().runOnUiThread {
                        binding.filmActorsRecycler.adapter = filmActorsAdapter
                        binding.filmWorkersRecycler.adapter = filmWorkersAdapter
                        binding.filmActorsAll.text = filmActors.size.toString()
                        binding.filmWorkersAll.text = filmWorkers.size.toString()
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
                KinopoiskAPI.RetrofitInstance.getKinoAPI.filmById(
                    kinopoiskId!!
                )
            }.fold(
                onSuccess = {
                    val film = it.body()
                    currentFilm = film
                    if (film != null) {
                        val filmName: String = film.nameRu
                            ?: film.nameEn ?: film.nameOriginal ?: "No name"
                        if (dao.getInterestById(film.kinopoiskId) == null) {
                            dao.saveInterest(NewInterestModel(
                                kinopoiskId = film.kinopoiskId,
                                isFilm = true,
                                isPerson = false,
                                posterUrl = film.posterUrl,
                                title = filmName
                            ))
                        }
                        if (dao.getFilmPersonInfoById(film.kinopoiskId) == null) {

                            dao.saveFilmPersonInfo(
                                NewFilmPersonInfoModel(
                                    id = film.kinopoiskId,
                                    isFilm = true,
                                    isPerson = false,
                                    posterUrl = film.posterUrl,
                                    title = filmName
                                )
                            )
                        }

                        requireActivity().runOnUiThread {
                            binding.descriptionText.text = film.description
//                            Log.d(
//                                "DEBUG IMAGE",
//                                "${binding.filmPoster.width} -- ${binding.filmPoster.height}"
//                            )

                            Glide
                                .with(binding.filmPoster.context)
//                                .load(R.drawable.gradient_mask_film_page)     //шейп градиент
                                .load(film.posterUrl)     //картинка из апи
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.filmPoster)

                            val rating = when {
                                film.ratingKinopoisk != null -> film.ratingKinopoisk
                                film.ratingImdb != null -> film.ratingImdb
                                film.ratingFilmCritics != null -> film.ratingFilmCritics
                                else -> null
                            }
                            val name = when {
                                film.nameRu != null -> film.nameRu
                                film.nameEn != null -> film.nameEn
                                film.nameOriginal != null -> film.nameOriginal
                                else -> null
                            }

                            var firstLine = ""
                            if (rating != null) firstLine += "<b>$rating</b> "
                            if (name != null) firstLine += name
                            binding.textFirstLineRatingName.text = Html.fromHtml(firstLine)

                            var secondLine = ""
                            if (film.year != null) secondLine += film.year
                            if (film.genres.isNotEmpty()) {
                                for (genre in film.genres.take(2)) {
                                    secondLine += ", "
                                    secondLine += genre.genre
                                }
                            }
                            binding.textSecondLineYearGenre.text = secondLine

                            var thirdLine = ""
                            if (film.countries.isNotEmpty()) thirdLine += film.countries[0].country
                            var lenght = ""
                            if (film.filmLength != null) {
                                val hours: Int = film.filmLength / 60
                                val minutes = film.filmLength - hours * 60
                                lenght = ", $hours ч $minutes мин"
                                thirdLine += lenght
                            }
                            when {
                                film.ratingAgeLimits != null -> thirdLine += ", ${film.ratingAgeLimits.filter { it.isDigit() }}+"
                                film.ratingMpaa != null -> thirdLine += when (film.ratingMpaa) {
                                    "g" -> ", 0+"
                                    "pg" -> ", 6+"
                                    "pg13" -> ", 12+"
                                    "r" -> ", 16+"
                                    "nc17" -> ", 18+"
                                    else -> ""
                                }
                            }
                            binding.textThirdLineCountryDuration.text = thirdLine

                            if (film.type == "TV_SERIES" || film.type == "MINI_SERIES" || film.type == "TV_SHOW") {
                                launch(Dispatchers.IO) {
                                    runCatching {
                                        KinopoiskAPI.RetrofitInstance.getKinoAPI.seasons(
                                            kinopoiskId!!
                                        )
                                    }.fold(
                                        onSuccess = {
                                            val seriesSeasons = it.body()
                                            Log.e("DEBUG", it.message() ?: "")
                                            Log.e("DEBUG", (it.body() ?: "").toString())
                                            if (seriesSeasons != null) {
                                                val seasonsCount = seriesSeasons.total
                                                var seriesCount = 0
                                                for (season in seriesSeasons.items) {
                                                    for (episode in season.episodes) {
                                                        seriesCount++
                                                    }
                                                }
                                                requireActivity().runOnUiThread {
                                                    binding.seriesSeasonsText.text =
                                                        getString(
                                                            R.string.seasong_and_series_count_with_var,
                                                            seasonsCount.toString(),
                                                            seriesCount.toString()
                                                        )
                                                    binding.seriesSeasons.visibility = View.VISIBLE
                                                    binding.seriesSeasonsAll.visibility =
                                                        View.VISIBLE
                                                    binding.seriesSeasonsText.visibility =
                                                        View.VISIBLE
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
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.imagesByFilmId(
                    kinopoiskId!!
                )
            }.fold(
                onSuccess = {
                    val images = it.body()?.items ?: emptyList()
                    galleryAdapter.constantData = images
                    galleryAdapter.data = images.take(20)

                    if (images.isEmpty()) {
                        requireActivity().runOnUiThread {
                            binding.filmGallery.visibility = View.GONE
                            binding.filmGalleryAll.visibility = View.GONE
                            binding.filmGalleryRecycler.visibility = View.GONE
                        }
                    }
                    requireActivity().runOnUiThread {
                        binding.filmGalleryRecycler.adapter = galleryAdapter
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
                //Log.d("DEBUG FILMS", kinopoiskId.toString())
                KinopoiskAPI.RetrofitInstance.getKinoAPI.similarFilmsByFilmId(
                    kinopoiskId!!
                )
            }.fold(
                onSuccess = {
                    val films = it.body()?.items ?: emptyList()
                    similarFilmsAdapter.data = films
                    //Log.d("DEBUG FILMS", films.toString())
                    //Log.d("DEBUG FILMS", it.toString())
                    //Log.d("DEBUG FILMS", it.body().toString())
                    if (films.isNotEmpty()) {
                        requireActivity().runOnUiThread {
                            binding.similarFilmsAll.text = films.size.toString()

                            binding.similarFilmsRecycler.adapter = similarFilmsAdapter
                            binding.similarFilms.visibility = View.VISIBLE
                            binding.similarFilmsAll.visibility = View.VISIBLE
                            binding.similarFilmsRecycler.visibility = View.VISIBLE
                            val constraintLayout: ConstraintLayout =
                                binding.constraint
                            val constraintSet = ConstraintSet()
                            constraintSet.clone(constraintLayout)
                            constraintSet.clear(
                                binding.filmGalleryRecycler.id,
                                ConstraintSet.BOTTOM
                            )
                            constraintSet.applyTo(constraintLayout)

                        }
                    }

                },
                onFailure = {
                    Log.e("DEBUG", it.message ?: "")
                    Log.e("DEBUG", it.stackTrace.joinToString() ?: "")
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
        return binding.root
    }

    fun onFilmStaffItemClick(staff: StaffPerson) {
        val bundle = Bundle()
        bundle.putInt(getString(R.string.bundle_var_staffId),  staff.staffId)
        //Log.d("DEBUG", "Premiere")

        val navController = view?.findNavController()
        if (navController != null) {
            navController.navigate(
                R.id.action_filmPageFragment_to_actorPageFragment,
                bundle
            )
        }
    }

    fun onFilmGalleryItemClick(picture: ImageItem) {
        val imageUrls = mutableListOf<String>()
        val newlist = galleryAdapter.data.subList(
            galleryAdapter.data.indexOf(picture),
            galleryAdapter.data.size
        )
        for (item in galleryAdapter.data) {
            imageUrls.add(item.imageUrl)
        }
        val index = galleryAdapter.data.indexOf(picture)
        val layoutManager = binding.filmGalleryRecycler.layoutManager
        val viewAtPosition = layoutManager?.findViewByPosition(index)
        val imageView2 = viewAtPosition?.findViewById<ImageView>(R.id.galleryPicture)

        val dialog = PhotoViewDialog.Builder(
            context = requireContext(),
            images = imageUrls
        ) { imageView, url ->
            Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }

            .withTransitionFrom(imageView2)
            .build()
        dialog.setCurrentPosition(index)
        dialog.show()
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
                        R.id.action_filmPageFragment_self,
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
                            R.id.action_filmPageFragment_self,
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
                            R.id.action_filmPageFragment_self,
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
                        R.id.action_filmPageFragment_self,
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
                            R.id.action_filmPageFragment_to_allFilmsFragment,
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
         * @return A new instance of fragment FilmPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(kinopoiskId: Int) =
            FilmPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_KINOPOSK_ID, kinopoiskId)
                }
            }
    }
}