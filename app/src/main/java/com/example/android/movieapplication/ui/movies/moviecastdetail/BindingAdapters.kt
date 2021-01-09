package com.example.android.movieapplication.ui.movies.moviecastdetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.db.Movie
import com.example.android.movieapplication.ui.movies.MovieListAdapter
import java.util.*

@BindingAdapter("birthday")
fun TextView.bindBirthday(birthday: String?) {
    text = birthday?.let { birth ->
        val year = findYearDiff(birth)
        birth.plus(" ($year years old)")
    } ?: ""
}

private fun findYearDiff(birth: String): Int {
    val today = Calendar.getInstance().get(Calendar.YEAR)
    val date = birth.split("-").map { it.toInt() }
    var year = today.minus(date[0])
    if (Calendar.getInstance().get(Calendar.MONTH) < date[1] ||
        Calendar.getInstance().get(Calendar.YEAR) == date[1] && Calendar.getInstance()
            .get(Calendar.DAY_OF_MONTH) < date[2]
    ) {
        year -= 1
    }
    return year
}

@BindingAdapter("movies")
fun RecyclerView.bindMovies(movies: List<Movie>?) {
    val adapter = adapter as MovieListAdapter
    adapter.submitList(movies)
}