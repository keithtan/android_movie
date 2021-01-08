package com.example.android.movieapplication.ui.movies.filter

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.MovieGenrePreferences
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MovieFilterViewModel @ViewModelInject constructor(
    private val repository: MovieDbRepository,
    application: Application
) : ObservableViewModel(application) {

    val startYear = MutableLiveData(INITIAL_YEAR)
    val endYear = MutableLiveData(currentYear)
    val voteAverage = MutableLiveData(5.0f)

    private val _genres = MutableLiveData<List<GenreModel>>()
    val genres: LiveData<List<GenreModel>>
        get() = _genres

    private val filterModelFlow = repository.movieFilterFlow.map {
        val genreList = it.genrePrefList.toDomainModel()
        FilterModel(it.startYear, it.endYear, it.voteAverage, genreList)
    }
    val filterModel = filterModelFlow.asLiveData()

    init {
        viewModelScope.launch {
            setInitialFilter()
        }
    }

    private suspend fun setInitialFilter() {
        filterModelFlow.first().let { filter ->
            if (filter.startYear != DEFAULT_YEAR)
                startYear.value = filter.startYear
            if (filter.endYear != DEFAULT_YEAR)
                endYear.value = filter.endYear
            if (filter.voteAverage != DEFAULT_VOTE)
                voteAverage.value = filter.voteAverage
            if (filter.genrePrefList.isEmpty()) {
                _genres.value = repository.getMovieNetworkGenres()
            }
            else {
                _genres.value = filter.genrePrefList
            }
        }
    }

    fun saveFilter() {
        viewModelScope.launch {
            repository.updateMovieFilter(
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

    private fun List<MovieGenrePreferences>.toDomainModel(): List<GenreModel> {
        return this.map {
            GenreModel(
                it.id,
                it.name,
                it.included,
                it.excluded
            )
        }
    }

}