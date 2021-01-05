package com.example.android.movieapplication.di

import com.example.android.movieapplication.network.MovieDbInterceptor
import com.example.android.movieapplication.network.MoviesApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(): MoviesApiService {
        val BASE_URL = "https://api.themoviedb.org/3/"

        val client = OkHttpClient.Builder()
            .addInterceptor(MovieDbInterceptor())
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        return retrofit.create(MoviesApiService::class.java)
    }

}