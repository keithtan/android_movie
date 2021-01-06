package com.example.android.movieapplication.network

data class FilterDto(
    val startDate: String?,
    val endDate: String?,
    val voteAverage: Float,
    val includedGenres: String,
    val excludedGenres: String
)