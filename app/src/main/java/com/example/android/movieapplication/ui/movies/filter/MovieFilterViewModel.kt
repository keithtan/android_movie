package com.example.android.movieapplication.ui.movies.filter

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.GenrePreferences
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.ui.movies.moviesection.Section
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

    private val movieFilterModelFlow = repository.movieFilterFlow.map {
        val genreList = it.genrePrefList.toDomainModel()
        FilterModel(it.startYear, it.endYear, it.voteAverage, genreList)
    }
    val movieFilterModel = movieFilterModelFlow.asLiveData()

    private val tvShowFilterModelFlow = repository.tvShowFilterFlow.map {
        val genreList = it.genrePrefList.toDomainModel()
        FilterModel(it.startYear, it.endYear, it.voteAverage, genreList)
    }
    val tvShowFilterModel = tvShowFilterModelFlow.asLiveData()

    fun setInitialFilter(section: Section) {
        viewModelScope.launch {
            when (section) {
                is Section.MovieSection -> setInitialMovieFilter()
                is Section.TvShowSection -> setInitialTvShowFilter()
            }
        }
    }

    private suspend fun setInitialMovieFilter() {
        movieFilterModelFlow.first().let { filter ->
            if (filter.startYear != DEFAULT_YEAR)
                startYear.value = filter.startYear
            if (filter.endYear != DEFAULT_YEAR)
                endYear.value = filter.endYear
            if (filter.voteAverage != DEFAULT_VOTE)
                voteAverage.value = filter.voteAverage
            if (filter.genrePrefList.isEmpty()) {
                _genres.value = repository.getNetworkMovieGenres()
            }
            else {
                _genres.value = filter.genrePrefList
            }
        }
    }

    private suspend fun setInitialTvShowFilter() {
        tvShowFilterModelFlow.first().let { filter ->
            if (filter.startYear != DEFAULT_YEAR)
                startYear.value = filter.startYear
            if (filter.endYear != DEFAULT_YEAR)
                endYear.value = filter.endYear
            if (filter.voteAverage != DEFAULT_VOTE)
                voteAverage.value = filter.voteAverage
            if (filter.genrePrefList.isEmpty()) {
                _genres.value = repository.getNetworkTvShowGenres()
            }
            else {
                _genres.value = filter.genrePrefList
            }
        }
    }

    fun saveMovieFilter() {
        viewModelScope.launch {
            repository.updateMovieFilter(
                startYear.value!!,
                endYear.value!!,
                voteAverage.value!!,
                _genres.value!!)
        }
    }

    fun saveTvShowFilter() {
        viewModelScope.launch {
            repository.updateTvShowFilter(
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

    private fun List<GenrePreferences>.toDomainModel(): List<GenreModel> {
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