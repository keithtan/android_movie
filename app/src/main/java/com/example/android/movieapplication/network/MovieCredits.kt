package com.example.android.movieapplication.network

import com.squareup.moshi.Json

data class MovieCredits(
    val id: Int?,
    val cast: List<MovieCast>
) {
    data class MovieCast(
        val id: Long,
        val name: String,
        @Json(name = "profile_path") val profilePath: String?,
        val character: String
    )
}