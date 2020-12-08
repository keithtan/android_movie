package com.example.android.movieapplication.ui.comingsoon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.ui.overview.OverviewViewModel

class ComingSoonViewModelFactory(
    private val repository: MovieDbRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComingSoonViewModel::class.java)) {
            return ComingSoonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}