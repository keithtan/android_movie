package com.example.android.movieapplication.ui.moviedetail

import android.app.Application
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.MovieDetail
import com.example.android.movieapplication.network.MovieDetail.MovieVideos.VideoDetail
import com.example.android.movieapplication.network.MovieDetail.MovieCredits.MovieCast
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
            .catch {
                emit(null)
            }
            .collect { value ->
                emit(value)
            }
    }

    val movieCast: LiveData<List<MovieCast>?> = movieDetail.map {
        it?.credits?.cast
    }

    val movieVideos: LiveData<List<VideoDetail>?> = movieDetail.map {
        it?.videos?.results
    }

    val release = movieDetail.map {
        it?.releaseDate?.let {releaseDate ->
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(releaseDate)
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            parser?.let { parser1 ->
                formatter.format(parser1)
            }
        } ?: ""

    }

    val ratingStars = movieDetail.map {
        it?.voteAverage?.div(2)?.toFloat()
    }

    val runtime = movieDetail.map {
        val hours = it?.runtime?.div(60)
        val minutes = it?.runtime?.rem(60)
        String.format("%dh %02dm", hours, minutes)
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

}