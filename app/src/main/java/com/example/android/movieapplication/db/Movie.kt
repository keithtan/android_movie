package com.example.android.movieapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val movieId: Long = 0,
    var position: Int = 0,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "first_air_date") val firstAirDate: String?,
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val title: String?,
    val name: String?,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "vote_average") val voteAverage: Double
)
