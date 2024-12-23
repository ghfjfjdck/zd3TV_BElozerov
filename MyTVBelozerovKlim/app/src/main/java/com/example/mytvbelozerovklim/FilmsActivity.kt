package com.example.mytvbelozerovklim

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.mytvbelozerovklim.data.FilmAdapter
import com.example.mytvbelozerovklim.data.FilmOmdb
import com.example.mytvbelozerovklim.data.FilmResponse
import com.example.mytvbelozerovklim.data.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmsActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FilmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        titleTextView = findViewById(R.id.films_title)
        titleTextView.text = "Список фильмов"

        recyclerView = findViewById(R.id.films_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3 колонки

        // Загрузка фильмов по умолчанию (например, боевиков)
        loadMoviesByGenre("action")

        // Настройка Bottom Navigation
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_movies -> {
                    loadMoviesByGenre("action")
                    true
                }
                R.id.horror_movies -> {
                    loadMoviesByGenre("horror")
                    true
                }
                R.id.comedy_movies -> {
                    loadMoviesByGenre("comedy")
                    true
                }

                else -> false
            }
        }
    }

    private fun loadMoviesByGenre(genre: String) {
        val api = RetrofitInstance.api
        api.searchFilms(genre, "movie").enqueue(object : Callback<FilmResponse> {
            override fun onResponse(call: Call<FilmResponse>, response: Response<FilmResponse>) {
                if (response.isSuccessful) {
                    val films = response.body()?.Search
                    if (!films.isNullOrEmpty()) {
                        adapter = FilmAdapter(films)
                        recyclerView.adapter = adapter
                        Log.d("MyLog", "Films loaded: ${films.size}")
                    } else {
                        Log.d("MyLog", "Films not found")
                    }
                } else {
                    Log.d("MyLog", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FilmResponse>, t: Throwable) {
                Log.e("MyLog", "Error: " + t.message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchFilms("movie", query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchFilms(type: String, title: String) {
        val api = RetrofitInstance.api
        api.searchFilms(title, type).enqueue(object : Callback<FilmResponse> {
            override fun onResponse(call: Call<FilmResponse>, response: Response<FilmResponse>) {
                if (response.isSuccessful) {
                    val films = response.body()?.Search
                    if (!films.isNullOrEmpty()) {
                        adapter = FilmAdapter(films)
                        recyclerView.adapter = adapter
                        Log.d("MyLog", "Films loaded: ${films.size}")
                    } else {
                        Log.d("MyLog", "Films not found")
                    }
                } else {
                    Log.d("MyLog", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FilmResponse>, t: Throwable) {
                Log.e("MyLog", "Error: " + t.message)
            }
        })
    }
}
