package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley

import com.example.medi_nion.databinding.BusinessCreateHome1Binding
import kotlinx.android.synthetic.main.business_create_home1.*

class BusinessManageFirstActivity : AppCompatActivity() {

    private var vpAdapter: FragmentStatePagerAdapter?= null
    private var haveChan = false
    private lateinit var binding: BusinessCreateHome1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_create_home1)

        vpAdapter = CustomPagerAdapter(supportFragmentManager)
        viewPager.adapter = vpAdapter

        indicator1.setViewPager(viewPager)

        val id: String = this.intent.getStringExtra("id").toString()
        val urlBusinessProfileInsert = "http://seonho.dothome.co.kr/BusinessProfileInsert.php"

        findViewById<Button>(R.id.createBusinessChan_btn1).setOnClickListener{
            val intent = Intent(this, BusinessManageEdit::class.java)
            intent.putExtra("id", id)
            intent.putExtra("isFirst", true)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
            finish()
            startActivity(intent)

        }
    }

    class CustomPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val PAGENUMBER = 3

        override fun getCount(): Int {
            return PAGENUMBER
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> BusinessManageFirstFragment1.newInstance(R.drawable.b_first, "나의 비즈니스 프로필", "내 비즈니스와 이웃을 연결하는 방법,", "지금 무료로 시작해보세요!")
                1 -> BusinessManageFirstFragment1.newInstance(R.drawable.b_second, "관심있는 정보를 한눈에", "사용자들의 관심을 끌 수 있도록", "프로필을 채워보세요!")
                else -> BusinessManageFirstFragment1.newInstance(R.drawable.b_third, "단골을 모으고 소식 알리기", "글을 작성하면 구독을 한 사용자의", "홈 피드에 노출이 된답니다~")
            }
        }
    }

}