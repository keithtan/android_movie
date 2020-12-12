package com.example.android.movieapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GenresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(genres: List<Genre>)

    @Query("SELECT * FROM genres")
    fun liveGenres(): LiveData<List<Genre>>

    @Query("SELECT * FROM genres")
    suspend fun genres(): List<Genre>

    @Update
    suspend fun updateGenre(genre: Genre)

    @Query("DELETE FROM genres")
    suspend fun clearGenres()
}