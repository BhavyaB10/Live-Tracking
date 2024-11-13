// SharedViewModel.kt
package com.example.livetracking.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.livetracking.Model.PreviousEvent
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val _dateRange = MutableLiveData<Pair<Long, Long>>()
    val dateRange: LiveData<Pair<Long, Long>> get() = _dateRange

    private val _previousSessions = MutableLiveData<List<PreviousEvent>>()
    val previousSessions: LiveData<List<PreviousEvent>> get() = _previousSessions

    init {
        fetchPreviousSessions()
    }

    fun setDateRange(startDate: Long, endDate: Long) {
        _dateRange.value = Pair(startDate, endDate)
    }

    private fun fetchPreviousSessions() {
        viewModelScope.launch {
            _previousSessions.value = locationRepository.getPreviousSessionLocations()
        }
    }

    fun filterSessionsByDateRange(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val allSessions = locationRepository.getPreviousSessionLocations()
            val filteredSessions = allSessions.filter { session ->
                session.start_time >= startDate && session.end_time <= endDate
            }
            _previousSessions.value = filteredSessions
        }
    }
}

