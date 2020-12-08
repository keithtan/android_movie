package com.example.android.movieapplication.ui.custommovies.filter

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.FilterDao
import com.example.android.movieapplication.util.ObservableViewModel
import kotlinx.coroutines.launch

class FilterDialogViewModel(private val database: FilterDao) : ObservableViewModel() {

    init {
        getFilter()
    }

    fun saveFilter(filter: Filter) {
        viewModelScope.launch {
            database.insert(filter)
        }
    }

    private fun getFilter() {
        viewModelScope.launch {
            val data = database.filter()
            dateFrom = data?.dateFrom ?: ""
            dateTo = data?.dateTo ?: ""
        }
    }

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
    var voteAverage: Int = 5
        set(value) {
            field = value
            notifyPropertyChanged(BR.voteAverage)
        }

}