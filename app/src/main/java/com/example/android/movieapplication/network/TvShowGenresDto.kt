package com.example.android.movieapplication.network

import com.squareup.moshi.Json

data class TvShowGenresDto(
    @Json(name = "genres") val tvShowGenres: List<TvShowGenreDto>
)