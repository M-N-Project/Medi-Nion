package com.example.medi_nion

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.business_home.*

class BusinessManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)

        val businessBoard : List<BusinessBoardItem> = listOf( //일단 더미데이터, db 연동해야함
            BusinessBoardItem("title", "2023년 2월 15일 오전 11시 10분",
                BitmapFactory.decodeResource(resources, R.drawable.bell_icon), "이것은 내용입니다", 1, 1),
            BusinessBoardItem("title1", "2023년 2월 16일 오전 11시 10분",
                BitmapFactory.decodeResource(resources, R.drawable.logo), "이것은 내용1입니다", 1, 1)
        )

        val adapter = BusinessAdapter(businessBoard)
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
        }

        backgroundImg.setOnClickListener {
            //배경 이미지 수정하게 하는,,
        }
    }
}