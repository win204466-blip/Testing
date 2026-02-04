package com.anitracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anitracker.app.R
import com.anitracker.app.adapter.GridAdapter
import com.anitracker.app.api.ApiClient
import com.anitracker.app.api.GraphQLRequest
import com.anitracker.app.api.Queries
import com.anitracker.app.model.Media
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: View
    private lateinit var chipAnime: Chip
    private lateinit var chipManga: Chip

    private lateinit var gridAdapter: GridAdapter
    private var searchJob: Job? = null
    private var currentType = "ANIME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_search)

        setupViews()
    }

    private fun setupViews() {
        etSearch = findViewById(R.id.etSearch)
        rvResults = findViewById(R.id.rvResults)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        chipAnime = findViewById(R.id.chipAnime)
        chipManga = findViewById(R.id.chipManga)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val onItemClick: (Media) -> Unit = { media ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("media_id", media.id)
            startActivity(intent)
        }

        gridAdapter = GridAdapter(onItemClick)
        rvResults.layoutManager = GridLayoutManager(this, 3)
        rvResults.adapter = gridAdapter

        chipAnime.setOnClickListener {
            currentType = "ANIME"
            chipAnime.isChecked = true
            chipManga.isChecked = false
            performSearch(etSearch.text.toString())
        }

        chipManga.setOnClickListener {
            currentType = "MANGA"
            chipManga.isChecked = true
            chipAnime.isChecked = false
            performSearch(etSearch.text.toString())
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    performSearch(s.toString())
                }
            }
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchJob?.cancel()
                performSearch(etSearch.text.toString())
                true
            } else {
                false
            }
        }

        etSearch.requestFocus()
    }

    private fun performSearch(query: String) {
        if (query.length < 2) {
            gridAdapter.submitList(emptyList())
            emptyState.visibility = View.GONE
            return
        }

        progressBar.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        rvResults.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.api.query(
                    GraphQLRequest(
                        Queries.SEARCH_MEDIA,
                        mapOf("search" to query, "type" to currentType)
                    )
                )

                val results = response.data.Page?.media ?: emptyList()
                gridAdapter.submitList(results)

                progressBar.visibility = View.GONE
                
                if (results.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    rvResults.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    rvResults.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@SearchActivity,
                    getString(R.string.error_loading),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
