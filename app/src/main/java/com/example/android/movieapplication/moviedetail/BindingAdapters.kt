package com.example.android.movieapplication.moviedetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.movieapplication.model.MovieDetail.Genre

@BindingAdapter("genres")
fun TextView.setGenres(genres: List<Genre>?) {
    if (!genres.isNullOrEmpty()) {
        this.text = genres.joinToString(" | ") { it.name }
    }
}