package com.example.android.movieapplication.data

import androidx.paging.PagingSource
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.network.MoviesApiService
import retrofit2.HttpException
import java.io.IOException

private const val MOVIE_DB_STARTING_PAGE_INDEX = 1

class MoviePagingSource(
    private val service: MoviesApiService
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: MOVIE_DB_STARTING_PAGE_INDEX
        return try {
            val response = service.getMovies(position)
            val movies = response.results
            LoadResult.Page(
                data = movies,
                prevKey = if (position == MOVIE_DB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

//    @ExperimentalPagingApi
//    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
//        return state.anchorPosition
//    }
}