package com.example.android.movieapplication.ui.movies.filter

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import com.example.android.movieapplication.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*

object Converter {
    @InverseMethod("stringToInt")
    @JvmStatic fun intToString(
        value: Int
    ): String {
        return value.toString()
    }

    @JvmStatic fun stringToInt(
        value: String
    ): Int {
        return value.toInt()
    }
}

const val DEFAULT_YEAR = 0
const val DEFAULT_VOTE = 0.0f
const val INITIAL_YEAR = 1874
val currentYear = Calendar.getInstance().get(Calendar.YEAR)

@BindingAdapter("startYearAdapter")
fun AutoCompleteTextView.bindStartYearAdapter(endYear: Int? = 2020) {
    val yearsFrom =
        if (endYear != DEFAULT_YEAR) (INITIAL_YEAR..endYear!!).toList()
        else (INITIAL_YEAR..currentYear).toList()
    val adapter =  ArrayAdapter(context, R.layout.list_item_year, yearsFrom)
    this.setAdapter(adapter)
}

@BindingAdapter("endYearAdapter")
fun AutoCompleteTextView.bindEndYearAdapter(startYear: Int? = 1874) {
    val yearsTo =
        if (startYear != DEFAULT_YEAR) (startYear!!..currentYear).toList()
        else (INITIAL_YEAR..currentYear).toList()
    val adapter = ArrayAdapter(context, R.layout.list_item_year, yearsTo)
    this.setAdapter(adapter)
}

@BindingAdapter(value = ["filterGenres", "checkedChangeListener", "longClickListener"])
fun ChipGroup.bindChips(
    genres: List<GenreModel>?,
    checkedChangeListener: CompoundButton.OnCheckedChangeListener,
    longClickListener: View.OnLongClickListener
) {
    genres?.map { genre ->
        val chip = (LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip)
            .apply {
                id = genre.id
                text = genre.name
                isChecked = genre.included
                isCheckedIconVisible = genre.included
                isSelected = genre.excluded
                setOnCheckedChangeListener(checkedChangeListener)
                setOnLongClickListener(longClickListener)
            }
        addView(chip)
    }
}