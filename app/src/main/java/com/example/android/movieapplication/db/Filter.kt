package com.example.android.movieapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter")
data class Filter(
    val dateFrom: String,
    val dateTo: String,
    val voteAverage: Int,
    @PrimaryKey val filterId: Long = 0L
)
