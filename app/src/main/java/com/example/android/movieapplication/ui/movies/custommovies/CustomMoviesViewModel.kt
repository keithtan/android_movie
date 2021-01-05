package com.example.android.movieapplication.ui.movies.custommovies

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
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import kotlinx.coroutines.flow.Flow

class CustomMoviesViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository,
    application: Application
) : AndroidViewModel(application) {

    fun searchCustomMovies(): Flow<PagingData<Movie>> {
        return repository.getMoviesStream(MovieSection.CUSTOM)
            .cachedIn(viewModelScope)
    }

    val filter = repository.userPreferencesFlow.asLiveData()

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

}