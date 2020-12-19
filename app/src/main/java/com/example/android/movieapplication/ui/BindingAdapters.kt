package com.example.android.movieapplication.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.example.android.movieapplication.R
import com.example.android.movieapplication.db.Genre
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@BindingAdapter(value = ["posterUrl", "imageRequestListener"], requireAll = false)
fun bindPoster(imgView: ImageView, imgUrl: String?, listener: RequestListener<Drawable>?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w154$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .listener(listener)
            .transform(CenterCrop(), RoundedCorners(25))
            .into(imgView)
    }
}

@BindingAdapter(value = ["backdropUrl", "imageRequestListener"], requireAll = false)
fun bindBackdrop(imgView: ImageView, imgUrl: String?, listener: RequestListener<Drawable>?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w780$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .listener(listener)
            .into(imgView)
    }
}

@BindingAdapter(value = ["filterGenres", "checkedChangedListener"])
fun ChipGroup.bindChips(genres: List<Genre>? = emptyList(), listener: CompoundButton.OnCheckedChangeListener) {
    genres?.map {genre ->
        val chip = (LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip)
            .apply {
                id = genre.id
                text = genre.name
                isChecked = genre.checked
                isCheckedIconVisible = genre.checked
                setOnCheckedChangeListener(listener)
            }
        addView(chip)
    }

}