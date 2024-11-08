package ru.vasmarfas.cinewave

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.databinding.ActivityMainBinding
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.FilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.NewFilmFiltersModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var dao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                KinopoiskAPI.RetrofitInstance.getKinoAPI.filters(
                )
            }.fold(
                onSuccess = {
                    if (it.body() != null) {
                        if (it.body()!!.countries!= null && it.body()!!.genres != null) {
                            val currentDao = dao.getFilters()
                            if(!currentDao.isEmpty()) {
                                dao.updateFilters(
                                    FilmFiltersModel(
                                        filters = it.body()!!
                                    )
                                )
                            } else {
                                dao.saveFilters(
                                    NewFilmFiltersModel(
                                        filters = it.body()!!
                                    )
                                )
                            }
                        }
                    }


                    val randomCountry1 = (1..16).random()
                    val country1 = it.body()?.countries!![randomCountry1 - 1].country
//                    val randomGenre1 = it.body()?.genres?.random()
                    val randomGenre1 = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 17, 18).random()
                    val genre1 = it.body()?.genres!![randomGenre1 - 1].genre

//                    val randomCountry2 = it.body()?.countries?.random()
                    val randomCountry2 = (1..16).random()
                    val country2 = it.body()?.countries!![randomCountry2 - 1].country

//                    val randomGenre2 = it.body()?.genres?.random()
                    val randomGenre2 = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 17, 18).random()
                    val genre2 = it.body()?.genres!![randomGenre2 - 1].genre
                }, onFailure = { Log.e("DEBUG", it.message ?: "")
                    runOnUiThread {
                        Toast.makeText(applicationContext,
                            getString(R.string.an_error_occured_while_loading_data, it.message), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
