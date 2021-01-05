package com.example.android.movieapplication.ui.peopledetail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.network.PeopleDetail
import com.example.android.movieapplication.ui.moviedetail.MovieDetailViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import java.util.*

class PeopleDetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    repository: MovieDbRepository
) : ViewModel() {

    fun savePersonId(personId: Long) {
        savedStateHandle.set(PERSON_KEY, personId)
    }

    val peopleDetail: LiveData<PeopleDetail?> = liveData {
        val personId = savedStateHandle.get<Long>(PERSON_KEY)
        repository.getPeopleDetails(personId!!)
            .catch { e ->
                println(e)
                emit(null)
            }
            .collect { value ->
                emit(value)
            }
    }

    val birthday = peopleDetail.map { detail ->
        detail?.birthday?.let { birth ->
            val year = findYearDiff(birth)
            birth.plus(" ($year years old)")
        }

    }

    private fun findYearDiff(birth: String): Int {
        val today = Calendar.getInstance().get(Calendar.YEAR)
        val date = birth.split("-").map { it.toInt() }
        var year = today.minus(date[0])
        if (Calendar.getInstance().get(Calendar.MONTH) < date[1] ||
            Calendar.getInstance().get(Calendar.YEAR) == date[1] && Calendar.getInstance()
                .get(Calendar.DAY_OF_MONTH) < date[2]
        ) {
            year -= 1
        }
        return year
    }

    companion object {
        private const val PERSON_KEY = "personId"
    }

}