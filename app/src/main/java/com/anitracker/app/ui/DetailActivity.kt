package com.anitracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anitracker.app.R
import com.anitracker.app.adapter.AnimeAdapter
import com.anitracker.app.api.ApiClient
import com.anitracker.app.api.GraphQLRequest
import com.anitracker.app.api.Queries
import com.anitracker.app.model.Media
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private var mediaId: Int = 0

    private lateinit var ivBanner: ImageView
    private lateinit var ivCover: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvStudio: TextView
    private lateinit var tvSynopsis: TextView
    private lateinit var chipGroupGenres: ChipGroup
    private lateinit var btnAddToList: MaterialButton

    private lateinit var recommendationsAdapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_detail)

        mediaId = intent.getIntExtra("media_id", 0)
        if (mediaId == 0) {
            finish()
            return
        }

        setupViews()
        loadMediaDetail()
    }

    private fun setupViews() {
        ivBanner = findViewById(R.id.ivBanner)
        ivCover = findViewById(R.id.ivCover)
        tvTitle = findViewById(R.id.tvTitle)
        tvStudio = findViewById(R.id.tvStudio)
        tvSynopsis = findViewById(R.id.tvSynopsis)
        chipGroupGenres = findViewById(R.id.chipGroupGenres)
        btnAddToList = findViewById(R.id.btnAddToList)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnAddToList.setOnClickListener {
            Toast.makeText(this, "Added to list!", Toast.LENGTH_SHORT).show()
        }

        val onItemClick: (Media) -> Unit = { media ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("media_id", media.id)
            startActivity(intent)
        }

        recommendationsAdapter = AnimeAdapter(onItemClick)
        val sectionRecommendations = findViewById<View>(R.id.sectionRecommendations)
        sectionRecommendations.findViewById<TextView>(R.id.tvSectionTitle).text = 
            getString(R.string.label_recommendations)
        val rvRecommendations = sectionRecommendations.findViewById<RecyclerView>(R.id.rvItems)
        rvRecommendations.layoutManager = 
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvRecommendations.adapter = recommendationsAdapter
    }

    private fun loadMediaDetail() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.api.query(
                    GraphQLRequest(
                        Queries.MEDIA_DETAIL,
                        mapOf("id" to mediaId)
                    )
                )

                response.data.Media?.let { media ->
                    bindMedia(media)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailActivity,
                    getString(R.string.error_loading),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindMedia(media: Media) {
        tvTitle.text = media.title.preferred()
        
        media.studios?.nodes?.firstOrNull()?.let { studio ->
            tvStudio.text = studio.name
            tvStudio.visibility = View.VISIBLE
        }

        media.bannerImage?.let { banner ->
            Glide.with(this).load(banner).centerCrop().into(ivBanner)
        } ?: run {
            Glide.with(this).load(media.coverImage.best()).centerCrop().into(ivBanner)
        }

        Glide.with(this).load(media.coverImage.best()).centerCrop().into(ivCover)

        media.description?.let { desc ->
            tvSynopsis.text = Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT)
        }

        setupStats(media)
        setupGenres(media.genres)
        setupRecommendations(media)
    }

    private fun setupStats(media: Media) {
        val statScore = findViewById<View>(R.id.statScore)
        statScore.findViewById<TextView>(R.id.tvStatValue).text = 
            media.averageScore?.let { String.format("%.1f", it / 10.0) } ?: "-"
        statScore.findViewById<TextView>(R.id.tvStatLabel).text = getString(R.string.label_score)

        val statEpisodes = findViewById<View>(R.id.statEpisodes)
        val episodeLabel = if (media.type == "MANGA") R.string.label_chapters else R.string.label_episodes
        val episodeValue = if (media.type == "MANGA") media.chapters else media.episodes
        statEpisodes.findViewById<TextView>(R.id.tvStatValue).text = episodeValue?.toString() ?: "-"
        statEpisodes.findViewById<TextView>(R.id.tvStatLabel).text = getString(episodeLabel)

        val statStatus = findViewById<View>(R.id.statStatus)
        statStatus.findViewById<TextView>(R.id.tvStatValue).text = 
            media.status?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "-"
        statStatus.findViewById<TextView>(R.id.tvStatLabel).text = getString(R.string.label_status)

        val statFormat = findViewById<View>(R.id.statFormat)
        statFormat.findViewById<TextView>(R.id.tvStatValue).text = 
            media.format?.replace("_", " ") ?: "-"
        statFormat.findViewById<TextView>(R.id.tvStatLabel).text = getString(R.string.label_format)
    }

    private fun setupGenres(genres: List<String>?) {
        chipGroupGenres.removeAllViews()
        genres?.forEach { genre ->
            val chip = Chip(this).apply {
                text = genre
                isClickable = false
                setChipBackgroundColorResource(R.color.background_elevated)
                setTextColor(getColor(R.color.text_secondary))
                textSize = 12f
            }
            chipGroupGenres.addView(chip)
        }
    }

    private fun setupRecommendations(media: Media) {
        val recommendations = media.recommendations?.nodes
            ?.mapNotNull { it.mediaRecommendation }
            ?: emptyList()

        if (recommendations.isNotEmpty()) {
            recommendationsAdapter.submitList(recommendations)
            findViewById<View>(R.id.sectionRecommendations).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.sectionRecommendations).visibility = View.GONE
        }

        findViewById<View>(R.id.sectionCharacters).visibility = View.GONE
    }
}
