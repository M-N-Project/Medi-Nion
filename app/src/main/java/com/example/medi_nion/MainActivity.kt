package com.example.medi_nion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

private var backPressedTime: Long = 0

class MainActivity : AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.itemIconTintList = null
        //bottomNavigationView.itemBackground = null
        bottomNavigationView.itemTextColor = null

        val id = intent.getStringExtra("id")
        val userType = intent.getStringExtra("userType")
        val userDept = intent.getStringExtra("userDept")
        val passwd = intent.getStringExtra("passwd")
        val userGrade = intent.getStringExtra("userGrade")

        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bnv.run { setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()
                    var bundle = Bundle()
                    bundle.putString("id",id)
                    bundle.putString("userType", userType)
                    bundle.putString("userDept", userDept)
                    bundle.putString("userGrade", userGrade)
                    homeFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, homeFragment).commit()
                }
                R.id.menuFragment -> {
                    val menuFragment = MenuFragment()
                    var bundle = Bundle()
                    bundle.putString("id",id)
                    bundle.putString("userType", userType)
                    bundle.putString("userDept", userDept)
                    bundle.putString("userGrade", userGrade)
                    menuFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()
                }
                R.id.scheduleFragment -> {
                    val scheduleFragment = ScheduleFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, scheduleFragment).commit()
                }
                R.id.businessFragment -> {
                    val businessFragment = BusinessMainFragment()
                    var bundle = Bundle()
                    bundle.putString("id",id)
                    bundle.putString("userGrade", userGrade)
                    businessFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, businessFragment).commit()
                }
                R.id.profileFragment -> {
                    val profileFragment = ProfileFragment()
                    var bundle = Bundle()
                    bundle.putString("id",id)
                    bundle.putString("userType", userType)
                    bundle.putString("userDept", userDept)
                    bundle.putString("passwd", passwd)
                    bundle.putString("userGrade", userGrade)
                    profileFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, profileFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.homeFragment
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finishAffinity()
        }
    }
}