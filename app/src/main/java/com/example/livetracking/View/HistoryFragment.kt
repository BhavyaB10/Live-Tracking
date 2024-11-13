//package com.example.livetracking.View
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.livetracking.Model.LocationDatabaseHelper
//import com.example.livetracking.Model.PreviousEvent
//import com.example.livetracking.R
//import com.example.livetracking.ViewModel.SharedViewModel
//
//class HistoryFragment : Fragment() {
//
//    private lateinit var locationDbHelper: LocationDatabaseHelper
//    private lateinit var previousSessionRV: RecyclerView
//    private val prevSessionLocation = mutableListOf<PreviousEvent>()
//    private lateinit var previousSessionAdapter: PreviousLocationAdapter
//
//    private lateinit var viewModel: SharedViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_history, container, false)
//
//        // Get the shared ViewModel
//        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
//
//        // Observe the date range changes
//        viewModel.dateRange.observe(viewLifecycleOwner) { dateRange ->
//            val (startDate, endDate) = dateRange
//            filterListByDateRange(startDate, endDate)
//        }
//
//
//        // Check if dataList is populated
//        Log.d("Debug", "Data List Size: ${prevSessionLocation.size}")
//        prevSessionLocation.forEach { location ->
//            Log.d("Debug", "Location: $location")
//        }
//
//        previousSessionRV = view.findViewById(R.id.previousSessionRV)
//        previousSessionRV.layoutManager = LinearLayoutManager(activity)
//        previousSessionAdapter = PreviousLocationAdapter(prevSessionLocation)
//        previousSessionRV.adapter = previousSessionAdapter
//
//        locationDbHelper = LocationDatabaseHelper(requireContext())
//
//        fetchDataFromDB()
//
//        return view
//    }
//
//    private fun fetchDataFromDB() {
//        val previousCursor = locationDbHelper.getAllPreviousSessionLocations()
//
//        with(previousCursor)
//        {
//            while (moveToNext()) {
//                val id = getInt(getColumnIndexOrThrow("_id"))
//                val start_time = getLong(getColumnIndexOrThrow("start_time"))
//                val start_lat = getDouble(getColumnIndexOrThrow("start_lat"))
//                val start_long = getDouble(getColumnIndexOrThrow("start_long"))
//                val end_lat = getDouble(getColumnIndexOrThrow("end_lat"))
//                val end_long = getDouble(getColumnIndexOrThrow("end_long"))
//                val end_time = getLong(getColumnIndexOrThrow("end_time"))
//                val total_distance = getFloat(getColumnIndexOrThrow("total_distance"))
//
//                prevSessionLocation.add(
//                    PreviousEvent(
//                        id,
//                        start_time,
//                        start_lat,
//                        start_long,
//                        end_lat,
//                        end_long,
//                        end_time,
//                        total_distance
//                    )
//                )
//            }
//            close()
//        }
//
//        previousSessionAdapter.notifyDataSetChanged()
//    }
//
//    fun filterListByDateRange(startDate: Long, endDate: Long) {
//        val filteredList = prevSessionLocation.filter { item ->
//            // The start_time and end_time are already in Long format (timestamps)
//            item.start_time >= startDate && item.end_time <= endDate
//        }
//
//        // Debug: Check filtered results
//        Log.d("Debug", "Filtered List Size: ${filteredList.size}")
//
//        // Update your RecyclerView with the filtered list
//        previousSessionAdapter.updateData(filteredList)
//    }
//
//}

// HistoryFragment.kt
package com.example.livetracking.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.livetracking.Model.PreviousEvent
import com.example.livetracking.R
import com.example.livetracking.ViewModel.SharedViewModel

class HistoryFragment : Fragment() {

    private lateinit var previousSessionRV: RecyclerView
    private val prevSessionLocation = mutableListOf<PreviousEvent>()
    private lateinit var previousSessionAdapter: PreviousLocationAdapter

    private lateinit var emptyView: ImageView

    private lateinit var viewModel: SharedViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Get the shared ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observe the date range changes
        viewModel.dateRange.observe(viewLifecycleOwner) { dateRange ->
            val (startDate, endDate) = dateRange
            filterListByDateRange(startDate, endDate)
        }

        previousSessionRV = view.findViewById(R.id.previousSessionRV)
        previousSessionRV.layoutManager = LinearLayoutManager(activity)
        previousSessionAdapter = PreviousLocationAdapter(prevSessionLocation)
        previousSessionRV.adapter = previousSessionAdapter

        emptyView = view.findViewById(R.id.emptyView)

        // Observe the previous sessions
        viewModel.previousSessions.observe(viewLifecycleOwner) { sessions ->
            prevSessionLocation.clear()
            prevSessionLocation.addAll(sessions)
            previousSessionAdapter.notifyDataSetChanged()

            checkIfRecyclerViewIsEmpty()
        }

        // Call this function to check the adapter's item count and show/hide the background image
        checkIfRecyclerViewIsEmpty()

        return view
    }


    private fun checkIfRecyclerViewIsEmpty() {
        if (previousSessionAdapter.itemCount == 0) {
            previousSessionRV.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            previousSessionRV.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun filterListByDateRange(startDate: Long, endDate: Long) {
        viewModel.filterSessionsByDateRange(startDate, endDate)
    }
}
