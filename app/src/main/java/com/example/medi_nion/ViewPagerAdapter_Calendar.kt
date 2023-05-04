package com.example.medi_nion

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter_Calendar(fragment: FragmentActivity, infomap: HashMap<String, String>): FragmentStateAdapter(fragment) {
    var fragments : ArrayList<Fragment> = ArrayList()

    private val id = infomap["id"]
    private val presentDate = infomap["presentDate"]
    private val year = infomap["year"]
    private val month = infomap["month"]
    private val date = infomap["date"]
    private val week = infomap["week"]

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val timeTableFragment = TimeTableFragment()   //TabLayout으로 ,, ?
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("presentDate", presentDate)
                bundle.putString("year", year)
                bundle.putString("month", month)
                bundle.putString("date", date)
                bundle.putString("week", week)
                timeTableFragment.arguments = bundle
                return timeTableFragment
            }
            else -> {
                val calendarFragment = CalendarFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                calendarFragment.arguments = bundle
                return calendarFragment
            }
        }
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size-1)
    }
}