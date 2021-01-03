package com.example.android.movieapplication.ui.peopledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.movieapplication.data.MovieDbRepository

class PeopleDetailViewModelFactory(
    private val personId: Long,
    private val repository: MovieDbRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleDetailViewModel::class.java)) {
            return PeopleDetailViewModel(personId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}