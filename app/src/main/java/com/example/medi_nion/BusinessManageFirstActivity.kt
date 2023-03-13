package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley


class BusinessManageFirstActivity : AppCompatActivity() {

    private var haveChan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)
        val id: String? = this.intent.getStringExtra("id")

        request_userInfo()

        if (haveChan == true) {
            val intentToBC = Intent(this, BusinessManageActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        } else {
            // 시작하기 버튼 누르고 진행.
        }
    }



    fun request_userInfo() {
        var id = intent.getStringExtra("id")!!
        val urlBoardInfo = "http://seonho.dothome.co.kr/BusinessChanInfo.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoardInfo,
            { response ->
                haveChan = response == "1"

            }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}