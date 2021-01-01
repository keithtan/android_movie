package com.example.android.movieapplication.ui.custommovies.filter

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository

class FilterDialogViewModelFactory(
    private val repository: MovieDbRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterDialogViewModel::class.java)) {
            return FilterDialogViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
