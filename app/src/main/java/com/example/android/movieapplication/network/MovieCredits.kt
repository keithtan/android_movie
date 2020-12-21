package com.example.android.movieapplication.network

import com.example.android.movieapplication.model.MovieCast

data class MovieCredits(
    val id: Int?,
    val cast: List<MovieCast>
)