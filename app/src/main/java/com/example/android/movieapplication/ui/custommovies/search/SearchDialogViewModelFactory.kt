package com.example.android.movieapplication.ui.custommovies.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.FilterDao

class SearchDialogViewModelFactory(
    private val repository: MovieDbRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchDialogViewModel::class.java)) {
            return SearchDialogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
