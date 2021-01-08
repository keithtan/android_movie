package com.example.android.movieapplication.ui.movies.filter

import androidx.databinding.*
import com.google.android.material.slider.Slider

@InverseBindingAdapter(attribute = "android:value")
fun Slider.getSliderValue() = value

@BindingAdapter( "android:valueAttrChanged")
fun Slider.setSliderListeners(attrChange: InverseBindingListener) {
    addOnChangeListener { _, _, _ ->
        attrChange.onChange()
    }
}