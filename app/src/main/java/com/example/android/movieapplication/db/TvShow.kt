package com.example.android.movieapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "tv_shows")
data class TvShow(
    @PrimaryKey(autoGenerate = true) val tvShowId: Long = 0,
    var position: Int = 0,
    @Json(name = "first_air_date") val firstAirDate: String?,
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val name: String,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "vote_average") val voteAverage: Double
)
