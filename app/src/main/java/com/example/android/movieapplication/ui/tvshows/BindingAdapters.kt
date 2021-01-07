package com.example.android.movieapplication.ui.tvshows

import android.graphics.Color
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.movieapplication.R
import com.example.android.movieapplication.network.GenreDto
import com.example.android.movieapplication.network.TvShowDetail.TvShowCredits.TvShowCast
import com.example.android.movieapplication.network.TvShowDetail.TvShowVideos.VideoDetail
import com.example.android.movieapplication.ui.moviedetail.MovieCastAdapter
import com.example.android.movieapplication.ui.moviedetail.MovieVideoAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("backdropUrl")
fun ImageView.bindBackdrop(imgUrl: String?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w780$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(this)
    }
}

@BindingAdapter("runtime")
fun TextView.bindRuntime(runtime: Int?) {
    text = runtime?.let {
        val hours = it.div(60)
        val minutes = it.rem(60)
        String.format("%dh %02dm", hours, minutes)
    } ?: ""
}

@BindingAdapter("releaseDate")
fun TextView.bindReleaseDate(releaseDate: String?) {
    text = releaseDate?.let {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .parse(releaseDate)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        parser?.let { parser1 ->
            formatter.format(parser1)
        }
    } ?: ""
}

@BindingAdapter("rating")
fun RatingBar.bindRating(voteAverage: Double?) {
    rating = voteAverage?.div(2)?.toFloat() ?: 0.0f
}

@BindingAdapter("genreDisplayChips")
fun ChipGroup.bindChips(genres: List<GenreDto>?) {
    genres?.map {genre ->
        val chip = Chip(context)
        chip.id = genre.id
        chip.text = genre.name
        chip.isEnabled = false
        chip.setTextColor(Color.BLACK)
        val chipDrawable = ChipDrawable.createFromAttributes(
            context,
            null,
            0,
            R.style.Widget_MaterialComponents_Chip_Choice
        )
        chip.setChipDrawable(chipDrawable)
        addView(chip)
    }
}

@BindingAdapter("cast")
fun RecyclerView.bindCast(cast: List<TvShowCast>?) {
    val adapter = adapter as TvShowCastAdapter
    adapter.submitList(cast)
}

@BindingAdapter("videos")
fun RecyclerView.bindVideos(videoList: List<VideoDetail>?) {
    val adapter = adapter as TvShowVideoAdapter
    adapter.submitList(videoList)
}

