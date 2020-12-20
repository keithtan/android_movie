package com.example.android.movieapplication.ui.custommovies.filter

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.launch

class FilterDialogViewModel(private val repository: MovieDbRepository) : ObservableViewModel() {

    init {
        viewModelScope.launch {
            var genres = repository.getDbGenres()
            println("db: " + genres)
            if (genres.isEmpty()) {
                genres = repository.getNetworkGenres()
                println("network: " + genres)
                repository.saveGenres(genres)
            }
            _genres.value = genres
        }

    }

    fun saveFilter(filter: Filter) {
        viewModelScope.launch {
            repository.saveFilter(filter)
        }
    }

    val filter: LiveData<Filter?> = repository.getFilter()

    fun saveGenres(genres: List<Genre>) {
        viewModelScope.launch {
            repository.saveGenres(genres)
        }
    }

    fun updateIncludedGenres(id: Int, isChecked: Boolean) {
        _genres.value
            ?.find { it.id == id }
            ?.run {
                included = isChecked
                excluded = false
            }
    }

    fun updateExcludedGenres(id: Int, isSelected: Boolean) {
        _genres.value
            ?.find { it.id == id }
            ?.run {
                excluded = isSelected
                included = false
            }
    }

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>>
        get() = _genres



    @get:Bindable
    var dateFrom: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dateFrom)
        }

    @get:Bindable
    var dateTo: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dateTo)
        }

    @get:Bindable
    var voteAverage: Float = 5.0F
        set(value) {
            field = value
            notifyPropertyChanged(BR.voteAverage)
        }



}