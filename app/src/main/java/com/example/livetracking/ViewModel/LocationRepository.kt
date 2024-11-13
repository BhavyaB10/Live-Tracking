// LocationRepository.kt
package com.example.livetracking.ViewModel

import android.content.Context
import com.example.livetracking.Model.LocationDatabaseHelper
import com.example.livetracking.Model.LocationEvent
import com.example.livetracking.Model.PreviousEvent

class LocationRepository(context: Context) {

    private val locationDbHelper = LocationDatabaseHelper(context)

    fun getCurrentSessionLocations(): List<LocationEvent> {
        val locations = mutableListOf<LocationEvent>()
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val cursor = locationDbHelper.getAllCurrentSessionLocations()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("_id"))
                val latitude = it.getDouble(it.getColumnIndexOrThrow("latitude"))
                val longitude = it.getDouble(it.getColumnIndexOrThrow("longitude"))
                val displacement = it.getFloat(it.getColumnIndexOrThrow("displacement"))
                val speed = it.getFloat(it.getColumnIndexOrThrow("speed"))
                val timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp"))

                locations.add(
                    LocationEvent(
                        id,
                        latitude,
                        longitude,
                        displacement,
                        speed,
                        timestamp
                    )
                )
            }
        }
        return locations
    }


    fun getPreviousSessionLocations(): List<PreviousEvent> {
        val sessions = mutableListOf<PreviousEvent>()

        val cursor = locationDbHelper.getAllPreviousSessionLocations()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("_id"))
                val start_time = it.getLong(it.getColumnIndexOrThrow("start_time"))
                val start_lat = it.getDouble(it.getColumnIndexOrThrow("start_lat"))
                val start_long = it.getDouble(it.getColumnIndexOrThrow("start_long"))
                val end_lat = it.getDouble(it.getColumnIndexOrThrow("end_lat"))
                val end_long = it.getDouble(it.getColumnIndexOrThrow("end_long"))
                val end_time = it.getLong(it.getColumnIndexOrThrow("end_time"))
                val total_distance = it.getFloat(it.getColumnIndexOrThrow("total_distance"))

                sessions.add(
                    PreviousEvent(
                        id,
                        start_time,
                        start_lat,
                        start_long,
                        end_lat,
                        end_long,
                        end_time,
                        total_distance
                    )
                )
            }
        }
        return sessions
    }

    fun addLocationToCurrentSession(locationEvent: LocationEvent) {
        locationDbHelper.addLocationToCurrentSession(
            locationEvent.latitude,
            locationEvent.longitude,
            locationEvent.displacement,
            locationEvent.speed,
            locationEvent.timestamp
        )
    }
}
