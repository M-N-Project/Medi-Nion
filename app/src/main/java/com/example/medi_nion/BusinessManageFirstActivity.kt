package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley

import com.example.medi_nion.databinding.BusinessCreateHome1Binding
import kotlinx.android.synthetic.main.business_create_home1.*
import org.json.JSONArray

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

        Log.d("0173213", id)
        findViewById<Button>(R.id.createBusinessChan_btn1).setOnClickListener{
            //비즈니스 채널 관리로 넘어가기.
            val request = Login_Request(
                Request.Method.POST,
                urlBusinessProfileInsert,
                { response ->
                    Log.d("bussinesssssss", response.toString())
                    if(!response.equals("business profile insert fail")) {
                        Toast.makeText(this, "비즈니스 채널 프로필 생성 완료", Toast.LENGTH_SHORT).show()


                    } else {
                        Toast.makeText(this, "비즈니스 채널 프로필 생성 실패", Toast.LENGTH_SHORT).show()
                    }

                }, { Log.d("business profile failed", "error......${error(this)}") },
                hashMapOf(
                    "id" to id
                )
            )
            request.retryPolicy = DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)

            val intent = Intent(this, BusinessManageActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("isFirst", true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
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
                0 -> BusinessManageFirstFragment1.newInstance(R.drawable.bf_first1, "나의 비즈니스 프로필", "내 비즈니스와 이웃을 연결하는 방법,", "지금 무료로 시작해보세요!")
                1 -> BusinessManageFirstFragment1.newInstance(R.drawable.ad2, "관심있는 정보를 한눈에", "사용자들의 관심을 끌 수 있도록", "프로필을 채워보세요!")
                else -> BusinessManageFirstFragment1.newInstance(R.drawable.ad2, "단골을 모으고 소식 알리기", "글을 작성하면 구독을 한 사용자의", "홈 피드에 노출이 된답니다~")
            }
        }
    }

}