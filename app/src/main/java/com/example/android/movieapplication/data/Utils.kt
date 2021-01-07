package com.example.android.movieapplication.data

import com.example.android.movieapplication.GenrePreferences
import com.example.android.movieapplication.UserPreferences
import com.example.android.movieapplication.network.FilterDto

fun Int.toStartDate(): String? {
    return if (this != 0) "$this-01-01"
    else null
}

private fun Int.toEndDate(): String? {
    return if (this != 0) "$this-12-31"
    else null
}

fun List<GenrePreferences>.joinIncludedIds(): String {
    return this.filter {
        it.included
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun List<GenrePreferences>.joinExcludedIds(): String {
    return this.filter {
        it.excluded
    }.joinToString("%2C") {
        it.id.toString()
    }
}

fun UserPreferences.toNetworkModel(): FilterDto {
    return FilterDto(
        startYear.toStartDate(),
        endYear.toEndDate(),
        voteAverage,
        genrePrefList.joinIncludedIds(),
        genrePrefList.joinExcludedIds()
    )
}