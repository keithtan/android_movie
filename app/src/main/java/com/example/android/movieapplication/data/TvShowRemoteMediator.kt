package com.example.android.movieapplication.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android.movieapplication.UserPreferences
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.db.TvShow
import com.example.android.movieapplication.db.TvShowRemoteKeys
import com.example.android.movieapplication.network.MoviesApiService
import com.example.android.movieapplication.ui.tvshows.TvShowSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

private const val MOVIE_DB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class TvShowRemoteMediator(
    private val service: MoviesApiService,
    private val movieDatabase: MovieDatabase,
    private val position: TvShowSection,
    private val userPreferencesFlow: Flow<UserPreferences>
) : RemoteMediator<Int, TvShow>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, TvShow>): MediatorResult {
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
            val tvShows = getTvShowsFromNetwork(page)
            val endOfPaginationReached = tvShows.isEmpty()
            movieDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    movieDatabase.tvShowRemoteKeysDao().clearRemoteKeys()
                    movieDatabase.tvShowsDao().clearTvShows(position.ordinal)
                }
                val prevKey = if (page == MOVIE_DB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = tvShows.map {
                    TvShowRemoteKeys(tvShowId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                movieDatabase.tvShowRemoteKeysDao().insertAll(keys)
                tvShows.map {
                    it.position = position.ordinal
                }
                movieDatabase.tvShowsDao().insertAll(tvShows)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getTvShowsFromNetwork(page: Int): List<TvShow> {
        val filter = userPreferencesFlow.first().toNetworkModel()
        val apiResponse =
            when (position) {
                TvShowSection.LATEST -> service.getLatestTvShows(page)
                TvShowSection.COMINGSOON -> service.getComingSoonTvShows(page)
                TvShowSection.CUSTOM -> service.getCustomTvShows(
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
        state: PagingState<Int, TvShow>
    ): TvShowRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { tvShowId ->
                movieDatabase.tvShowRemoteKeysDao().remoteKeysTvShowId(tvShowId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TvShow>): TvShowRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { tvShow ->
                // Get the remote keys of the first items retrieved
                movieDatabase.tvShowRemoteKeysDao().remoteKeysTvShowId(tvShow.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TvShow>): TvShowRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { tvShow ->
                // Get the remote keys of the last item retrieved
                movieDatabase.tvShowRemoteKeysDao().remoteKeysTvShowId(tvShow.id)
            }
    }

}