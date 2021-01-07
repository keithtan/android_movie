package com.example.android.movieapplication.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TvShowRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tvShowRemoteKey: List<TvShowRemoteKeys>)

    @Query("SELECT * FROM tv_show_remote_keys WHERE tvShowId = :tvShowId")
    suspend fun remoteKeysTvShowId(tvShowId: Long): TvShowRemoteKeys?

    @Query("DELETE FROM tv_show_remote_keys")
    suspend fun clearRemoteKeys()
}