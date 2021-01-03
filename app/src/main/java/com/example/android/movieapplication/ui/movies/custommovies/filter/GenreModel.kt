package com.example.android.movieapplication.ui.movies.custommovies.filter

data class GenreModel(
    val id: Int,
    val name: String,
    var included: Boolean,
    var excluded: Boolean
)
