package com.example.android.movieapplication.ui.custommovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.ui.comingsoon.ComingSoonViewModel

class CustomMoviesViewModelFactory(
    private val repository: MovieDbRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomMoviesViewModel::class.java)) {
            return CustomMoviesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
