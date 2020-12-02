package com.example.android.movieapplication.moviedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.model.MovieDetail
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class MovieDetailViewModel(
    movieId: Long,
    repository: MovieDbRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _movieDetail = MutableLiveData<MovieDetail>()
    val movieDetail: LiveData<MovieDetail?> = liveData {
        repository.getMovieDetailsStream(movieId)
            .catch { _ ->
                emit(null)
            }
            .collect { value ->
                println(value)
                emit(value)
            }
    }

}