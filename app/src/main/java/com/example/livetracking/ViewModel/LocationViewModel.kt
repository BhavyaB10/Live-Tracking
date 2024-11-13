// LocationViewModel.kt
package com.example.livetracking.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.livetracking.Model.LocationEvent
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val _currentSessionLocations = MutableLiveData<List<LocationEvent>>()
    val currentSessionLocations: LiveData<List<LocationEvent>> get() = _currentSessionLocations

    init {
        fetchCurrentSessionLocations()
    }

    fun fetchCurrentSessionLocations() {
        viewModelScope.launch {
            _currentSessionLocations.value = locationRepository.getCurrentSessionLocations()
        }
    }

    fun addLocationToCurrentSession(locationEvent: LocationEvent) {
        viewModelScope.launch {
            locationRepository.addLocationToCurrentSession(locationEvent)
            fetchCurrentSessionLocations() // Refresh data
        }
    }
}
