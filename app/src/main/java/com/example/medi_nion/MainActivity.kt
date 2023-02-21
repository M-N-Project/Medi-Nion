package com.example.medi_nion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.itemIconTintList = null
        //bottomNavigationView.itemBackground = null
        bottomNavigationView.itemTextColor = null

        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val currentId = intent.getStringExtra("id")
        var bundle = Bundle()
        bundle.putString("id", currentId)

        bnv.run { setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, homeFragment).commit()
                }
                R.id.menuFragment -> {
                    val menuFragment = MenuFragment()
                    menuFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()
                }
                R.id.scheduleFragment -> {
                    val scheduleFragment = ScheduleFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, scheduleFragment).commit()
                }
                R.id.businessFragment -> {
                    val businessFragment = BusinessMainFragment()
                    businessFragment.arguments = bundle
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