package com.example.android.movieapplication.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.Genre
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

    suspend fun saveFilter(filter: Filter) {
        database.filterDao().insert(filter)
    }

    fun getFilter(): LiveData<Filter?> {
        return database.filterDao().liveFilter()
    }

    suspend fun getNetworkGenres(): List<Genre> {
        return service.getGenres().genres.map {
            Genre(
                it.id,
                it.name,
                false
            )
        }
    }

    fun getLiveDbGenres(): LiveData<List<Genre>> {
        println("result: " + database.genresDao().liveGenres().value)
        return database.genresDao().liveGenres()
    }

    suspend fun getDbGenres(): List<Genre> {
        return database.genresDao().genres()
    }

    suspend fun saveGenres(genres: List<Genre>) {
        database.genresDao().insertAll(genres)
    }

    suspend fun updateGenre(genre: Genre) {
        database.genresDao().updateGenre(genre)
    }

}

enum class MovieSection(val position: Int) {
    LATEST(0),
    COMINGSOON(1),
    CUSTOM(2)
}