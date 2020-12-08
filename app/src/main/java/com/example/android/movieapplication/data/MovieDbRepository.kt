package com.example.android.movieapplication.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.model.MovieDetail
import com.example.android.movieapplication.network.MoviesApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieDbRepository(
    private val service: MoviesApiService,
    private val database: MovieDatabase
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    fun getLatestMoviesStream(): Flow<PagingData<Movie>> {
        return getMoviesStream(MovieSection.LATEST)
    }

    fun getComingSoonMoviesStream(): Flow<PagingData<Movie>> {
        return getMoviesStream(MovieSection.COMINGSOON)
    }

    fun getCustomMoviesStream(): Flow<PagingData<Movie>> {
        return getMoviesStream(MovieSection.CUSTOM)
    }

    private fun getMoviesStream(section: MovieSection): Flow<PagingData<Movie>> {
        val pagingSourceFactory =  { database.moviesDao().movies(section.ordinal) }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = 20
            ),
            remoteMediator = MovieRemoteMediator(
                service,
                database,
                section
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getMovieDetailsStream(movieId: Long): Flow<MovieDetail> = flow {
        emit(service.getMovieDetails(movieId))
    }

}

enum class MovieSection(val position: Int) {
    LATEST(0),
    COMINGSOON(1),
    CUSTOM(2)
}