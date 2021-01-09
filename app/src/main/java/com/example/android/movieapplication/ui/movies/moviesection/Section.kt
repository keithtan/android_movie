package com.example.android.movieapplication.ui.movies.moviesection

import java.io.Serializable

sealed class Section : Serializable {

    data class MovieSection(override val type: TYPE): Section()

    data class TvShowSection(override val type: TYPE): Section()

    abstract val type: TYPE

}

enum class TYPE {
    LATEST, COMING_SOON, CUSTOM
}