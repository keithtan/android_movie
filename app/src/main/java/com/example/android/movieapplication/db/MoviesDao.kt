package com.example.android.movieapplication.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Movie>)

    @Query("SELECT * FROM movies WHERE section = :section")
    fun movies(section: String): PagingSource<Int, Movie>

    @Query("DELETE FROM movies WHERE section = :section")
    suspend fun clearMovies(section: String)
}