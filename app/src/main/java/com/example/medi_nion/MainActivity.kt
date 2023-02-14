package com.example.medi_nion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bnv.run { setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, homeFragment).commit()
                }
                R.id.menuFragment -> {
                    val menuFragment = MenuFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()
                }
                R.id.scheduleFragment -> {
                    val scheduleFragment = ScheduleFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, scheduleFragment).commit()
                }
                R.id.businessFragment -> {
                    val businessFragment = BusinessFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, businessFragment).commit()
                }
                R.id.profileFragment -> {
                    val profileFragment = ProfileFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, profileFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.homeFragment
        }
    }
}