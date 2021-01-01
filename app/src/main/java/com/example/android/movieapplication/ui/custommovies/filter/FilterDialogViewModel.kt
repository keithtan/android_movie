package com.example.android.movieapplication.ui.custommovies.filter

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.db.Genre
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FilterDialogViewModel(
    private val repository: MovieDbRepository,
    application: Application
) : ObservableViewModel(application) {

    init {
        viewModelScope.launch {
            var genres = repository.getDbGenres()
            if (genres.isEmpty()) {
                genres = repository.getNetworkGenres()
                repository.saveGenres(genres)
            }
            _genres.value = genres

            setFilter()
        }
    }


    val startYear = MutableLiveData(INITIAL_YEAR)
    val endYear = MutableLiveData(currentYear)
    val voteAverage = MutableLiveData(5.0f)

    private val filterModelFlow = repository.userPreferencesFlow.map {
        FilterModel(it.startYear, it.endYear, it.voteAverage)
    }
    val filterModel = filterModelFlow.asLiveData()

    private suspend fun setFilter() {
        filterModelFlow.collect {
            if (it.startYear != DEFAULT_YEAR)
                startYear.value = it.startYear
            if (it.endYear != DEFAULT_YEAR)
                endYear.value = it.endYear
            if (it.voteAverage != DEFAULT_VOTE)
                voteAverage.value = it.voteAverage
        }
    }

    fun saveFilter() {
        viewModelScope.launch {
            repository.updateFilter(startYear.value!!, endYear.value!!, voteAverage.value!!)
        }
    }


    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>>
        get() = _genres

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

}