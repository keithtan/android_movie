package com.example.android.movieapplication.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android.movieapplication.MovieFilterPreferences
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.db.MovieRemoteKeys
import com.example.android.movieapplication.network.MoviesApiService
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

private const val MOVIE_DB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val service: MoviesApiService,
    private val movieDatabase: MovieDatabase,
    private val position: MovieSection,
    private val movieFilterPreferencesFlow: Flow<MovieFilterPreferences>
) : RemoteMediator<Int, Movie>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: MOVIE_DB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If the previous key is null, then we can't request more data
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: 1
            }
        }

        try {
            val movies = getMoviesFromNetwork(page)
            val endOfPaginationReached = movies.isEmpty()
            movieDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    movieDatabase.movieRemoteKeysDao().clearRemoteKeys()
                    movieDatabase.moviesDao().clearMovies(position.ordinal)
                }
                val prevKey = if (page == MOVIE_DB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = movies.map {
                    MovieRemoteKeys(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                movieDatabase.movieRemoteKeysDao().insertAll(keys)
                movies.map {
                    it.position = position.ordinal
                }
                movieDatabase.moviesDao().insertAll(movies)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getMoviesFromNetwork(page: Int): List<Movie> {
        val filter = movieFilterPreferencesFlow.first().toNetworkModel()
        val apiResponse =
            when (position) {
                MovieSection.LATEST -> service.getLatestMovies(page)
                MovieSection.COMINGSOON -> service.getComingSoonMovies(page)
                MovieSection.CUSTOM -> service.getCustomMovies(
                    page,
                    filter.startDate,
                    filter.endDate,
                    filter.voteAverage,
                    filter.includedGenres,
                    filter.excludedGenres
                )
            }

        return apiResponse.results
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Movie>
    ): MovieRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movieId ->
                movieDatabase.movieRemoteKeysDao().remoteKeysMovieId(movieId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): MovieRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // Get the remote keys of the first items retrieved
                movieDatabase.movieRemoteKeysDao().remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): MovieRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                movieDatabase.movieRemoteKeysDao().remoteKeysMovieId(movie.id)
            }
    }

}