package com.example.medi_nion

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CommentDetail  : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_comment_item)
        val comment_detail_num = findViewById<TextView>(R.id.comment_num)
        val comment_detail_content = findViewById<TextView>(R.id.comment_content)
        val comment_detail_time = findViewById<TextView>(R.id.comment_time)
        val Comment2_editText = findViewById<EditText>(R.id.Comment2_editText)
        val Comment2_Btn = findViewById<Button>(R.id.Comment2_Btn)

        val manager : InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        val comment_num = intent?.getIntExtra("comment_num", 0).toString()
        val comment = intent?.getStringExtra("comment")
        val comment_time = intent?.getStringExtra("comment_time")

        comment_detail_num.setText(comment_num)
        comment_detail_content.setText(comment)
        comment_detail_time.setText(comment_time)

        Comment2_Btn.setOnClickListener {
            if (Comment2_editText.text.toString().isEmpty()) {
                Comment2_Btn.setOnClickListener {
                    Toast.makeText(baseContext, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    Log.d("comment2?", "><><")
                }
            } else {
                Comment2Request()
                manager.hideSoftInputFromWindow(
                    getCurrentFocus()?.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
                ) //Comment버튼 누르면 키보드 내리기
                Comment2_editText.setText(null) //댓글입력창 clear
                Log.d("comment2Request", "wow")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Comment2Request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment_num = findViewById<TextView>(R.id.comment_num).text.toString()
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
                        "$id, $post_num, $comment_num, $comment2, $comment2_time"
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
                "comment_num" to comment_num,
                "comment2" to comment2,
                "comment2_time" to comment2_time
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}
