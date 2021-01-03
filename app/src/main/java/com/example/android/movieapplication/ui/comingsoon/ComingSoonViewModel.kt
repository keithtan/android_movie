package com.example.android.movieapplication.ui.comingsoon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow

class ComingSoonViewModel(private val repository: MovieDbRepository) : ViewModel() {

    fun searchComingSoonMovies(): Flow<PagingData<Movie>> {
        return repository.getComingSoonMoviesStream()
            .cachedIn(viewModelScope)
    }

}