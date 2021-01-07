package com.example.android.movieapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_show_remote_keys")
data class TvShowRemoteKeys(
    @PrimaryKey
    val tvShowId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)