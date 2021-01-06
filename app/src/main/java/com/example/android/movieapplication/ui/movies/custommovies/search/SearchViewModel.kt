package com.example.android.movieapplication.ui.movies.custommovies.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie

class SearchViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = MutableLiveData<String>()
    val query: LiveData<String>
        get() = _query

    fun setQuery(query: String) {
        _query.value = query
    }

    init {
        _query.value = savedStateHandle.get(QUERY_KEY)
    }


    val movies: LiveData<List<Movie>> = _query.switchMap {
        savedStateHandle.set(QUERY_KEY, it)
        liveData {
            it?.let {
                emit(repository.getSearchedMoviesStream(it).results)
            }
        }
    }

    companion object {
        private const val QUERY_KEY = "query"
    }

}