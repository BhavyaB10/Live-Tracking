package com.example.livetracking.Model

data class LocationEvent(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val displacement: Float, // Displacement from the previous location
    val speed: Float,        // Speed at the current location
    val timestamp: Long      // Timestamp of the location capture
)
