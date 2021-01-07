package com.example.android.movieapplication.network

import com.example.android.movieapplication.db.TvShow
import com.squareup.moshi.Json

data class TvShowDto(
    @Json(name = "total_pages") val totalPages: Int,
    val results: List<TvShow> = emptyList(),
    @Json(name = "total_results") val totalResults: Int
)