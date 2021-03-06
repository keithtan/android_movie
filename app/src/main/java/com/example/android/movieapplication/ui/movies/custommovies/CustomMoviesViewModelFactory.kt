package com.example.android.movieapplication.ui.movies.custommovies

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository

class CustomMoviesViewModelFactory(
    private val repository: MovieDbRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomMoviesViewModel::class.java)) {
            return CustomMoviesViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
