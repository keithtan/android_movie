package com.example.android.movieapplication.network

import com.squareup.moshi.Json

data class MovieGenresDto(
    @Json(name = "genres") val movieGenres: List<MovieGenreDto>
)