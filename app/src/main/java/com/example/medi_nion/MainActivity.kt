package com.example.medi_nion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.medi_nion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

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
        val infomap = HashMap<String, String>()
        infomap.put("id", intent.getStringExtra("id").toString())
        infomap.put("nickname", intent.getStringExtra("nickname").toString())
        infomap.put("userType", intent.getStringExtra("userType").toString())
        infomap.put("userDept", intent.getStringExtra("userDept").toString())
        infomap.put("passwd", intent.getStringExtra("passwd").toString())
        infomap.put("userMedal", intent.getIntExtra("userMedal", 0).toString())

        linearLayout.isUserInputEnabled = true //false시 스크롤 막힘

        //여기서 linearLayout은 ViewPager2, id바꿀시 혹시 에러날까봐 냅둠
        binding.linearLayout.adapter = ViewPagerAdapter2_Main(this, infomap)

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

        when(item.itemId){
            R.id.homeFragment -> {
                binding.linearLayout.currentItem = 0
                return true
            }
            R.id.menuFragment -> {
                binding.linearLayout.currentItem = 1
                return true
            }
            R.id.scheduleFragment -> {
                binding.linearLayout.currentItem = 2
                return true
            }
            R.id.businessFragment -> {
                binding.linearLayout.currentItem = 3
                return true
            }
            R.id.profileFragment -> {
                binding.linearLayout.currentItem = 4
                return true
            }
            else -> {
                return false
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