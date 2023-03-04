package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_detail.*
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray

var Comment_items = ArrayList<CommentItem>()
lateinit var Commentadapter: CommentListAdapter

class BoardDetail : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)

        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
        var manager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager //키보드 내리기

        fetchDataComment()

        val Commentadapter = CommentListAdapter(Comment_items)
        CommentRecyclerView.adapter = Commentadapter

        Comment_Btn.setOnClickListener {
            CommentRequest()
            manager.hideSoftInputFromWindow(
                getCurrentFocus()?.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
            ) //Comment버튼 누르면 키보드 내리기
            Comment_editText.setText(null) //댓글입력창 clear

        }

        /*
        val Bookmark_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2)
        Bookmark_Btn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                // 체크시 > 북마크 등록
                bookmarkCheckedRequest()
            }
            else {
                // 체크 해제 > 북마크 해제
                bookmarkUncheckedRequest()
            }
        }
        */

    }


    fun CommentRequest() {
        var id = intent?.getStringExtra("id").toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()
        val url = "http://seonho.dothome.co.kr/Comment.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment fail")) {
                    comment = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment success",
                        "$id, $comment"
                    )

                } else {

                    Toast.makeText(
                        applicationContext,
                        "댓글을 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "comment" to comment
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchDataComment() {
        val url = "http://seonho.dothome.co.kr/Comment_list.php"
        val jsonArray: JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("comment2", "comment2")
                val jsonArray = JSONArray(response)

                Log.d("comment3", "comment3")
                for (i in 1 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    val comment = item.getString("comment")
                    val comment_time = item.getString("comment_time")

                    val commentItem = CommentItem(comment, comment_time)

                    Comment_items.add(commentItem)
                    Log.d("comment4", "comment4")
                    val Commentadapter = CommentListAdapter(Comment_items)
                    CommentRecyclerView.adapter = Commentadapter
                    Log.d("comment5", "comment5")
                }

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    // 북마크 체크할 때 북마크 생성 함수
    fun bookmarkCheckedRequest() {
        var bookmarkBtn = findViewById<CheckBox>(R.id.checkbox_bookmark2)
        var user_id = intent?.getStringExtra("id").toString()

        val url = "http://seonho.dothome.co.kr/Bookmark.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment fail")) {
                    //comment = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("북마크가 생성되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment success",
                        ""
                    )

                } else {

                    Toast.makeText(
                        applicationContext,
                        "북마크를 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "user_id" to user_id,
                //"post_id" to post_id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    // 북마크 버튼 해제시 북마크 삭제 함수
    fun bookmarkUncheckedRequest() {

    }

    // 북마크 체크/언체크 상태 조회 후 가져오기
    fun fetchDataBookmark() {

    }
}
