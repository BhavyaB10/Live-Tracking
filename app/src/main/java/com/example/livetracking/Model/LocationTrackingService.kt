//package com.example.livetracking

//import android.app.Service
//import android.content.Intent
//import android.os.IBinder
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.location.Location
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.google.android.gms.location.*
//import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
//import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
//import org.greenrobot.eventbus.EventBus
//
//class LocationTrackingService : Service() {
//    companion object {
//        const val CHANNEL_ID = "12345"
//        const val NOTIFICATION_ID = 12345
//    }
//    // Variables for location services
//    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
//    private var locationCallback: LocationCallback? = null
//    private var locationRequest: LocationRequest? = null
//
//    private var notificationManager: NotificationManager? = null
//
//    private var lastLocation: Location? = null
//
//    private lateinit var dbHelper: LocationDatabaseHelper
//
//    override fun onCreate() {
//        super.onCreate()

//        // Initialize the database helper
//        dbHelper = LocationDatabaseHelper(this)
//        // Initialize the fused location provider client
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        // Create a location request with high accuracy and 20-second intervals
//        locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 20000)
//            .setMinUpdateDistanceMeters(15f)
//            .build()
//        // Initialize the location callback
//        locationCallback = object : LocationCallback() {
//            override fun onLocationAvailability(p0: LocationAvailability) {
//                super.onLocationAvailability(p0)
//            }
//
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                onNewLocation(locationResult)
//            }
//        }
//
//
//        // Create a notification channel for foreground service (required for Android O and above)
//        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel =
//                NotificationChannel(CHANNEL_ID, "locations", NotificationManager.IMPORTANCE_HIGH)
//            notificationManager?.createNotificationChannel(notificationChannel)
//        }
//    }
//
//    // Start location updates when the service starts
//    @Suppress("MissingPermission")
//    fun createLocationRequest() {
//        try {
//            fusedLocationProviderClient?.requestLocationUpdates(
//                locationRequest!!, locationCallback!!, null
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // Stop location updates and stop the service
//    private fun removeLocationUpdates() {
//        locationCallback?.let {
//            fusedLocationProviderClient?.removeLocationUpdates(it)
//        }
//        stopForeground(true)
//        stopSelf()
//    }
//
//    // Handle new location updates
//    private fun onNewLocation(locationResult: LocationResult) {
//        val currentLocation = locationResult.lastLocation
//        val latitude = currentLocation!!.latitude
//        val longitude = currentLocation
//            .longitude
//        val timestamp = System.currentTimeMillis()
//
//        var displacement = 0f
//        var speed = 0f
//
//        if (lastLocation != null) {
//            displacement = currentLocation.distanceTo(lastLocation!!)
//            speed = currentLocation.speed
//
//        }
//
//        lastLocation = currentLocation
//
//        EventBus.getDefault().post(LocationEvent(
//            0,
//            latitude,
//            longitude,
//            displacement,
//            speed,
//            timestamp
//        ))
//
//        // Start foreground service with a notification
//        startForeground(NOTIFICATION_ID, getNotification())
//
//        // Save the location to the database
//        saveLocationToDatabase(latitude, longitude, displacement, speed, timestamp)
//    }
//
//    private fun saveLocationToDatabase(latitude: Double, longitude: Double, displacement: Float, speed: Float, timestamp: Long) {
//        dbHelper.addLocation(latitude, longitude, displacement, speed, timestamp)
//    }
//
//    // Create a notification for the foreground service
//    fun getNotification(): Notification {
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Tracking ON...")
////            .setContentText(
////                "Latitude --> ${lastLocation?.latitude}\nLongitude --> ${lastLocation?.longitude}"
////            )
//            .setContentText(
//               // "Latitude --> ${lastLocation?.latitude}\nLongitude --> ${lastLocation?.longitude}"
//             "Dice is calculating distance you travel."
//            )
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notification.setChannelId(CHANNEL_ID)
//        }
//        return notification.build()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        createLocationRequest()
//        return START_STICKY
//    }
//
//    override fun onBind(intent: Intent): IBinder? = null
//
//    override fun onDestroy() {
//        super.onDestroy()
//        removeLocationUpdates()
//    }
//}
package com.example.livetracking.Model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.livetracking.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import org.greenrobot.eventbus.EventBus

class LocationTrackingService : Service() {

    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_ID = 12345
        const val STOP_NOTIFICATION_ID = 12346
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private var notificationManager: NotificationManager? = null

    private var lastLocation: Location? = null

    private lateinit var dbHelper: LocationDatabaseHelper

    override fun onCreate() {
        super.onCreate()

        // Initialize the database helper
        dbHelper = LocationDatabaseHelper(this)

        // Initialize the fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Create a location request with high accuracy and 10-second intervals
        locationRequest =
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 10000).setMinUpdateDistanceMeters(15f)
                .build()

        // Initialize the location callback
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }

        // Create a notification channel for foreground service (required for Android O and above)
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "locations",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(notificationChannel)

        // Start foreground service with a notification
        startForeground(NOTIFICATION_ID, getNotification())
    }

    // Start location updates when the service starts
    @Suppress("MissingPermission")
    private fun createLocationRequest() {
        try {
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //Fire notification that tracking is started
        notificationManager?.notify(NOTIFICATION_ID,getNotification())
    }

    // Stop location updates and stop the service
    private fun removeLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()

        // Fire a notification that tracking has stopped
        notificationManager?.notify(STOP_NOTIFICATION_ID, getStopNotification())
    }

    // Handle new location updates
    private fun onNewLocation(locationResult: LocationResult) {
        val currentLocation = locationResult.lastLocation
        val latitude = currentLocation!!.latitude
        val longitude = currentLocation.longitude
        val timestamp = System.currentTimeMillis()

        var displacement = 0f
        var speed = 0f

        if (lastLocation != null) {
            displacement = currentLocation.distanceTo(lastLocation!!)
            speed = currentLocation.speed
        }

        lastLocation = currentLocation

        EventBus.getDefault().post(
            LocationEvent(
                0,
                latitude,
                longitude,
                displacement,
                speed,
                timestamp
            )
        )


        // Save the location to the current session table
        saveLocationToDatabase(latitude, longitude, displacement, speed, timestamp)
    }

    private fun saveLocationToDatabase(
        latitude: Double,
        longitude: Double,
        displacement: Float,
        speed: Float,
        timestamp: Long
    ) {
        if(displacement != 0F && latitude != 0.0 && longitude != 0.0)
        {
            dbHelper.addLocationToCurrentSession(latitude, longitude, displacement, speed, timestamp)
        }

    }

    // Create a notification for the foreground service
    private fun getNotification(): Notification {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking ON...")
            .setContentText("Dice is calculating distance you travel.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
        notification.setChannelId(CHANNEL_ID)
        return notification.build()
    }

    // Create a notification for when tracking stops
    private fun getStopNotification(): Notification {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking Stopped")
            .setContentText("Your tracking session has been stopped.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notification.setChannelId(CHANNEL_ID)

        return notification.build()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        notificationManager?.notify(STOP_NOTIFICATION_ID, getNotification())
        startForeground(NOTIFICATION_ID,getNotification())
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Move current session data to previous session table when service is destroyed
        //  dbHelper.moveCurrentSessionToPrevious()
       // dbHelper.clearCurrentSession()
        removeLocationUpdates()
    }

}

