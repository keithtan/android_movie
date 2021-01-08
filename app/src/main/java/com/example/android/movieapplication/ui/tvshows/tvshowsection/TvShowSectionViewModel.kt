package com.example.android.movieapplication.ui.tvshows.tvshowsection

import android.app.Application
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class TvShowSectionViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository,
    application: Application
) : AndroidViewModel(application) {

    val filter = repository.tvShowFilterFlow

    fun searchMovies(tvShowSection: TvShowSection): Flow<PagingData<TvShow>> = filter.flatMapLatest {
        repository.getTvShowsStream(tvShowSection)
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