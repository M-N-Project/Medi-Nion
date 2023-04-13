package com.example.medi_nion

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter2_Main (fragment : FragmentActivity) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> MenuFragment()
            2 -> ScheduleFragment()
            3 -> BusinessMainFragment()
            else -> ProfileFragment()
        }
    }
}