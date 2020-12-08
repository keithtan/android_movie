package com.example.android.movieapplication.ui.custommovies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow

class CustomMoviesViewModel(private val repository: MovieDbRepository) : ViewModel() {

    fun searchCustomMovies(): Flow<PagingData<Movie>> {
        return repository.getCustomMoviesStream()
            .cachedIn(viewModelScope)
    }

    private val _navigateToSelectedMovie = MutableLiveData<Long>()
    val navigateToSelectedMovie: LiveData<Long>
        get() = _navigateToSelectedMovie

    fun displayMovieDetails(movieId: Long) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieDetailsComplete() {
        _navigateToSelectedMovie.value = null
    }
}