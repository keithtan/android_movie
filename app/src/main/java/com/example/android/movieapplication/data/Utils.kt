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

fun List<GenrePreferences>.joinGenreIncludedIds(): String {
    return this.filter {
        it.included
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun List<GenrePreferences>.joinGenreExcludedIds(): String {
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
        genrePrefList.joinGenreIncludedIds(),
        genrePrefList.joinGenreExcludedIds()
    )
}

fun TvShowFilterPreferences.toNetworkModel(): FilterDto {
    return FilterDto(
        startYear.toStartDate(),
        endYear.toEndDate(),
        voteAverage,
        genrePrefList.joinGenreIncludedIds(),
        genrePrefList.joinGenreExcludedIds()
    )
}