package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.business_home.*

class BusinessManageActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)

//        val businessBoard : ArrayList<BusinessBoardItem> = listOf(
//             BusinessBoardItem("title", "2023년 2월 15일 오전 11시 10분",
//                BitmapFactory.decodeResource(resources, R.drawable.bell_icon), "이것은 내용입니다", 1, 1),
//            BusinessBoardItem("title1", "2023년 2월 16일 오전 11시 10분",
//                BitmapFactory.decodeResource(resources, R.drawable.logo), "이것은 내용1입니다", 1, 1)
//        ) //businessBoard end
        //일단 더미데이터, db 연동해야함

        val businessBoard = ArrayList<BusinessBoardItem>()
        businessBoard.add(BusinessBoardItem(getDrawable(R.drawable.business_profile_img)!!, "개강전 이벤트!!", "2023년 2월 15일 오후 1시 30분",
        "이것은 내용입니다. 약사세요~ 줄바꿈도 해야한답니다", 1, 2))

        businessBoard.add(BusinessBoardItem(getDrawable(R.drawable.business_profile_img)!!, "1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))

        //이벤트 연결중,,,
        val adapter = BusinessRecyclerAdapter(businessBoard)
        BusinessBoardRecyclerView.adapter = adapter

        var write = findViewById<Button>(R.id.write_btn)
        var profileImg = findViewById<ImageView>(R.id.profileImg)
        var backgroundImg = findViewById<ImageView>(R.id.backgroundImg)

        write.setOnClickListener {
            var newIntent = Intent(this, BusinessWriting::class.java)
            startActivity(newIntent)
        }

        profileImg.setOnClickListener {
            //프로필 이미지 수정하게 하는,,
            Toast.makeText(this, "동그란 맘속에 피어난 how is the life", Toast.LENGTH_SHORT).show()
        }

        backgroundImg.setOnClickListener {
            //배경 이미지 수정하게 하는,,
            Toast.makeText(this, "사람 찾아 인생을 찾아~", Toast.LENGTH_SHORT).show()
        }
    }
}