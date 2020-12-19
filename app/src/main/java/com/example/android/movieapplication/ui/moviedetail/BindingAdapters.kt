package com.example.android.movieapplication.ui.moviedetail

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.movieapplication.R
import com.example.android.movieapplication.model.Genre
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

@BindingAdapter("genres")
fun TextView.setGenres(genres: List<Genre>?) {
    if (!genres.isNullOrEmpty()) {
        this.text = genres.joinToString(", ") { it.name }
    }
}

@BindingAdapter("genreDisplayChips")
fun bindChips(chipGroup: ChipGroup, genres: List<Genre>? = emptyList()) {
    genres?.map {genre ->
        val chip = Chip(chipGroup.context)
        chip.id = genre.id
        chip.text = genre.name
        chip.isEnabled = false
        chip.setTextColor(Color.BLACK)
        val chipDrawable = ChipDrawable.createFromAttributes(
            chipGroup.context,
            null,
            0,
            R.style.Widget_MaterialComponents_Chip_Choice
        )
        chip.setChipDrawable(chipDrawable)
        chipGroup.addView(chip)
    }

}