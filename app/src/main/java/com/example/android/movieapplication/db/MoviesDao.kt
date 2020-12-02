package com.example.android.movieapplication.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.movieapplication.model.Movie

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Movie>)

    @Query("SELECT * FROM movies")
    fun movies(): PagingSource<Int, Movie>

    @Query("SELECT * FROM movies")
    fun movies1(): List<Movie>

    @Query("DELETE FROM movies")
    suspend fun clearMovies()
}