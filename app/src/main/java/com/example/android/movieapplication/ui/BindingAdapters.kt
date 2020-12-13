package com.example.android.movieapplication.ui

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.example.android.movieapplication.R
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.ui.custommovies.filter.FilterDialogViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

@BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
fun bindImage(imgView: ImageView, imgUrl: String?, listener: RequestListener<Drawable>?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w92$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .listener(listener)
            .into(imgView)
    }
}

@BindingAdapter(value = ["genreChips", "viewModel"])
fun bindChips(chipGroup: ChipGroup, genres: List<Genre>? = emptyList(), viewModel: FilterDialogViewModel) {
    genres?.map {genre ->
        println("chip")
        val chip = Chip(chipGroup.context)
        chip.id = genre.id
        chip.text = genre.name
        chip.isSelected = genre.checked
        chip.setChipBackgroundColorResource(R.color.material_on_primary_disabled)
        val chipDrawable = ChipDrawable.createFromAttributes(
            chipGroup.context,
            null,
            0,
            R.style.Widget_MaterialComponents_Chip_Choice
        )
        chip.setChipDrawable(chipDrawable)
        chip.setOnCheckedChangeListener { compoundButton, b ->
            println("id: " + compoundButton.id)
            println("checked: " + b)
            println(chip.isChecked)
            println(chip.isSelected)
            if (chip.isChecked && chip.isSelected) {
                chip.isChecked = false
                chip.isSelected = false
                genre.checked = false
            }
            else {
                chip.isChecked = b
                genre.checked = b
            }

            viewModel.updateGenre(genre)
        }
        chipGroup.addView(chip)
    }

}