package com.example.livetracking.View

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.livetracking.R
import com.example.livetracking.ViewModel.SharedViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class ViewLocationsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var backBtn :ImageView

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var dateRangePickerButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_locations)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        backBtn=findViewById(R.id.backBtnImg)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewPager.adapter = FragmentPageAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = when (index) {
                0 -> {
                    "Current Session"
                }
                1 -> {
                    "Previous Session"
                }

                else -> {
                    throw Resources.NotFoundException("Position Not Found")
                }
            }

        }.attach()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]


        dateRangePickerButton = findViewById(R.id.date_range_picker)

        dateRangePickerButton.setOnClickListener {
            showDateRangePicker()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDateRangePicker() {

        // val fragment = supportFragmentManager.findFragmentById(R.id.history_fragment) as HistoryFragment

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select Date").build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = datePicked.first
            val endDate = datePicked.second

            Toast.makeText(
                this,
                "${convertDateToLong(startDate)} ${convertDateToLong(endDate)}",
                Toast.LENGTH_SHORT
            ).show()

            // Set the date range in the ViewModel
            sharedViewModel.setDateRange(startDate, endDate)
        }
    }

    private fun convertDateToLong(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MMM d,yyyy hh:mm:ss a", Locale.getDefault())

        return format.format(date)
    }

}
