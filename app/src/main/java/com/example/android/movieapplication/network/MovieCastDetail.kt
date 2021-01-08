package com.example.android.movieapplication.network

import com.example.android.movieapplication.db.Movie
import com.squareup.moshi.Json

data class MovieCastDetail(
    val id: Long,
    val name: String,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    @Json(name = "place_of_birth") val placeOfBirth: String,
    @Json(name = "profile_path") val profilePath: String,
    @Json(name = "movie_credits") val movieCredits: MovieList
) {
    data class MovieList(
        @Json(name = "cast") val movieList: List<Movie>
    )
}