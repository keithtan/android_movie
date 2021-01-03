package com.example.android.movieapplication.network

import com.example.android.movieapplication.db.Movie
import com.squareup.moshi.Json

data class MovieDto(
    @Json(name = "total_pages") val totalPages: Int,
    val results: List<Movie> = emptyList(),
    @Json(name = "total_results") val totalResults: Int
)