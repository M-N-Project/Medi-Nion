package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_detail.*
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.comment_comment_detail.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

var CommentDetail_items =ArrayList<CommentDetailItem>()
var CommentDetailadapter = CommentDetailListAdapter(CommentDetail_items)
val DetailviewModel: CommentDetailViewModel = CommentDetailViewModel()

class CommentDetail  : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_comment_detail)

        val comment_detail_num = findViewById<TextView>(R.id.comment_detail_num)
        val comment_detail_content = findViewById<TextView>(R.id.comment_detail_content)
        val comment_detail_time = findViewById<TextView>(R.id.comment_detail_time)
        val Comment2_editText = findViewById<EditText>(R.id.Comment2_editText)
        val Comment2_Btn = findViewById<Button>(R.id.Comment2_Btn)

        val manager : InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        fetchData()

        val comment_num = intent?.getIntExtra("comment_num", 0).toString()
        val comment = intent?.getStringExtra("comment")
        val comment_time = intent?.getStringExtra("comment_time")
        val post_num = intent?.getStringExtra("post_num")
        val comment_id = intent?.getStringExtra("id")

        Log.d("123456", "$comment_id")


        comment_detail_num.setText(comment_num)
        comment_detail_content.setText(comment)
        comment_detail_time.setText(comment_time)

        val CommentDetailadapter = CommentDetailListAdapter(CommentDetail_items)
        CommentRecyclerView2.adapter = CommentDetailadapter


        Comment2_Btn.setOnClickListener {
            Comment2Request()
            manager.hideSoftInputFromWindow(
                getCurrentFocus()?.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
            ) //Comment버튼 누르면 키보드 내리기
            Comment2_editText.setText(null) //댓글입력창 clear
            Log.d("comment2Request", "wow")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Comment2Request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getStringExtra("post_num").toString()
        var comment2_num = 1
        var comment2 = findViewById<EditText>(R.id.Comment2_editText).text.toString()

        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val comment2_time = current.format(formatter)

        val url = "http://seonho.dothome.co.kr/Comment2.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment2 fail")) {

                    Log.d("comment2", response)

                    Toast.makeText(
                        baseContext,
                        String.format("대댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment2 success",
                        "$id, $post_num, $comment2_num, $comment2, $comment2_time"
                    )

                } else {

                    Toast.makeText(
                        applicationContext,
                        "대댓글을 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment2 Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "post_num" to post_num,
                "comment_num" to comment2_num.toString(),
                "comment2" to comment2,
                "comment2_time" to comment2_time
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchData() {
        var id = intent.getStringExtra("id")
        val comment_num = intent?.getIntExtra("comment_num", 0).toString()
        val post_num = intent?.getStringExtra("post_num").toString()
        val urlDetail = "http://seonho.dothome.co.kr/Comment2_list.php"
        val jsonArray : JSONArray

        Log.d("????", "????")
        val request = Login_Request(
            Request.Method.POST,
            urlDetail,
            { response ->
                CommentDetail_items.clear()
                Log.d(";;;;", response)
                if (response != "no Comment2") {

                    Log.d("LLLL", "::::")

                    val jsonArray = JSONArray(response)

                    var comment_user = HashMap<String, Int>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val id = item.getString("id")
                        if (!comment_user.containsKey(id)) comment_user[id] =
                            comment_user.size + 1
                    }

                    Log.d("****", "???")

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val comment2 = item.getString("comment2")
                        val comment2_time = item.getString("comment2_time")
                        val comment2_num = comment_user[id]!!

                        val commentDetailItem =
                            CommentDetailItem(comment2, comment2_num, comment2_time)

                        CommentDetail_items.add(commentDetailItem)


                        Log.d(
                            "commmentDetailItem",
                            "$comment_num, $comment2, $comment2_num, $comment2_time"
                        )

                        //viewModel.setItemList(Comment_items)
                        CommentRecyclerView2.adapter = CommentDetailadapter
                    }
                }
            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "comment_num" to comment_num,
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }
}
