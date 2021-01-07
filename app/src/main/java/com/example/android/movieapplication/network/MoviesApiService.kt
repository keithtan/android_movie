package com.example.android.movieapplication.network

import com.example.android.movieapplication.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.InstallIn
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val currentDate: String
    get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

interface MoviesApiService {
    @GET("discover/movie?sort_by=release_date.desc&vote_average.gte=5")
    suspend fun getLatestMovies(
        @Query("page") page: Int,
        @Query("release_date.lte") releaseDate: String = currentDate
    ): MovieDto

    @GET("discover/movie?sort_by=release_date.asc")
    suspend fun getComingSoonMovies(
        @Query("page") page: Int,
        @Query("release_date.gte") releaseDate: String = currentDate
    ): MovieDto

    @GET("discover/movie")
    suspend fun getCustomMovies(
        @Query("page") page: Int,
        @Query("release_date.gte") releaseDateGte: String?,
        @Query("release_date.lte") releaseDateLte: String?,
        @Query("vote_average.gte") voteAverage: Float?,
        @Query("with_genres") genreWithFilter: String?,
        @Query("without_genres") genreWithoutFilter: String?
    ): MovieDto

    @GET("search/movie")
    suspend fun getSearchedMovies(
        @Query("query") query: String
    ): MovieDto

    @GET("movie/{movieId}?append_to_response=credits,videos")
    suspend fun getMovieDetails(@Path("movieId") movieId: Long): MovieDetail

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresDto

    @GET("person/{personId}?append_to_response=movie_credits")
    suspend fun getPeopleDetails(@Path("personId") personId: Long): PeopleDetail


    @GET("discover/tv?sort_by=first_air_date.desc&vote_average.gte=5")
    suspend fun getLatestTvShows(
        @Query("page") page: Int,
        @Query("first_air_date.lte") firstAirDate: String = currentDate
    ): TvShowDto

    @GET("discover/tv?sort_by=first_air_date.asc")
    suspend fun getComingSoonTvShows(
        @Query("page") page: Int,
        @Query("first_air_date.gte") firstAirDate: String = currentDate
    ): TvShowDto

    @GET("discover/tv")
    suspend fun getCustomTvShows(
        @Query("page") page: Int,
        @Query("first_air_date.gte") firstAirDateGte: String?,
        @Query("first_air_date.lte") firstAirDateLte: String?,
        @Query("vote_average.gte") voteAverage: Float?,
        @Query("with_genres") genreWithFilter: String?,
        @Query("without_genres") genreWithoutFilter: String?
    ): TvShowDto

    @GET("search/tv")
    suspend fun getSearchedTvShows(
        @Query("query") query: String
    ): TvShowDto

    @GET("tv/{tvShowId}?append_to_response=credits,videos")
    suspend fun getTvShowDetails(@Path("tvShowId") tvShowId: Long): TvShowDetail

//    @GET("genre/movie/list")
//    suspend fun getGenres(): GenresDto
//
//    @GET("person/{personId}?append_to_response=movie_credits")
//    suspend fun getPeopleDetails(@Path("personId") personId: Long): PeopleDetail

}

class MovieDbInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url: HttpUrl = chain.request()
            .url()
            .newBuilder()
            .addQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
            .addQueryParameter("language", "en-US")
            .addQueryParameter("region", "sg")
            .build()
        val urlStr = url.toString().replace("%252C", "%2C")
        return chain.proceed(
            chain.request()
                .newBuilder()
                .url(urlStr)
                .build()
        )
    }
}