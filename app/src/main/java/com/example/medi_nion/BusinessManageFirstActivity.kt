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

        val url = "http://seonho.dothome.co.kr/businessChanUpdate.php"
        request_userInfo()

        findViewById<Button>(R.id.createBusinessChan_btn).setOnClickListener{
            //비즈니스 채널 생성 후 -> User 테이블의 businessChan = 1로 수정
            val request = Login_Request(
                Request.Method.POST,
                url,
                { response ->
                    if(response!="business Chan update fail"){
                        //비즈니스 채널 관리로 넘어가기.
                        val intent = Intent(this, BusinessManageActivity::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("isFirst", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                        finish()
                        startActivity(intent)
                    }

                }, { Log.d("businessChan failed", "error......${error(applicationContext)}") },
                hashMapOf(
                    "id" to id.toString()
                )
            )
            val queue = Volley.newRequestQueue(applicationContext)
            queue.add(request)

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