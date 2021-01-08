package com.example.android.movieapplication.ui.tvshows.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.db.TvShow

class TvShowSearchViewModel @ViewModelInject constructor(
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


    val tvShows: LiveData<List<TvShow>> = _query.switchMap {
        savedStateHandle.set(QUERY_KEY, it)
        liveData {
            it?.let {
                emit(repository.getSearchedTvShowsStream(it).results)
            }
        }
    }

    companion object {
        private const val QUERY_KEY = "query"
    }

}