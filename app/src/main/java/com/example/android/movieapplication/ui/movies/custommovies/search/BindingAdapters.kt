package com.example.android.movieapplication.ui.movies.custommovies.search

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.ui.peopledetail.MovieListAdapter

@BindingAdapter("movies")
fun RecyclerView.bindMovies(movies: List<Movie>?) {
    val adapter = adapter as MovieListAdapter
    adapter.submitList(movies)
}