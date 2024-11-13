package com.example.livetracking.View

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.livetracking.*
import com.example.livetracking.Model.LocationEvent
import com.example.livetracking.Model.LocationTrackingService
import com.example.livetracking.Model.LocationDatabaseHelper
import com.example.livetracking.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private var service: Intent? = null
    private var isTracking: Boolean = false

    private lateinit var locationDbHelper: LocationDatabaseHelper
    private var previousLocation: Pair<Double, Double>? = null
    private var totalDistance = 0.0

    //Navigation drawer
    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var lottie : LottieAnimationView

    // Register permission request for background location (needed for Android Q and above)
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // Permission granted, you can start the service
            }
        }

    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        } else {
                            startTracking()
                        }
                    } else {
                        startTracking()
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    startTracking()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        service = Intent(this, LocationTrackingService::class.java)
        locationDbHelper = LocationDatabaseHelper(this)

        // Display the last known location when the activity starts
        displayLastLocation()

        lottie = findViewById(R.id.lottie)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_700))

        binding.apply {
            btnLocationTracking.setOnClickListener {
                if (isTracking) {
                    stopTracking()
                } else {
                    checkPermissions()
                }
            }
            btnViewSavedLocations.setOnClickListener {
                val intent = Intent(this@MainActivity, ViewLocationsActivity::class.java)
                startActivity(intent)
            }

            menuImage.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        val navView: NavigationView = findViewById(R.id.navigation_view)

        navView.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_700))

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.todo ->{
                    startActivity(Intent(this@MainActivity, TodoActivity::class.java))
                }
                R.id.tracking -> {
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    // Function to check and request necessary permissions
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionsToRequest = mutableListOf<String>()

            // Check location permissions
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
                permissionsToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            // Check notification permission (for Android 13 and above)
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }

            // Request permissions if not already granted
            if (permissionsToRequest.isNotEmpty()) {
                locationPermissions.launch(permissionsToRequest.toTypedArray())
            } else {
                startTracking()
            }
        } else {
            // Handle permissions for older Android versions
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                startTracking()
            }
        }
    }

    private fun startTracking() {
        Toast.makeText(this@MainActivity,"Location Tracking is Started...",Toast.LENGTH_LONG).show()
        lottie.visibility = View.VISIBLE  // Make the animation visible
        lottie.playAnimation()              // Start the animation
        startService(service)
        binding.btnLocationTracking.setBackgroundResource(R.drawable.stop)
        isTracking = true
    }

    private fun stopTracking() {
        lottie.cancelAnimation()          // Stop the animation
        lottie.visibility = View.GONE     // Make the animation invisible
        stopService(service)
        binding.btnLocationTracking.setBackgroundResource(R.drawable.play_btn)
        isTracking = false


        val location = locationDbHelper.getLastLocationFromCurrentSession()
        if(location!=null)
        {
            // Move current session data to the previous session
            locationDbHelper.moveCurrentSessionToPrevious()
        }
        totalDistance = 0.0
        previousLocation = null
    }

    override fun onDestroy() {
        super.onDestroy()

        lottie.cancelAnimation()          // Stop the animation
        lottie.visibility = View.GONE     // Make the animation invisible

        // Stop the service and unregister EventBus
        stopTracking()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    // Receive and display location updates through EventBus
    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        binding.tvLatitude.text = "LAT: ${locationEvent.latitude}"
        binding.tvLongitude.text = "LONG: ${locationEvent.longitude}"

        if (previousLocation != null) {
            val distance = distance(
                previousLocation!!.first,
                previousLocation!!.second,
                locationEvent.latitude,
                locationEvent.longitude
            )
            totalDistance += distance

            binding.tvDisplacement.text = "DIST -> ${String.format("%.2f", totalDistance)} K.M"
        }

        previousLocation = Pair(locationEvent.latitude, locationEvent.longitude)

        // Save the current location data into the database
        locationDbHelper.addLocationToCurrentSession(locationEvent.latitude, locationEvent.longitude, totalDistance.toFloat(), locationEvent.speed, locationEvent.timestamp)
    }

    // Display the last known location from the database
    private fun displayLastLocation() {
        val location = locationDbHelper.getLastLocationFromCurrentSession()
        if (location != null) {
            binding.tvLatitude.text = "LAT: ${location.latitude}"
            binding.tvLongitude.text = "LONG: ${location.longitude}"
            locationDbHelper.clearCurrentSession()
        } else {
            binding.tvLatitude.text = "LAT: N/A"
            binding.tvLongitude.text = "LONG: N/A"
        }
    }

    // Function to calculate the distance between two points using the Haversine formula
    private fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val lat1Rad = toRadians(lat1)
        val long1Rad = toRadians(long1)
        val lat2Rad = toRadians(lat2)
        val long2Rad = toRadians(long2)

        val dlong = long2Rad - long1Rad
        val dlat = lat2Rad - lat1Rad

        var ans = sin(dlat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dlong / 2).pow(2)
        ans = 2 * asin(sqrt(ans))

        val R = 6371.0 // Radius of Earth in Kilometers

        return ans * R
    }

    // Convert degree to radians
    private fun toRadians(degree: Double): Double {
        return degree * (PI / 180)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }
}

