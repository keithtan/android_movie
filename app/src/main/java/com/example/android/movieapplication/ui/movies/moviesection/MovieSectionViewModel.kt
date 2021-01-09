package com.example.android.movieapplication.ui.movies.moviesection

import android.app.Application
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class MovieSectionViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository,
    application: Application
) : AndroidViewModel(application) {

    private val movieFilter = repository.movieFilterFlow
    private val tvShowFilter = repository.tvShowFilterFlow

    fun searchMovies(section: Section): Flow<PagingData<Movie>> =
        combine(movieFilter, tvShowFilter) { t, _ ->
            t
        }
            .flatMapLatest {
                repository.getMoviesStream(section)
                    .cachedIn(viewModelScope)
    }

    val emptyText = SpannableString("No movies found..\nTry a different filter")
        .apply {
            setSpan(
                RelativeSizeSpan(1.5f),
                0,
                15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        application.applicationContext,
                        R.color.deep_orange_a200
                    )
                ),
                0,
                15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    val loadingState = MutableLiveData<Boolean>()
    val retryState = MutableLiveData<Boolean>()
    val emptyState = MutableLiveData<Boolean>()

}