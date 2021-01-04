package com.example.android.movieapplication.ui.movies

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.android.movieapplication.R

@BindingAdapter("posterUrl")
fun ImageView.bindPoster(imgUrl: String?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w154$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .transform(CenterCrop(), RoundedCorners(25))
            .into(this)
    }
}

@BindingAdapter("backdropUrl")
fun ImageView.bindBackdrop(imgUrl: String?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w780$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(this)
    }
}

@BindingAdapter("profileUrl")
fun ImageView.bindProfile(imgUrl: String?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w92$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .transform(CenterCrop(), RoundedCorners(50))
            .into(this)
    }
}

