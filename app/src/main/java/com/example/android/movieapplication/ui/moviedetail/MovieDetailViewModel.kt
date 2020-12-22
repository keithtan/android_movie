package com.example.android.movieapplication.ui.moviedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.MovieCredits.MovieCast
import com.example.android.movieapplication.network.MovieDetail
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailViewModel(
    movieId: Long,
    repository: MovieDbRepository,
    app: Application
) : AndroidViewModel(app) {

    val movieDetail: LiveData<MovieDetail?> = liveData {
        repository.getMovieDetailsStream(movieId)
            .catch { _ ->
                emit(null)
            }
            .collect { value ->
                emit(value)
            }
    }

    val movieCast: LiveData<List<MovieCast>> = liveData {
        repository.getMovieCastStream(movieId)
            .catch { _ ->
                emit(emptyList())
            }
            .collect { value ->
                emit(value)
            }
    }

    val release = movieDetail.map {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .parse(it?.releaseDate ?: "")
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        parser?.let { parser1 ->
            formatter.format(parser1)
        }

    }

    val ratingStars = movieDetail.map {
        it?.voteAverage?.div(2)?.toFloat()
    }

    val runtime = movieDetail.map {
        val hours = it?.runtime?.div(60)
        val minutes = it?.runtime?.rem(60)
        String.format("%dh %02dm", hours, minutes)
    }

}