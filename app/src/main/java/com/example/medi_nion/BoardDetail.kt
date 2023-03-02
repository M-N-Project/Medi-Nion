package com.example.medi_nion

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_detail.*
import org.json.JSONArray

var Comment_items =ArrayList<CommentItem>()
val Commentadapter = CommentListAdapter(Comment_items)
lateinit var datas : BoardItem

class BoardDetail : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)

        var count = 0
        var isDefault = true //좋아요 빈하트, 채운하트 구분하기위함
        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
        val Like_Btn = findViewById<ImageView>(R.id.imageView_like2) //좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //좋아요 숫자 부분


        val manager : InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        window.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING)
        fetchData()

        //Board.kt에서 BoardDetail.kt로 데이터 intent
        val itemPos = intent.getIntExtra("itemIndex", -1)
        var id = intent.getStringExtra("id")
        val num = intent?.getIntExtra("num", 0).toString()
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val time = intent.getStringExtra("time")

        val textView_num = findViewById<TextView>(R.id.textView_Num)
        val title_textView = findViewById<TextView>(R.id.textView_title)
        val content_textView = findViewById<TextView>(R.id.textView_content)
        val time_textView = findViewById<TextView>(R.id.textView_time)
        var comment_count = 0
        val comment_num = findViewById<TextView>(R.id.comment_num)

        textView_num.setText(num)
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
            comment_count++
            comment_num.text = comment_count.toString()
        }




        Like_Btn.setOnClickListener {

            Log.d("asdfasdf", "clicked!")
            //좋아요 눌렀을때,,
            LikeRequest()
            isDefault = !isDefault

            if(isDefault) {
                count--
                Like_count.text = count.toString()
                Like_Btn.setImageResource(R.drawable.favorite_border)
            }
            else {
                count++
                Like_count.text = count.toString()
                Like_Btn.setImageResource(R.drawable.favorite_fill)
            }
        }
    }

    fun LikeRequest() {  //좋아요 DB연동중
        var id = intent?.getStringExtra("id").toString()
        var heart = findViewById<ImageView>(R.id.imageView_like2).toString() //좋아요 클릭만 가져오게 하기(익명이라 누가 눌렀는진 의미 없을듯,,)
        val url = "http://seonho.dothome.co.kr/Heart.php"

        val request = Login_Request (
            Request.Method.POST,
            url,
            { response ->
                if(!response.equals("Like fail")) {
                    heart = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("조아요 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "lion heart",
                        "$id, $heart"
                    )
                } else {
                    Toast.makeText(
                        applicationContext,
                        "lion heart fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "comment" to heart
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }



    fun CommentRequest() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment_num = findViewById<TextView>(R.id.comment_num).text.toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()
        val url = "http://seonho.dothome.co.kr/Comment.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment fail")) {
                    comment = response.toString()
                    comment_num = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment success",
                        "$id, $post_num, $comment_num, $comment"
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
                "comment_num" to comment_num,
                "post_num" to post_num,
                "comment" to comment
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun Comment2Request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment_num = findViewById<TextView>(R.id.comment_num).text.toString()
        var comment2 = findViewById<EditText>(R.id.Comment_editText).text.toString()
        val url = "http://seonho.dothome.co.kr/Comment2.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment2 fail")) {
                    comment2 = response.toString()

                    Log.d("MMMM", response)
                    Toast.makeText(
                        baseContext,
                        String.format("대댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment2 success",
                        "$id, $post_num, $comment_num, $comment2"
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
                "comment2" to comment2
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
                Comment_items.clear()
                Log.d("comment2", "comment2")
                val jsonArray = JSONArray(response)

                    Log.d("comment3", "comment3")
                    for(i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        Log.d("4444445555", item.toString())
                        val comment = item.getString("comment")
                        val comment_time = item.getString("comment_time")
                        val comment_num = item.getInt("comment_num")

                        val commentItem = CommentItem(comment, comment_num, comment_time)

                        Comment_items.add(commentItem)
                        Log.d("comment4", "comment4")
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
                                manager.showSoftInput(Comment_editText, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) //키보드 올리기
                                Log.d("????", "123")

                                Comment_Btn.setOnClickListener {
                                    Toast.makeText(applicationContext, String.format("우왕"), Toast.LENGTH_SHORT).show()
                                    //comment2_linearLayout.visibility = View.VISIBLE
                                    //Log.d("comment2_linear", "layout????")
                                    Comment2Request()
                                    Log.d("9999", "9999")
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
