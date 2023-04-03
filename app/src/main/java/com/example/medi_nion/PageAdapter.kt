package com.example.medi_nion

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<Fragment>): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> return BusinessManageFirstFragment1()
            1 -> return BusinessManageFirstFragment2()
            else -> return BusinessManageFirstFragment3()
        }
    }
}