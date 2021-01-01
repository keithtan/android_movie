package com.example.android.movieapplication.ui.custommovies.filter

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.data.toDomainModel
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FilterDialogViewModel(
    private val repository: MovieDbRepository,
    application: Application
) : ObservableViewModel(application) {


    val startYear = MutableLiveData(INITIAL_YEAR)
    val endYear = MutableLiveData(currentYear)
    val voteAverage = MutableLiveData(5.0f)

    private val _genres = MutableLiveData<List<GenreModel>>()
    val genres: LiveData<List<GenreModel>>
        get() = _genres

    private val filterModelFlow = repository.userPreferencesFlow.map {
        val genreList = it.genrePrefList.toDomainModel()
        FilterModel(it.startYear, it.endYear, it.voteAverage, genreList)
    }
    val filterModel = filterModelFlow.asLiveData()

    init {
        viewModelScope.launch {
            setFilter()
        }
    }

    private suspend fun setFilter() {
        filterModelFlow.first().let {
            if (it.startYear != DEFAULT_YEAR)
                startYear.value = it.startYear
            if (it.endYear != DEFAULT_YEAR)
                endYear.value = it.endYear
            if (it.voteAverage != DEFAULT_VOTE)
                voteAverage.value = it.voteAverage
            if (it.genrePrefList.isEmpty()) {
                _genres.value = repository.getNetworkGenres()
            }
            else {
                _genres.value = it.genrePrefList
            }
        }
    }

    fun saveFilter() {
        viewModelScope.launch {
            repository.updateFilter(
                startYear.value!!,
                endYear.value!!,
                voteAverage.value!!,
                _genres.value!!)
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