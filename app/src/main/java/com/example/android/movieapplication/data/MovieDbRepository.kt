package com.example.android.movieapplication.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.model.Movie
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

    fun getMovieResultStream(): Flow<PagingData<Movie>> {
        val pagingSourceFactory =  { database.moviesDao().movies() }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = 80
            ),
            remoteMediator = MovieRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
//            pagingSourceFactory = { MoviePagingSource(service) }
        ).flow
    }

    fun getMovieDetailsStream(movieId: Long): Flow<MovieDetail> = flow {
        emit(service.getMovieDetails(movieId))
    }

}