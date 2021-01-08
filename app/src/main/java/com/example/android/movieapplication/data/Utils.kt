package com.example.android.movieapplication.data

import com.example.android.movieapplication.*
import com.example.android.movieapplication.network.FilterDto

fun Int.toStartDate(): String? {
    return if (this != 0) "$this-01-01"
    else null
}

private fun Int.toEndDate(): String? {
    return if (this != 0) "$this-12-31"
    else null
}

fun List<MovieGenrePreferences>.joinMovieGenreIncludedIds(): String {
    return this.filter {
        it.included
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun List<MovieGenrePreferences>.joinMovieGenreExcludedIds(): String {
    return this.filter {
        it.excluded
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun MovieFilterPreferences.toNetworkModel(): FilterDto {
    return FilterDto(
        startYear.toStartDate(),
        endYear.toEndDate(),
        voteAverage,
        genrePrefList.joinMovieGenreIncludedIds(),
        genrePrefList.joinMovieGenreExcludedIds()
    )
}

fun List<TvShowGenrePreferences>.joinTvShowIncludedIds(): String {
    return this.filter {
        it.included
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun List<TvShowGenrePreferences>.joinTvShowExcludedIds(): String {
    return this.filter {
        it.excluded
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun TvShowFilterPreferences.toNetworkModel(): FilterDto {
    return FilterDto(
        startYear.toStartDate(),
        endYear.toEndDate(),
        voteAverage,
        genrePrefList.joinTvShowIncludedIds(),
        genrePrefList.joinTvShowExcludedIds()
    )
}