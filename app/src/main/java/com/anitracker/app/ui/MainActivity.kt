package com.anitracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anitracker.app.R
import com.anitracker.app.adapter.AnimeAdapter
import com.anitracker.app.api.ApiClient
import com.anitracker.app.api.GraphQLRequest
import com.anitracker.app.api.Queries
import com.anitracker.app.model.Media
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var loadingOverlay: View
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var trendingAdapter: AnimeAdapter
    private lateinit var popularAdapter: AnimeAdapter
    private lateinit var upcomingAdapter: AnimeAdapter
    private lateinit var topRatedAdapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupViews()
        setupAdapters()
        loadData()
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = 
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupViews() {
        loadingOverlay = findViewById(R.id.loadingOverlay)
        bottomNav = findViewById(R.id.bottomNav)

        findViewById<View>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_anime -> true
                R.id.nav_manga -> true
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun setupAdapters() {
        val onItemClick: (Media) -> Unit = { media ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("media_id", media.id)
            startActivity(intent)
        }

        trendingAdapter = AnimeAdapter(onItemClick)
        popularAdapter = AnimeAdapter(onItemClick)
        upcomingAdapter = AnimeAdapter(onItemClick)
        topRatedAdapter = AnimeAdapter(onItemClick)

        setupSection(R.id.sectionTrending, getString(R.string.section_trending), trendingAdapter)
        setupSection(R.id.sectionPopular, getString(R.string.section_popular), popularAdapter)
        setupSection(R.id.sectionUpcoming, getString(R.string.section_upcoming), upcomingAdapter)
        setupSection(R.id.sectionTopRated, getString(R.string.section_top_rated), topRatedAdapter)
    }

    private fun setupSection(sectionId: Int, title: String, adapter: AnimeAdapter) {
        val section = findViewById<View>(sectionId)
        section.findViewById<TextView>(R.id.tvSectionTitle).text = title
        
        val recyclerView = section.findViewById<RecyclerView>(R.id.rvItems)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        loadingOverlay.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val trendingResponse = ApiClient.api.query(
                    GraphQLRequest(Queries.TRENDING_ANIME)
                )
                trendingAdapter.submitList(trendingResponse.data.Page?.media)

                val calendar = Calendar.getInstance()
                val month = calendar.get(Calendar.MONTH)
                val season = when (month) {
                    in 0..2 -> "WINTER"
                    in 3..5 -> "SPRING"
                    in 6..8 -> "SUMMER"
                    else -> "FALL"
                }
                val year = calendar.get(Calendar.YEAR)

                val popularResponse = ApiClient.api.query(
                    GraphQLRequest(
                        Queries.POPULAR_THIS_SEASON,
                        mapOf("season" to season, "year" to year)
                    )
                )
                popularAdapter.submitList(popularResponse.data.Page?.media)

                val upcomingResponse = ApiClient.api.query(
                    GraphQLRequest(Queries.UPCOMING_ANIME)
                )
                upcomingAdapter.submitList(upcomingResponse.data.Page?.media)

                val topRatedResponse = ApiClient.api.query(
                    GraphQLRequest(Queries.TOP_RATED)
                )
                topRatedAdapter.submitList(topRatedResponse.data.Page?.media)

                loadingOverlay.visibility = View.GONE

            } catch (e: Exception) {
                loadingOverlay.visibility = View.GONE
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.error_loading),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
