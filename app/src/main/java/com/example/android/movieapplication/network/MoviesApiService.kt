package com.example.android.movieapplication.network

import com.example.android.movieapplication.BuildConfig
import com.example.android.movieapplication.model.MovieDetail
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

private const val BASE_URL = "https://api.themoviedb.org/3/"

interface MoviesApiService {

    @GET("discover/movie")
    suspend fun getLatest(
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "primary_release_date.desc"
    ): MovieData

    @GET("movie/now_playing")
    suspend fun getMovies(@Query("page") page: Int): MovieData

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Long): MovieDetail
}

class MovieDbInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url: HttpUrl = chain.request()
            .url()
            .newBuilder()
            .addQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
            .addQueryParameter("language", "en-US")
            .build()
        return chain.proceed(
            chain.request()
                .newBuilder()
                .url(url)
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