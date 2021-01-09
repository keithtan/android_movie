package com.example.android.movieapplication.network

import com.squareup.moshi.Json

data class MovieDetail(
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val genres: List<MovieGenreDto>,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    val runtime: Int?,
    @Json(name = "spoken_languages") val spokenLanguages: List<Language>,
    val title: String?,
    val name: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    val credits: MovieCredits,
    val videos: MovieVideos
) {

    data class Language(
        val name: String
    )

    data class MovieCredits(
        val cast: List<MovieCast>
    ) {
        data class MovieCast(
            val id: Long,
            val name: String,
            @Json(name = "profile_path") val profilePath: String?,
            val character: String
        )
    }

    data class MovieVideos(
        val results: List<VideoDetail>
    ) {
        data class VideoDetail(
            val id: String,
            val key: String,
            val name: String
        )
    }

}