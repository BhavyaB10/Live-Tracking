package com.example.livetracking.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.livetracking.Model.PreviousEvent
import com.example.livetracking.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PreviousLocationAdapter(private var prevLocation : MutableList<PreviousEvent>):RecyclerView.Adapter<PreviousLocationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val sTime : TextView = view.findViewById(R.id.startTime)
        val sLat : TextView = view.findViewById(R.id.startLat)
        val sLong : TextView = view.findViewById(R.id.startLong)
        val eLat : TextView = view.findViewById(R.id.endLat)
        val eLong : TextView = view.findViewById(R.id.endLong)
        val eTime : TextView = view.findViewById(R.id.endTime)
        val dist : TextView = view.findViewById(R.id.total_diatance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_previous_session, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return prevLocation.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = prevLocation[position]

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING

        holder.sLat.text="Start Latitude: ${location.start_lat}"
        holder.sLong.text="Start Longitude: ${location.start_long}"
        holder.eLat.text="End Latitude:${location.end_lat}"
        holder.eLong.text="End Longitude:${location.end_long}"

        val dist = (location.total_distance)/1000
        holder.dist.text="Total Distance: ${df.format(dist)}K.M."

       // val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM d ,yyyy hh:mm:ss a", Locale.getDefault())
        val startTimeString = dateFormat.format(Date(location.start_time))
        val endTimeString = dateFormat.format(Date(location.end_time))

        holder.sTime.text="Start Time: $startTimeString"
        holder.eTime.text="End Time: $endTimeString"
    }

    // Method to update the data in the adapter and refresh the RecyclerView
    fun updateData(newData: List<PreviousEvent>) {
        prevLocation.clear()
        prevLocation.addAll(newData)
        notifyDataSetChanged()
    }


}