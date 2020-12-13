package com.example.android.movieapplication.model

import com.squareup.moshi.Json

data class MovieDetail(
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val genres: List<Genre>,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String,
    val runtime: Int,
    @Json(name = "spoken_languages") val spokenLanguages: List<Language>,
    val title: String,
    @Json(name = "vote_average") val voteAverage: Double
) {

    data class Language(
        val name: String
    )
}