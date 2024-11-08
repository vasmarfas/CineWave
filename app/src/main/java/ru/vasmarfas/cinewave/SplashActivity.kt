package ru.vasmarfas.cinewave

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vasmarfas.cinewave.data.db.UserDao
import ru.vasmarfas.cinewave.data.db.entity.FilmFiltersModel
import ru.vasmarfas.cinewave.data.db.entity.NewFilmFiltersModel
import ru.vasmarfas.cinewave.data.retrofit.KinopoiskAPI
import ru.vasmarfas.cinewave.databinding.ActivityMainBinding
import ru.vasmarfas.cinewave.databinding.ActivitySplashBinding
import ru.vasmarfas.cinewave.ui.adapters.SplashViewPagerAdapter
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var dots: Array<TextView?>
    private lateinit var adapter: SplashViewPagerAdapter
    @Inject
    lateinit var dao: UserDao
    private val images = listOf(R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.transparent_element)
    private val texts = listOf(
        "Узнавай о премьерах",
        "Создавай коллекции",
        "Делись с друзьями",
        "")
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // Если это первый запуск, показываем заставку
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)


            adapter = SplashViewPagerAdapter(this, images, texts)
            binding.elementsViewPager.adapter = adapter
            addDotsIndicator(0)

            binding.skipTextClickable.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            binding.elementsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                private var isLastPageSwiped = false
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    if (position == images.size - 1) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        addDotsIndicator(position)
                    }

                }

                override fun onPageScrollStateChanged(state: Int) {
//                if (isLastPageSwiped && state == ViewPager.SCROLL_STATE_IDLE) {
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    finish()
//                }
                }
            })

            // Обновляем SharedPreferences, чтобы указать, что приложение запускалось
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
        } else {
            // Если это не первый запуск, сразу переходим на MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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

    private fun addDotsIndicator(position: Int) {
        dots = arrayOfNulls(images.size - 1)
        binding.dotsLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.gray_600))
            binding.dotsLayout.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            dots[position]?.setTextColor(resources.getColor(R.color.rounded_border_color))
        }
    }
}
//    private val SPLASH_TIME_OUT: Long = 3000 // 3 секунды
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_splash)
//
//        Handler().postDelayed({
//            // Переход на MainActivity
//            startActivity(Intent(this, MainActivity::class.java))
//            // Закрыть SplashActivity
//            finish()
//        }, SPLASH_TIME_OUT)
//    }
//}