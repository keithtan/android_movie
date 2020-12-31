package com.example.android.movieapplication.ui.custommovies.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FilterDialogViewModel(private val repository: MovieDbRepository) : ObservableViewModel() {

    init {
        viewModelScope.launch {
            var genres = repository.getDbGenres()
            if (genres.isEmpty()) {
                genres = repository.getNetworkGenres()
                repository.saveGenres(genres)
            }
            _genres.value = genres
        }
    }

    private val filterModelFlow = repository.userPreferencesFlow.map {
        FilterModel(it.startYear, it.endYear, it.voteAverage)
    }
    val filterModel = filterModelFlow.asLiveData()

    fun saveFilter() {
        viewModelScope.launch {
            println("vote: " + startYear + " " + endYear)
            repository.updateFilter(startYear.value!!, endYear.value!!, voteAverage.value!!)
        }
    }

    fun saveGenres(genres: List<Genre>) {
        viewModelScope.launch {
            repository.saveGenres(genres)
        }
    }

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>>
        get() = _genres

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


    val startYear = MutableLiveData<Int>()

    val endYear = MutableLiveData<Int>()

    val voteAverage = MutableLiveData(5.0f)



}