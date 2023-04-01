package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class BusinessManageFirstActivity : AppCompatActivity() {

    private var haveChan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_create_home)
        val id: String? = this.intent.getStringExtra("id")

        findViewById<Button>(R.id.createBusinessChan_btn).setOnClickListener{
            //비즈니스 채널 관리로 넘어가기.
            val intent = Intent(this, BusinessManageActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("isFirst", true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
            finish()
            startActivity(intent)

        }
    }

}