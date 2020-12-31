package com.example.android.movieapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.android.movieapplication.R
import com.example.android.movieapplication.db.Genre
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


@BindingAdapter("posterUrl")
fun ImageView.bindPoster(imgUrl: String?) {
    imgUrl?.let {
        val fullUrl = "https://image.tmdb.org/t/p/w154$it"
        val imgUri = fullUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
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
            .transform(CenterCrop(), RoundedCorners(50))
            .into(this)
    }
}

@BindingAdapter(value = ["filterGenres", "checkedChangeListener", "longClickListener"])
fun ChipGroup.bindChips(
    genres: List<Genre>? = emptyList(),
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

//@BindingAdapter(value = ["startYear"])
//fun AutoCompleteTextView.bindStartYear(
//    startYear: Int
//) {
//    this.setText(startYear.toString(), false)
//    this.setOnItemClickListener { _, view, _, _ ->
//        hideKeyboard(view)
//    }
//}
//
//@BindingAdapter(value = ["endYear"])
//fun AutoCompleteTextView.bindEndYear(
//    endYear: Int
//) {
//    this.setText(endYear.toString(), false)
//    this.setOnItemClickListener { _, view, _, _ ->
//        hideKeyboard(view)
//    }
//}
//
//private fun hideKeyboard(view: View) {
//    val imm: InputMethodManager? =
//        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//    imm?.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
//}

object Converter {
    @InverseMethod("stringToInt")
    @JvmStatic fun intToString(
        value: Int
    ): String {
        // Converts long to String.
        return value.toString()
    }

    @JvmStatic fun stringToInt(
        value: String
    ): Int {
        // Converts String to long.
        return value.toInt()
    }

}

