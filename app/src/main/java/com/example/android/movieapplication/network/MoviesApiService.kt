package com.example.android.movieapplication.network

import com.example.android.movieapplication.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

private const val BASE_URL = "https://api.themoviedb.org/3/"

private val currentDate: String
    get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

interface MoviesApiService {

    @GET("discover/movie?sort_by=release_date.desc&vote_average.gte=5")
    suspend fun getLatest(
        @Query("page") page: Int,
        @Query("release_date.lte") release_date: String = currentDate
    ): MovieData

    @GET("discover/movie?sort_by=release_date.asc")
    suspend fun getComingSoon(
        @Query("page") page: Int,
        @Query("release_date.gte") release_date: String = currentDate
    ): MovieData

    @GET("discover/movie")
    suspend fun getCustomMovies(
        @Query("page") page: Int,
        @Query("release_date.gte") releaseDateGte: String?,
        @Query("release_date.lte") releaseDateLte: String?,
        @Query("vote_average.gte") voteAverage: Float?,
        @Query(value = "with_genres") genreWithFilter: String?,
        @Query(value = "without_genres") genreWithoutFilter: String?
    ): MovieData

    @GET("search/movie")
    suspend fun getSearchedMovies(
        @Query("query") query: String
    ): MovieData

    @GET("movie/now_playing")
    suspend fun getMovies(@Query("page") page: Int): MovieData

    @GET("movie/{movieId}?append_to_response=credits,videos")
    suspend fun getMovieDetails(@Path("movieId") movieId: Long): MovieDetail

    @GET("genre/movie/list")
    suspend fun getGenres(): Genres

    @GET("person/{personId}?append_to_response=movie_credits")
    suspend fun getPeopleDetails(@Path("personId") personId: Long): PeopleDetail
}

class MovieDbInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url: HttpUrl = chain.request()
            .url()
            .newBuilder()
            .addQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
            .addQueryParameter("language", "en-US")
            .addQueryParameter("region", "sg")
            .build()
        val urlStr = url.toString().replace("%252C", "%2C")
        println(urlStr)
        return chain.proceed(
            chain.request()
                .newBuilder()
                .url(urlStr)
                .build()
        )
    }
}

private val client = OkHttpClient.Builder()
    .addInterceptor(MovieDbInterceptor())
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

object MoviesApi {
    val retrofitService : MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }
}