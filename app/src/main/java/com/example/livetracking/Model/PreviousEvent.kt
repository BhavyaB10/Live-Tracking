package com.example.livetracking.Model

data class PreviousEvent(
    val id: Int,
    val start_time: Long,
    val start_lat: Double,
    val start_long: Double,
    val end_lat: Double,
    val end_long: Double,
    val end_time: Long,
    val total_distance: Float
)