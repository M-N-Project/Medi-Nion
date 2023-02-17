package com.example.medi_nion

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.business_home.*

class BusinessBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_board_recom)

//        val businessBoard : List<BusinessBoardItem> = listOf(
//            BusinessBoardItem("title", "2023년 2월 15일 오전 11시 10분",
//                BitmapFactory.decodeResource(resources, R.drawable.bell_icon), "이것은 내용입니다", 1, 1),
//            BusinessBoardItem("title1", "2023년 2월 16일 오전 11시 10분",
//                BitmapFactory.decodeResource(resources, R.drawable.logo), "이것은 내용1입니다", 1, 1)
//        )

//        val adapter = BusinessRecyclerAdapter(businessBoard)
//        BusinessBoardRecyclerView.adapter = adapter
    }
}