package com.example.livetracking.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.livetracking.Model.LocationEvent
import com.example.livetracking.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class LocationAdapter(private var locations: List<LocationEvent>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val latitudeTextView: TextView = view.findViewById(R.id.latTextView)
        val longitudeTextView: TextView = view.findViewById(R.id.longTextView)
        val displacementTextView: TextView = view.findViewById(R.id.displacementTextView)
        val speedTextView: TextView = view.findViewById(R.id.speedTextView)
        val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        holder.latitudeTextView.text = "Latitude: ${location.latitude}"
        holder.longitudeTextView.text = "Longitude: ${location.longitude}"
        val speed = location.speed
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        holder.speedTextView.text = "Speed: ${df.format(speed)}"

        val distance = location.displacement
        holder.displacementTextView.text = "Displacement: ${df.format(distance)}"


        // Convert timestamp to formatted date string
        val dateFormat = SimpleDateFormat("MMM d,yyyy hh:mm:ss a", Locale.US)
        val dateString = dateFormat.format(Date(location.timestamp))

        holder.timestampTextView.text = "Time : $dateString" // Display formatted date
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    fun updateData(newLocations: List<LocationEvent>) {
        locations = newLocations
        notifyDataSetChanged()
    }
}
