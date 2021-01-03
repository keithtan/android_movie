package com.example.android.movieapplication.ui.custommovies.search

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: MovieDbRepository) : ViewModel() {

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _movies.value = repository.getSearchedMoviesStream(query).results
        }
    }

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

}