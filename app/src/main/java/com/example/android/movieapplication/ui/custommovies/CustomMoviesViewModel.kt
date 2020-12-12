package com.example.android.movieapplication.ui.custommovies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow

class CustomMoviesViewModel(private val repository: MovieDbRepository) : ViewModel() {

    fun searchCustomMovies(): Flow<PagingData<Movie>> {
        return repository.getCustomMoviesStream()
            .cachedIn(viewModelScope)
    }

    val filter = repository.getFilter()

    val genres = repository.getLiveDbGenres()

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