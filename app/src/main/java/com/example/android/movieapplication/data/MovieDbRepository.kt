package com.example.android.movieapplication.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.GenrePreferences
import com.example.android.movieapplication.MovieFilterPreferences
import com.example.android.movieapplication.TvShowFilterPreferences
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.*
import com.example.android.movieapplication.ui.movies.filter.GenreModel
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDbRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val service: MoviesApiService,
    private val database: MovieDatabase
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    fun getMoviesStream(section: MovieSection): Flow<PagingData<Movie>> {
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
                section,
                movieFilterFlow,
                tvShowFilterFlow
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun getSearchedMoviesStream(query: String): MovieDto {
        return service.getSearchedMovies(query)
    }

    fun getMovieDetailsStream(movieId: Long): Flow<MovieDetail> = flow {
        emit(service.getMovieDetails(movieId))
    }

    private val movieFilterDataStore: DataStore<MovieFilterPreferences> =
        context.createDataStore(
            fileName = "user_prefs.pb",
            serializer = MovieFilterPreferencesSerializer
        )

    val movieFilterFlow: Flow<MovieFilterPreferences> = movieFilterDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Timber.e(exception, "Error reading movie preferences.")
                emit(MovieFilterPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateMovieFilter(startYear: Int, endYear: Int, voteAverage: Float, genres: List<GenreModel>) {
        val genrePrefs = genres.toDataStoreModel()
        movieFilterDataStore.updateData { preferences ->
            preferences.toBuilder()
                .setStartYear(startYear)
                .setEndYear(endYear)
                .setVoteAverage(voteAverage)
                .clearGenrePref()
                .addAllGenrePref(genrePrefs)
                .build()
        }
    }

    suspend fun getNetworkMovieGenres(): List<GenreModel> {
        return service.getMovieGenres().movieGenres.map {
            GenreModel(
                it.id,
                it.name,
                included = false,
                excluded = false
            )
        }
    }

    suspend fun getMovieCastDetails(castId: Long): Flow<MovieCastDetail> = flow {
        emit(service.getMovieCastDetails(castId))
    }


    suspend fun getSearchedTvShowsStream(query: String): MovieDto {
        return service.getSearchedTvShows(query)
    }

    fun getTvShowDetailsStream(tvShowId: Long): Flow<MovieDetail> = flow {
        emit(service.getTvShowDetails(tvShowId))
    }

    private val tvShowFilterDataStore: DataStore<TvShowFilterPreferences> =
        context.createDataStore(
            fileName = "user_prefs.pb",
            serializer = TvShowFilterPreferencesSerializer
        )

    val tvShowFilterFlow: Flow<TvShowFilterPreferences> = tvShowFilterDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Timber.e(exception, "Error reading tv show preferences.")
                emit(TvShowFilterPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateTvShowFilter(startYear: Int, endYear: Int, voteAverage: Float, genres: List<GenreModel>) {
        val genrePrefs = genres.toDataStoreModel()
        tvShowFilterDataStore.updateData { preferences ->
            preferences.toBuilder()
                .setStartYear(startYear)
                .setEndYear(endYear)
                .setVoteAverage(voteAverage)
                .clearGenrePref()
                .addAllGenrePref(genrePrefs)
                .build()
        }
    }

    suspend fun getNetworkTvShowGenres(): List<GenreModel> {
        return service.getTvShowGenres().tvShowGenres.map {
            GenreModel(
                it.id,
                it.name,
                included = false,
                excluded = false
            )
        }
    }

    suspend fun getTvShowCastDetails(castId: Long): Flow<MovieCastDetail> = flow {
        emit(service.getTvShowCastDetails(castId))
    }


    private fun List<GenreModel>.toDataStoreModel(): List<GenrePreferences> {
        return this.map {
            GenrePreferences.newBuilder()
                .setId(it.id)
                .setName(it.name)
                .setIncluded(it.included)
                .setExcluded(it.excluded)
                .build()
        }
    }

}