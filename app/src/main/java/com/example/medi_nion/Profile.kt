package com.example.medi_nion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)


        //버튼 누르면 아래 상세사항 나오기
        val item1 = findViewById<LinearLayout>(R.id.item1_title)
        val item2 = findViewById<LinearLayout>(R.id.item2_title)
        val item3 = findViewById<LinearLayout>(R.id.item3_title)

        item1.setOnClickListener{
            val item1_list = findViewById<LinearLayout>(R.id.item1_list_layout)
            if(item1_list.visibility == View.GONE) item1_list.visibility = View.VISIBLE
            else item1_list.visibility = View.GONE
        }

        item2.setOnClickListener{
            val item2_list = findViewById<LinearLayout>(R.id.item2_list_layout)
            if(item2_list.visibility == View.GONE) item2_list.visibility = View.VISIBLE
            else item2_list.visibility = View.GONE
        }
        item3.setOnClickListener{
            val item3_list = findViewById<LinearLayout>(R.id.item3_list_layout)
            if(item3_list.visibility == View.GONE) item3_list.visibility = View.VISIBLE
            else item3_list.visibility = View.GONE
        }

    }
}