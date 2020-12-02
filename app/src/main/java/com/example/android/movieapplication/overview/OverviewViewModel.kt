package com.example.android.movieapplication.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.model.Movie
import kotlinx.coroutines.flow.Flow

class OverviewViewModel(private val repository: MovieDbRepository) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

    private val _navigateToSelectedMovie = MutableLiveData<Long>()
    val navigateToSelectedMovie: LiveData<Long>
        get() = _navigateToSelectedMovie

    private var currentMovieResult: Flow<PagingData<Movie>>? = null


    fun searchMovies(): Flow<PagingData<Movie>> {
        return repository.getMovieResultStream()
            .cachedIn(viewModelScope)
    }

    fun displayMovieDetails(movieId: Long) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieDetailsComplete() {
        _navigateToSelectedMovie.value = null
    }

}