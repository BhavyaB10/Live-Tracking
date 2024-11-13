//package com.example.livetracking.View
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.livetracking.Model.LocationDatabaseHelper
//import com.example.livetracking.Model.LocationEvent
//import com.example.livetracking.R
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import java.text.SimpleDateFormat
//import java.util.*
//
//class CurrentSessionFragment : Fragment() {
//    private lateinit var locationDbHelper: LocationDatabaseHelper
//    private lateinit var currentSessionRecyclerView: RecyclerView
//    private val currentSessionLocations = mutableListOf<LocationEvent>()
//    private lateinit var currentSessionAdapter: LocationAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_current_session, container, false)
//
//        // Initialize the RecyclerViews and Adapters for current and previous sessions
//        currentSessionRecyclerView = view.findViewById(R.id.currentSessionRV)
//        currentSessionRecyclerView.layoutManager = LinearLayoutManager(activity)
//        currentSessionAdapter = LocationAdapter(currentSessionLocations)
//        currentSessionRecyclerView.adapter = currentSessionAdapter
//
//        locationDbHelper = LocationDatabaseHelper(requireContext())
//
//        fetchDataFromDb()
//
//        return view
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onLocationEvent(event: LocationEvent) {
//        // Save the new location to the database
//        locationDbHelper.addLocationToCurrentSession(
//            event.latitude,
//            event.longitude,
//            event.displacement,
//            event.speed,
//            event.timestamp
//        )
//
//        // Update the list and notify adapter
//        fetchDataFromDb()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        // Register with EventBus to receive new location events
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        // Unregister from EventBus when the activity stops
//        EventBus.getDefault().unregister(this)
//    }
//
//
//    // Fetch data for both current and previous sessions from the database and display them in the RecyclerViews
//    private fun fetchDataFromDb() {
//        currentSessionLocations.clear()
//
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//
//        val currentCursor = locationDbHelper.getAllCurrentSessionLocations()
//        with(currentCursor) {
//            while (moveToNext()) {
//                val id = getInt(getColumnIndexOrThrow("_id"))
//                val latitude = getDouble(getColumnIndexOrThrow("latitude"))
//                val longitude = getDouble(getColumnIndexOrThrow("longitude"))
//                val displacement = getFloat(getColumnIndexOrThrow("displacement"))
//                val speed = getFloat(getColumnIndexOrThrow("speed"))
//                val timeStamp = getLong(getColumnIndexOrThrow("timestamp"))
//
//                // Convert timestamp to formatted date string
//                val dateString = dateFormat.format(Date(timeStamp))
//
//                currentSessionLocations.add(
//                    LocationEvent(
//                        id,
//                        latitude,
//                        longitude,
//                        displacement,
//                        speed,
//                        timeStamp
//                    )
//                )
//            }
//            close()
//        }
//
//
//        currentSessionAdapter.notifyDataSetChanged()
//    }
//
//}

// CurrentSessionFragment.kt
package com.example.livetracking.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.livetracking.Model.LocationEvent
import com.example.livetracking.R
import com.example.livetracking.ViewModel.LocationViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CurrentSessionFragment : Fragment() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var currentSessionRecyclerView: RecyclerView
    private val currentSessionLocations = mutableListOf<LocationEvent>()
    private lateinit var currentSessionAdapter: LocationAdapter

    private lateinit var emptyView: ImageView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_current_session, container, false)

        // Initialize the RecyclerViews and Adapters for current and previous sessions
        currentSessionRecyclerView = view.findViewById(R.id.currentSessionRV)
        currentSessionRecyclerView.layoutManager = LinearLayoutManager(activity)
        currentSessionAdapter = LocationAdapter(currentSessionLocations)
        currentSessionRecyclerView.adapter = currentSessionAdapter

        emptyView = view.findViewById(R.id.emptyView)

        // Initialize ViewModel
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        // Observe LiveData from ViewModel
        locationViewModel.currentSessionLocations.observe(viewLifecycleOwner) { locations ->
            currentSessionLocations.clear()
            currentSessionLocations.addAll(locations)
            currentSessionAdapter.notifyDataSetChanged()
            checkIfRecyclerViewIsEmpty()
        }


        // Call this function to check the adapter's item count and show/hide the background image
        checkIfRecyclerViewIsEmpty()
        

        return view
    }

    private fun checkIfRecyclerViewIsEmpty() {
        if (currentSessionAdapter.itemCount == 0) {
            currentSessionRecyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            currentSessionRecyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationEvent(event: LocationEvent) {
        // Add new location to the database through ViewModel
        locationViewModel.addLocationToCurrentSession(event)
    }

    override fun onStart() {
        super.onStart()
        // Register with EventBus to receive new location events
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        // Unregister from EventBus when the activity stops
        EventBus.getDefault().unregister(this)
    }
}
