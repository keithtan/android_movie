package com.example.android.movieapplication.ui.movies.filter

data class FilterModel(
    val startYear: Int,
    val endYear: Int,
    val voteAverage: Float,
    val genrePrefList: List<GenreModel>
)