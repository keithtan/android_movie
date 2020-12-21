package com.example.android.movieapplication.model

import com.squareup.moshi.Json

data class MovieCast(
    val id: Long,
    val name: String,
    @Json(name = "profile_path") val profilePath: String?,
    val character: String
)