package com.example.android.movieapplication.ui.moviedetail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository

class MovieDetailViewModelFactory(
//    private val movieId: Long,
    private val savedStateHandle: SavedStateHandle,
    private val repository: MovieDbRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailViewModel::class.java)) {
            return MovieDetailViewModel(/*movieId,*/savedStateHandle, repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
