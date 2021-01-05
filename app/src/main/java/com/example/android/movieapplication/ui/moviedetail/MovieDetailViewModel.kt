package com.example.android.movieapplication.ui.moviedetail

import android.app.Application
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.MovieDetail
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class MovieDetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    repository: MovieDbRepository,
    app: Application
) : AndroidViewModel(app) {

    fun saveMovieId(movieId: Long) {
        savedStateHandle.set(MOVIE_KEY, movieId)
    }

    val movieDetail: LiveData<MovieDetail?> = liveData {
        val movieId = savedStateHandle.get<Long>(MOVIE_KEY)
        repository.getMovieDetailsStream(movieId!!)
            .catch {
                emit(null)
            }
            .collect { value ->
                emit(value)
            }
    }

    val errorText = SpannableString("Details not found...\nCheck your internet connection")
        .apply {
            setSpan(
                RelativeSizeSpan(1.5f),
                0,
                17,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        app.applicationContext,
                        R.color.deep_orange_a200
                    )
                ),
                0,
                17,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    companion object {
        private const val MOVIE_KEY = "movieId"
    }

}