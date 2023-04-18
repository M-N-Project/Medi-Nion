package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter2_Main (fragment : FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    val id : String = ""
    val userType : String = ""
    val userDept : String = ""
    val passwd : String = ""
    val userMedal : Int = 0
    val nickname : String = ""

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                val homeFragment = HomeFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putInt("userMedal", userMedal)
                homeFragment.arguments = bundle
                return homeFragment
            }
            1 -> {
                val menuFragment = MenuFragment()
                val bundle = Bundle()
                bundle.putString("id",id)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putString("nickname", nickname)
                bundle.putInt("userMedal", userMedal)
                Log.d("wowowowo2", "$id")
                Log.d("wowowowo2", "$userType")
                Log.d("wowowowo2", "$userDept")

                menuFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                return menuFragment
            }
            2 -> {
                val scheduleFragment = ScheduleFragment()
                val bundle = Bundle()
                scheduleFragment.arguments = bundle
                return scheduleFragment
            }
            3 -> {
                val businessFragment = BusinessMainFragment()
                val bundle = Bundle()
                bundle.putString("id",id)
                businessFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                return businessFragment
            }
            else -> {
                val profileFragment = ProfileFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putString("passwd", passwd)
                bundle.putInt("userMedal", userMedal)
                bundle.putString("nickname", nickname)
                Log.d("wowowowo2", "$userType")
                profileFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                return profileFragment
            }
        }
    }
}