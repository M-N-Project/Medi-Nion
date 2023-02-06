package com.example.medi_nion

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.medi_nion.databinding.ActivityMainBinding
import com.example.medi_nion.databinding.HospitalProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

class HospitalProfile : AppCompatActivity() {

    private lateinit var binding: HospitalProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HospitalProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {
        //ViewPager2 Adapter 셋팅
        var viewPager2Adatper = ViewPager2Adapter(this)
        viewPager2Adatper.addFragment(EmployeeInfoFragment())
        viewPager2Adatper.addFragment(HospitalInfoFragment())
        viewPager2Adatper.addFragment(HospitalReviewFragment())

        //Adapter 연결
        binding.vpViewpagerMain.apply {
            adapter = viewPager2Adatper

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.e("YMC", "ViewPager position: $position")
                    super.onPageSelected(position)
                }
            })
        }
        //ViewPager, TabLayout 연결
        TabLayoutMediator(binding.tlNavigationView, binding.vpViewpagerMain) { tab, position ->
            Log.e("YMC", "ViewPager position: $position")
            when (position) {
                0 -> tab.text = "채용 정보"
                1 -> tab.text = "병원 정보"
                2 -> tab.text = "리뷰"
            }
        }.attach()
    }
}