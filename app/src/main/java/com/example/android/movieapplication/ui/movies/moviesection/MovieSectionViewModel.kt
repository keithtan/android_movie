package com.example.android.movieapplication.ui.movies.moviesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow

class MovieSectionViewModel(
    private val repository: MovieDbRepository,
    private val section: MovieSection
) : ViewModel() {

    fun searchMovies(): Flow<PagingData<Movie>> {
        return repository.getMoviesStream(section)
            .cachedIn(viewModelScope)
    }

}