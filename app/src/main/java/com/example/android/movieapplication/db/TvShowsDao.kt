package com.example.android.movieapplication.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TvShowsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<TvShow>)

    @Query("SELECT * FROM tv_shows WHERE position = :position")
    fun tvShows(position: Int): PagingSource<Int, TvShow>

    @Query("DELETE FROM tv_shows WHERE position = :position")
    suspend fun clearTvShows(position: Int)
}