package com.example.android.movieapplication.ui.custommovies.filter

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
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
            println("vote: " + voteAverage)
            repository.updateFilter(startYear, endYear, voteAverage.value!!)
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

    @get:Bindable
    var startYear: Int = 1874
        set(value) {
            field = value
            notifyPropertyChanged(BR.startYear)
        }

    @get:Bindable
    var endYear: Int = 2020
        set(value) {
            field = value
            notifyPropertyChanged(BR.endYear)
        }


    val voteAverage = MutableLiveData(5.0f)




//    private val _startYear = MutableLiveData(1874)
//    val startYear: LiveData<Int>
//        get() = _startYear
//    fun setStartYear(startYear: Int) {
//        _startYear.value = startYear
//    }
//
//    private val _endYear = MutableLiveData(2020)
//    val endYear: LiveData<Int>
//        get() = _endYear
//    fun setEndYear(endYear: Int) {
//        _endYear.value = endYear
//    }
//
//    private val _voteAverage = MutableLiveData(5.0f)
//    val voteAverage: LiveData<Float>
//        get() = _voteAverage
//    fun setVoteAverage(voteAverage: Float) {
//        _voteAverage.value = voteAverage
//    }



}