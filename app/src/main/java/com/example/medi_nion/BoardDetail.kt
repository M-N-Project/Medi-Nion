package com.example.medi_nion

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_detail.*
import org.json.JSONArray

var Comment_items =ArrayList<CommentItem>()
lateinit var Commentadapter : CommentListAdapter

class BoardDetail : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)

        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)

        var manager : InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager //키보드 내리기



        window.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING)
        fetchData()

        //Board.kt에서 BoardDetail.kt로 데이터 intent
        var id = intent.getStringExtra("id")

        var title = intent.getStringExtra("title")
        var content = intent.getStringExtra("content")
        var time = intent.getStringExtra("time")

        var title_textView = findViewById<TextView>(R.id.textView_title)
        var content_textView = findViewById<TextView>(R.id.textView_content)
        var time_textView = findViewById<TextView>(R.id.textView_time)

        title_textView.setText(title)
        content_textView.setText(content)
        time_textView.setText(time)
        //

        val Commentadapter = CommentListAdapter(Comment_items)
        CommentRecyclerView.adapter = Commentadapter


        Comment_Btn.setOnClickListener {
            CommentRequest()
            manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
            Comment_editText.setText(null) //댓글입력창 clear
        }



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

    @SuppressLint("SuspiciousIndentation")
    fun fetchData() {
        val url = "http://seonho.dothome.co.kr/Comment_list.php"
        val jsonArray : JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("comment2", "comment2")
                val jsonArray = JSONArray(response)

                    Log.d("comment3", "comment3")
                    for(i in 1 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val comment = item.getString("comment")
                        val comment_time = item.getString("comment_time")

                        val commentItem = CommentItem(comment, comment_time)

                        Comment_items.add(commentItem)
                        Log.d("comment4", "comment4")
                        val Commentadapter = CommentListAdapter(Comment_items)
                        CommentRecyclerView.adapter = Commentadapter
                        Log.d("comment5", "comment5")

                        //댓글 아이템 하나 누르면
                        var manager : InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
                        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
                        val comment2_linearLayout = findViewById<LinearLayout>(R.id.comment2_linearLayout)

                        Commentadapter.setOnItemClickListener(object : CommentListAdapter.OnItemClickListener {
                            override fun onItemClick(v: View, data: CommentItem, pos: Int) {
                                Toast.makeText(applicationContext, String.format("대댓글 ? "), Toast.LENGTH_SHORT).show()
                                Comment_editText.requestFocus()
                                manager.showSoftInput(Comment_editText, InputMethodManager.SHOW_IMPLICIT) //키보드 올리기
                                Log.d("????", "123")

                                Comment_Btn.setOnClickListener {
                                    Toast.makeText(applicationContext, String.format("우왕"), Toast.LENGTH_SHORT).show()
                                    //comment2_linearLayout.visibility = View.VISIBLE
                                    //Log.d("comment2", "layout????")
                                }

                            }
                        })
                    }

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}
