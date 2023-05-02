package com.example.medi_nion

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter2_Main (fragment : FragmentActivity, infomap:HashMap<String, String>) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 5

    private val id = infomap["id"]
    private val nickname = infomap["nickname"]
    private val userType = infomap["userType"]
    private val userDept = infomap["userDept"]
    private val passwd = infomap["passwd"]
    private val userMedal = infomap["userMedal"]?.toInt()


    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                val homeFragment = HomeFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                userMedal?.let { bundle.putInt("userMedal", it.toInt()) }
                homeFragment.arguments = bundle
                return homeFragment
            }
            1 -> {
                val calendarMainFragment = CalendarFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                calendarMainFragment.arguments = bundle
                return calendarMainFragment
            }
            2 -> {
                val menuFragment = MenuFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                userMedal?.let { bundle.putInt("userMedal", it.toInt()) }
                menuFragment.arguments = bundle
                return menuFragment
            }

            3 -> {
                val businessMainFragment = BusinessMainFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                businessMainFragment.arguments = bundle
                return businessMainFragment
            }
            else -> {
                val profileFragment = ProfileFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putString("passwd", passwd)
                userMedal?.let { bundle.putInt("userMedal", it.toInt()) }
                profileFragment.arguments = bundle
                return profileFragment
            }
        }
    }


}