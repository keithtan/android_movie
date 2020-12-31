package com.example.android.movieapplication.data

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.UserPreferences
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.network.MovieData
import com.example.android.movieapplication.network.MovieDetail
import com.example.android.movieapplication.network.MoviesApiService
import com.example.android.movieapplication.network.PeopleDetail
import com.example.android.movieapplication.ui.custommovies.filter.UserPreferencesSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException

class MovieDbRepository private constructor(
    context: Context,
    private val service: MoviesApiService,
    private val database: MovieDatabase
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20

        @Volatile
        private var INSTANCE: MovieDbRepository? = null

        fun getInstance(
            context: Context,
            service: MoviesApiService,
            database: MovieDatabase
        ): MovieDbRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = MovieDbRepository(context, service, database)
                INSTANCE = instance
                instance
            }
        }
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

    suspend fun getSearchedMoviesStream(query: String): MovieData {
        return service.getSearchedMovies(query)
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
                section,
                userPreferencesFlow
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getMovieDetailsStream(movieId: Long): Flow<MovieDetail> = flow {
        emit(service.getMovieDetails(movieId))
    }


    private val dataStore: DataStore<UserPreferences> =
        context.createDataStore(
            fileName = "user_prefs.pb",
            serializer = UserPreferencesSerializer
        )

    private val TAG: String = "UserPreferencesRepo"

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateFilter(startYear: Int, endYear: Int, voteAverage: Float) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setStartYear(startYear)
                .setEndYear(endYear)
                .setVoteAverage(voteAverage)
                .build()
        }
    }

    suspend fun getNetworkGenres(): List<Genre> {
        return service.getGenres().genres.map {
            Genre(
                it.id,
                it.name,
                included = false,
                excluded = false
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

    suspend fun getPeopleDetails(personId: Long): Flow<PeopleDetail> = flow {
        emit(service.getPeopleDetails(personId))
    }

}

enum class MovieSection(val position: Int) {
    LATEST(0),
    COMINGSOON(1),
    CUSTOM(2)
}