package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class BusinessManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)

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