package com.example.android.movieapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val movieId: Long = 0,
    @Json(name = "release_date") val releaseDate: String,
    val id: Long,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val title: String,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "vote_average") val voteAverage: Double
)