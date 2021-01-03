package com.example.android.movieapplication.ui.movies.moviesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository

class MovieSectionViewModelFactory(
    private val repository: MovieDbRepository,
    private val section: MovieSection
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieSectionViewModel::class.java)) {
            return MovieSectionViewModel(repository, section) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}