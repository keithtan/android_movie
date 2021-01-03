package com.example.android.movieapplication.data

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.movieapplication.GenrePreferences
import com.example.android.movieapplication.UserPreferences
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MovieData
import com.example.android.movieapplication.network.MovieDetail
import com.example.android.movieapplication.network.MoviesApiService
import com.example.android.movieapplication.network.PeopleDetail
import com.example.android.movieapplication.ui.custommovies.filter.GenreModel
import com.example.android.movieapplication.ui.custommovies.filter.UserPreferencesSerializer
import kotlinx.coroutines.flow.*
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
                Log.e(TAG, "Error reading user preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateFilter(startYear: Int, endYear: Int, voteAverage: Float, genres: List<GenreModel>) {
        val genrePrefs = genres.toDataStoreModel()
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setStartYear(startYear)
                .setEndYear(endYear)
                .setVoteAverage(voteAverage)
                .clearGenrePref()
                .addAllGenrePref(genrePrefs)
                .build()
        }
    }

    suspend fun getNetworkGenres(): List<GenreModel> {
        return service.getGenres().genres.map {
            GenreModel(
                it.id,
                it.name,
                included = false,
                excluded = false
            )
        }
    }

    suspend fun getPeopleDetails(personId: Long): Flow<PeopleDetail> = flow {
        emit(service.getPeopleDetails(personId))
    }

}

fun List<GenreModel>.toDataStoreModel(): List<GenrePreferences> {
    return this.map {
        GenrePreferences.newBuilder()
            .setId(it.id)
            .setName(it.name)
            .setIncluded(it.included)
            .setExcluded(it.excluded)
            .build()
    }
}

fun List<GenrePreferences>.toDomainModel(): List<GenreModel> {
    return this.map {
        GenreModel(
            it.id,
            it.name,
            it.included,
            it.excluded
        )
    }
}

enum class MovieSection(val position: Int) {
    LATEST(0),
    COMINGSOON(1),
    CUSTOM(2)
}