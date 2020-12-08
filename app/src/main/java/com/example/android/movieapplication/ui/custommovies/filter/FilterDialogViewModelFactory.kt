package com.example.android.movieapplication.ui.custommovies.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.db.FilterDao

class FilterDialogViewModelFactory(
    private val database: FilterDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterDialogViewModel::class.java)) {
            return FilterDialogViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
