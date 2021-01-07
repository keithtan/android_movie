package com.example.android.movieapplication.network

import com.squareup.moshi.Json

data class TvShowDetail(
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val genres: List<GenreDto>,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "first_air_date") val firstAirDate: String,
    val runtime: Int,
    @Json(name = "spoken_languages") val spokenLanguages: List<Language>,
    val name: String,
    @Json(name = "vote_average") val voteAverage: Double,
    val credits: TvShowCredits,
    val videos: TvShowVideos
) {

    data class Language(
        val name: String
    )

    data class TvShowCredits(
        val cast: List<TvShowCast>
    ) {
        data class TvShowCast(
            val id: Long,
            val name: String,
            @Json(name = "profile_path") val profilePath: String?,
            val character: String
        )
    }

    data class TvShowVideos(
        val results: List<VideoDetail>
    ) {
        data class VideoDetail(
            val id: String,
            val key: String,
            val name: String
        )
    }

}