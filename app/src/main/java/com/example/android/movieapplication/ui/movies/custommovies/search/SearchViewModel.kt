package com.example.android.movieapplication.ui.movies.custommovies.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository
) : ViewModel() {

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _movies.value = repository.getSearchedMoviesStream(query).results
        }
    }

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

}