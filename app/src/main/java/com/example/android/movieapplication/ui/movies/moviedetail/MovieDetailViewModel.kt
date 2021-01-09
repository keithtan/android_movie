package com.example.android.movieapplication.ui.movies.moviedetail

import android.app.Application
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.MovieDetail
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class MovieDetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    repository: MovieDbRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _section = MutableLiveData<MovieSection>()
    fun setSection(section: MovieSection) {
        println("SECTION: " + section)
        _section.value = section
    }

    fun saveMovieId(movieId: Long) {
        savedStateHandle.set(MOVIE_KEY, movieId)
    }

    val movieDetail: LiveData<MovieDetail?> = savedStateHandle.getLiveData<Long>(MOVIE_KEY)
        .switchMap {
            liveData {
                when (_section.value) {
                    MovieSection.MOVIE_LATEST,
                    MovieSection.MOVIE_COMING_SOON,
                    MovieSection.MOVIE_CUSTOM ->
                        repository.getMovieDetailsStream(it!!)
                            .catch {
                                emit(null)
                            }
                            .collect { value ->
                                emit(value)
                            }
                    MovieSection.TV_SHOW_LATEST,
                    MovieSection.TV_SHOW_COMING_SOON,
                    MovieSection.TV_SHOW_CUSTOM ->
                        repository.getTvShowDetailsStream(it!!)
                            .catch {e ->
                                println(e)
                                emit(null)
                            }
                            .collect { value ->
                                emit(value)
                            }
                }
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