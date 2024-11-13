package com.example.livetracking.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.livetracking.View.CurrentSessionFragment
import com.example.livetracking.View.HistoryFragment

class FragmentPageAdapter
    (fragmentManager:FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        return if (position == 0)
            CurrentSessionFragment()
        else
            HistoryFragment()
    }
}