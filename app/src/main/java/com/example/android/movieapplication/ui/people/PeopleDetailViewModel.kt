package com.example.android.movieapplication.ui.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.PeopleDetail
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class PeopleDetailViewModel(
    personId: Long,
    repository: MovieDbRepository
) : ViewModel() {

    val peopleDetail: LiveData<PeopleDetail?> = liveData {
        repository.getPeopleDetails(personId)
            .catch { e ->
                println(e)
                emit(null)
            }
            .collect { value ->
                emit(value)
            }
    }

    private val _navigateToSelectedMovie = MutableLiveData<Long>()
    val navigateToSelectedMovie: LiveData<Long>
        get() = _navigateToSelectedMovie

    fun displayMovieDetails(movieId: Long) {
        _navigateToSelectedMovie.value = movieId
    }

    fun displayMovieDetailsComplete() {
        _navigateToSelectedMovie.value = null
    }

}