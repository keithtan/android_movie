package com.example.android.movieapplication.network

import com.example.android.movieapplication.db.TvShow
import com.squareup.moshi.Json

data class TvShowCastDetail(
    val id: Long,
    val name: String,
    val biography: String?,
    val birthday: String?,
    val deathday: String?,
    @Json(name = "place_of_birth") val placeOfBirth: String?,
    @Json(name = "profile_path") val profilePath: String?,
    @Json(name = "tv_credits") val tvShowCredits: TvShowList
) {
    data class TvShowList(
        @Json(name = "cast") val tvShowList: List<TvShow>
    )
}