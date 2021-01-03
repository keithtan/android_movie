package com.example.android.movieapplication.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow

class OverviewViewModel(private val repository: MovieDbRepository) : ViewModel() {

    fun searchMovies(): Flow<PagingData<Movie>> {
        return repository.getLatestMoviesStream()
            .cachedIn(viewModelScope)
    }

}