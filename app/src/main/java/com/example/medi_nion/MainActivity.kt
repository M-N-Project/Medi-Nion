//package com.example.medi_nion
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.android.synthetic.main.activity_main.*
//
//private var backPressedTime: Long = 0
//
//class MainActivity : AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당
//    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        bottomNavigationView.itemIconTintList = null
//        //bottomNavigationView.itemBackground = null
//        bottomNavigationView.itemTextColor = null
//
//        val id = intent.getStringExtra("id")
//        val userType = intent.getStringExtra("userType")
//        val userDept = intent.getStringExtra("userDept")
//        val passwd = intent.getStringExtra("passwd")
//        val userMedal = intent.getIntExtra("userMedal", 0)
//        val nickname = intent.getStringExtra("nickname")
//
//        var bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//
//        bnv.run { setOnItemSelectedListener {
//            when(it.itemId) {
//                R.id.homeFragment -> {
//                    // 다른 프래그먼트 화면으로 이동하는 기능
//                    val homeFragment = HomeFragment()
//                    var bundle = Bundle()
//                    bundle.putString("id",id)
//                    bundle.putString("nickname", nickname)
//                    bundle.putString("userType", userType)
//                    bundle.putString("userDept", userDept)
//                    bundle.putInt("userMedal", userMedal)
//                    homeFragment.arguments = bundle
//                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, homeFragment).commit()
//                }
//                R.id.menuFragment -> {
//                    val menuFragment = MenuFragment()
//                    var bundle = Bundle()
//                    bundle.putString("id",id)
//                    bundle.putString("userType", userType)
//                    bundle.putString("userDept", userDept)
//                    bundle.putString("nickname", nickname)
//                    bundle.putInt("userMedal", userMedal)
//                    menuFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
//                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()
//                }
//                R.id.scheduleFragment -> {
//                    val scheduleFragment = ScheduleFragment()
//                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, scheduleFragment).commit()
//                }
//                R.id.businessFragment -> {
//                    val businessFragment = BusinessMainFragment()
//                    var bundle = Bundle()
//                    bundle.putString("id",id)
//                    businessFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
//                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, businessFragment).commit()
//                }
//                R.id.profileFragment -> {
//                    val profileFragment = ProfileFragment()
//                    var bundle = Bundle()
//                    bundle.putString("id",id)
//                    bundle.putString("userType", userType)
//                    bundle.putString("userDept", userDept)
//                    bundle.putString("passwd", passwd)
//                    bundle.putInt("userMedal", userMedal)
//                    bundle.putString("nickname", nickname)
//                    profileFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
//                    Log.d("wowowowo", "$bundle")
//                    supportFragmentManager.beginTransaction().replace(R.id.linearLayout, profileFragment).commit()
//                }
//            }
//            true
//        }
//            selectedItemId = R.id.homeFragment
//        }
//    }
//
//    override fun onBackPressed() {
//        if(System.currentTimeMillis() - backPressedTime >= 2000) {
//            backPressedTime = System.currentTimeMillis()
//            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            finishAffinity()
//        }
//    }
//}





package com.example.medi_nion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.medi_nion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.sign_up.*

private var backPressedTime: Long = 0

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener { //mainactivity, 여기서는 프레그먼트 제어를 담당

    //OnNavigationItemSelectedListener
    private lateinit var viewPager2: ViewPager2
    lateinit var binding: ActivityMainBinding

//    private var id: String? = null
//    private var nickname: String? = null
//    private var userType: String? = null
//    private var userDept: String? = null
//    private var passwd: String? = null
//    private var userMedal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///id자리



        linearLayout.isUserInputEnabled = true //false시 스크롤 막힘

        //여기서 linearLayout은 ViewPager2, id바꿀시 혹시 에러날까봐 냅둠
        binding.linearLayout.adapter = ViewPagerAdapter2_Main(this)

        binding.linearLayout.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()

        var id = intent.getStringExtra("id")
        var nickname = intent.getStringExtra("nickname")
        var userType = intent.getStringExtra("userType")
        var userDept = intent.getStringExtra("userDept")
        var passwd = intent.getStringExtra("passwd")
        var userMedal = intent.getIntExtra("userMedal", 0)

        Log.d("omomg", "$id")
        Log.d("omomg", "$userType")
        Log.d("omomg", "$userDept")

        Log.d("onNavigationItemSelect", "id: $id")
        Log.d("onNavigationItemSelect", "nickname: $nickname")
        Log.d("onNavigationItemSelect", "userType: $userType")
        Log.d("onNavigationItemSelect", "userDept: $userDept")
        Log.d("onNavigationItemSelect", "userMedal: $userMedal")

        Log.d("wowowowo", "$id")

        when(item.itemId){
            R.id.homeFragment -> {
                binding.linearLayout.currentItem = 0
                val homeFragment = HomeFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("nickname", nickname)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putInt("userMedal", userMedal)
                homeFragment.arguments = bundle
                return true
            }
            R.id.menuFragment -> {
                binding.linearLayout.currentItem = 1
                val menuFragment = MenuFragment()
                val bundle = Bundle()
                bundle.putString("id",id)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putString("nickname", nickname)
                bundle.putInt("userMedal", userMedal)
                Log.d("wowowowo", "$id")
                Log.d("wowowowo", "$userType")
                Log.d("wowowowo", "$userDept")

                menuFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                Log.d("wowowowo1", "$bundle")
                return true
            }
            R.id.scheduleFragment -> {
                binding.linearLayout.currentItem = 2
                val scheduleFragment = ScheduleFragment()
                val bundle = Bundle()
                scheduleFragment.arguments = bundle
                return true
            }
            R.id.businessFragment -> {
                binding.linearLayout.currentItem = 3
                val businessFragment = BusinessMainFragment()
                val bundle = Bundle()
                bundle.putString("id",id)
                businessFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                return true
            }
            R.id.profileFragment -> {
                binding.linearLayout.currentItem = 4
                val profileFragment = ProfileFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                bundle.putString("userType", userType)
                bundle.putString("userDept", userDept)
                bundle.putString("passwd", passwd)
                bundle.putInt("userMedal", userMedal)
                bundle.putString("nickname", nickname)
                Log.d("profilewowowowo", "$id")
                Log.d("wowowowo", "$userType")
                Log.d("wowowowo", "$userDept")
                profileFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
                return true
            }
            else -> {
                return false
            }
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

