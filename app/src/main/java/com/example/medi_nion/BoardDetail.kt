package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_comment_item.*
import kotlinx.android.synthetic.main.board_detail.*
import kotlinx.android.synthetic.main.board_detail.CommentRecyclerView
import kotlinx.android.synthetic.main.comment_comment_detail.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.LinkedList

var Comment_items =ArrayList<CommentItem>()
var Commentadapter = CommentListAdapter(Comment_items)
val viewModel: CommentViewModel = CommentViewModel()

class BoardDetail : AppCompatActivity() {

    var isDefault = false //좋아요 빈하트, 채운하트 구분하기위함

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)


        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
        val Like_Btn = findViewById<CheckBox>(R.id.imageView_like2) //좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //좋아요 숫자 부분
        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2) //북마크 imageview 부분
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2) //북마크 count 부분


        //val scroll = findViewById<NestedScrollView>(R.id.scroll).isNestedScrollingEnabled


        val manager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        fetchData()
        fetchLikeData()
        fetchBookmarkData()

        //Board.kt에서 BoardDetail.kt로 데이터 intent
        val board = intent.getStringExtra("board")
        val itemPos = intent.getIntExtra("itemIndex", -1)
        var id = intent.getStringExtra("id") //접속한 유저의 아이디
        var writerId = intent.getStringExtra("writerId")
        val post_num = intent?.getIntExtra("num", 0).toString()
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val time = intent.getStringExtra("time")
        val image = intent.getStringExtra("image")

        val heart = intent?.getStringExtra("heart")

        // 게시물 옵션 버튼.
        val optionBtn = findViewById<Button>(R.id.moreBtn)
        var optionRadio = findViewById<RadioGroup>(R.id.optionRadioGroup)
        if(id==writerId){
            optionBtn.visibility = View.VISIBLE

            optionBtn.setOnClickListener{
                if(optionRadio.visibility == View.GONE)
                    optionRadio.visibility = View.VISIBLE
                else optionRadio.visibility = View.GONE
            }

            val option_updatePost = findViewById<RadioButton>(R.id.postUpdate_RadioBtn)
            option_updatePost.setOnClickListener{
                // 글쓰기 화면으로 이동
                val board = intent.getStringExtra("board")
                val intent = Intent(applicationContext, BoardWrite::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", board)
                intent.putExtra("post_num", post_num)
                intent.putExtra("title", title)
                intent.putExtra("content", content)
                intent.putExtra("image", image)
                intent.putExtra("update", 1)

                startActivity(intent)
            }
            val option_deletePost = findViewById<RadioButton>(R.id.postDelete_RadioBtn)
            option_deletePost.setOnClickListener{
                // 지우기.
                PostDeleteRequest()
            }
        }

        val title_textView = findViewById<TextView>(R.id.textView_title)
        val content_textView = findViewById<TextView>(R.id.textView_content)
        val time_textView = findViewById<TextView>(R.id.textView_time)

//        textView_num.setText(num)
        title_textView.setText(title)
        content_textView.setText(content)
        time_textView.setText(time)

        if (image != null) {
            if (image.isNotEmpty()) {
                var postImg = findViewById<ImageView>(R.id.post_imgView)
                postImg.visibility = View.VISIBLE
                val bitmap: Bitmap? = StringToBitmaps(image)
                postImg.setImageBitmap(bitmap)
            } else {
                var postImg = findViewById<ImageView>(R.id.post_imgView)
                postImg.visibility = View.GONE
            }
        }

        val Commentadapter = CommentListAdapter(Comment_items)
        CommentRecyclerView.adapter = Commentadapter

        Comment_Btn.setOnClickListener {
            CommentRequest()
            manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
            Comment_editText.setText(null) //댓글입력창 clear
        }

        Like_Btn.setOnClickListener {
            if (Like_Btn.isChecked) {
                LikeRequest(true)
                Like_count.text = (Like_count.text.toString().toInt() + 1).toString()
            } else {
                LikeRequest(false)
                Like_count.text = (Like_count.text.toString().toInt() - 1).toString()
            }
        }



            Book_Btn.setOnClickListener {
                if (Book_Btn.isChecked) {
                    BookRequest(true)
                    Book_count.text = (Book_count.text.toString().toInt() + 1).toString()
                } else {
                    BookRequest(false)
                    Book_count.text = (Book_count.text.toString().toInt() - 1).toString()
                }
            }
        }

    fun PostDeleteRequest(){
        var id = intent?.getStringExtra("id").toString() //user id 받아오기, 내가 좋아요 한 글 보기 위함
        val board = intent.getStringExtra("board").toString()
        val post_num = intent?.getIntExtra("num", 0).toString()

        val urlDelete = "http://seonho.dothome.co.kr/postDelete.php"

        val request = Login_Request(
            Request.Method.POST,
            urlDelete,
            { response ->
                if (!response.equals("update fail")) {
                    Log.d("sadsaf", response)
                    Toast.makeText(
                        baseContext,
                        String.format("게시물이 삭제되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    var intent = Intent(applicationContext, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("board", board)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        applicationContext,
                        "lion heart fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "board" to board,
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    fun LikeRequest(flag: Boolean) {
        var id = intent?.getStringExtra("id").toString()
        val board = intent.getStringExtra("board").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/Heart.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        var heartFlag = ""
        var like_count = findViewById<TextView>(R.id.textView_likecount2).text.toString()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Like fail")) {

                    if(flag == true) heartFlag = "heartUP"
                    else heartFlag = "heartDOWN"

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseLike ->
                            if (!responseLike.equals("update fail")) {
                                post_num = responseLike.toString()
                                like_count = responseLike.toString()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "lion heart fail",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num,
                            "flag" to heartFlag
                        )
                    )

                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestCnt)

                    Toast.makeText(
                        baseContext,
                        String.format("좋아요가 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "bookmark success",
                        "$id, $post_num"
                    )

                } else {

                    Toast.makeText(
                        applicationContext,
                        "like fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("bookmark Request", "${board}, ${post_num}, ${flag.toString()}")

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "board" to board,
                "id" to id,
                "post_num" to post_num,
                "flag" to flag.toString()
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    fun fetchLikeData() {
        val url = "http://seonho.dothome.co.kr/Heart_list.php"
        var post_num = intent?.getIntExtra("num", 0).toString()
        var id = intent?.getStringExtra("id").toString()

        val Like_Btn = findViewById<CheckBox>(R.id.imageView_like2)
        val Like_count = findViewById<TextView>(R.id.textView_likecount2)

        val jsonArray: JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (response != "no Heart") {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {

                        val item = jsonArray.getJSONObject(i)

                        val heartId = item.getString("id")
                        val heart_num = item.getString("count")

                        Like_count.text = heart_num
                        if(heartId == id){
                            Like_Btn.isChecked = true
                            break
                        }
                    }

                    Log.d("like fetch", id.toString())

                }

            }, { Log.d("like Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun BookRequest(flag : Boolean) {
        var id = intent?.getStringExtra("id").toString()
        val board = intent.getStringExtra("board").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/Bookmark.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        var bookmarkFlag = ""
        var book_count = findViewById<TextView>(R.id.textView_bookmarkcount2).text.toString()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Bookmark fail")) {

                    if(flag == true) bookmarkFlag = "bookmarkUP"
                    else bookmarkFlag = "bookmarkDOWN"

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseBookmark ->
                            if (!responseBookmark.equals("update fail")) {
                                post_num = responseBookmark.toString()
                                book_count = responseBookmark.toString()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "lion heart fail",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num,
                            "flag" to bookmarkFlag
                        )
                    )

                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestCnt)

                    Toast.makeText(
                        baseContext,
                        String.format("북마크가 생성되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "bookmark success",
                        "$id, $post_num"
                    )

                    //fetchBookmarkData()
                } else {

                    Toast.makeText(
                        applicationContext,
                        "Bookmark fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("bookmark Request", "${board}, ${post_num}, ${flag.toString()}")

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "board" to board,
                "id" to id,
                "post_num" to post_num,
                "flag" to flag.toString()
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchBookmarkData() {
        val url = "http://seonho.dothome.co.kr/Bookmark_list.php"
        var post_num = intent?.getIntExtra("num", 0).toString()
        var id = intent?.getStringExtra("id").toString()

        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2)
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2)

        val jsonArray: JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (response != "no Bookmark") {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {

                        val item = jsonArray.getJSONObject(i)

                        val bookmarkId = item.getString("id")
                        val bookmark_num = item.getString("count")

                        Book_count.text = bookmark_num
                        if(bookmarkId == id){
                            Book_Btn.isChecked = true
                            break
                        }
                    }

                    Log.d("bookmark fetch", id.toString())

                }

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun CommentRequest() {
        var id = intent?.getStringExtra("id").toString()
        var board = intent?.getStringExtra("board").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()

        val url = "http://seonho.dothome.co.kr/Comment.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val comment_time = current.format(formatter)

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment fail")) {

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseComment ->
                            if (!responseComment.equals("update fail")) {
                                post_num = responseComment.toString()

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "lion heart fail",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
//                            "count" to heart_count,
                            "board" to board,
                            "post_num" to post_num,
                            "flag" to "commentUP" // 댓글 삭제 기능 구현 후 commentUP/ commentDOWN으로 나눌 예정.
                        )
                    )

                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestCnt)

                    Toast.makeText(
                        baseContext,
                        String.format("댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "comment success",
                        "$id, $post_num, $comment, $comment_time"
                    )

                    fetchData()

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
                "board" to board,
                "post_num" to post_num,
                "comment" to comment,
                "comment_time" to comment_time
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchData() {
        val url = "http://seonho.dothome.co.kr/Comment_list.php"
        val urlDetail = "http://seonho.dothome.co.kr/commentInfoDetail.php"
        val urlCommentHeart = "http://seonho.dothome.co.kr/commentHeart.php"
        var post_num = intent?.getIntExtra("num", 0).toString()
        var board = intent?.getStringExtra("board").toString()

        Comment_items.clear()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (response != "no Comment") {
                    val jsonArray = JSONArray(response)

                    val comment_count = jsonArray.length()
                    findViewById<TextView>(R.id.textView_commentcount2).text =
                        comment_count.toString()

                    var comment_user = HashMap<String, Int>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val id = item.getString("id")
                        if (!comment_user.containsKey(id)) comment_user[id] =
                            comment_user.size + 1
                    }

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)


                        val id = item.getString("id")
                        val comment = item.getString("comment")
                        val comment_time = item.getString("comment_time")
                        val comment_num = comment_user[id]!!

                        val commentItem = CommentItem(id, comment, comment_num, comment_time)

                        Comment_items.add(commentItem)


                        Log.d("commmentItem", "$id, $post_num, $comment, $comment_num, $comment_time")

                        //viewModel.setItemList(Comment_items)
                        CommentRecyclerView.adapter = Commentadapter

                        var userId = intent?.getStringExtra("id").toString()
                        var detailId: String = ""
                        var detailComment: String = ""
                        var detailCommentTime: String = ""

                        items.clear()

                        Commentadapter.setOnItemClickListener(object :
                            CommentListAdapter.OnItemClickListener {
                            override fun onItemClick(v: View, data: CommentItem, pos: Int) {
                                val request = Login_Request(
                                    Request.Method.POST,
                                    urlDetail,
                                    { response ->
                                        items.clear()
//                                    for (i in jsonArray.length()-1  downTo  0) {
                                        val jsonObject = JSONObject(response)

                                        detailId = jsonObject.getString("id")
                                        detailComment = jsonObject.getString("comment")
                                        detailCommentTime = jsonObject.getString("comment_time")

                                        val intent = Intent(
                                            applicationContext,
                                            CommentDetail::class.java
                                        )
                                        intent.putExtra("comment_num", data.comment_num)
                                        intent.putExtra("id", userId)
                                        intent.putExtra("board", board)
                                        intent.putExtra("comment", data.comment)
                                        intent.putExtra("comment_time", data.comment_time)
                                        intent.putExtra("post_num", post_num)

                                        startActivity(intent)
                                    },
                                    { Log.d("Comment failed", "error......${error(applicationContext)}") },
                                    hashMapOf(
                                        "post_num" to post_num,
                                        "board" to board,
                                        "comment" to comment
                                    )
                                )
                                val queue = Volley.newRequestQueue(applicationContext)
                                queue.add(request)
                            }

                            //댓글 좋아요 눌렀을때.
                            override fun onItemHeart(v: View, data: CommentItem, pos: Int) {
                                Log.d("commentHart", "sfjksd;f")
                                val commentHeart = v.findViewById<CheckBox>(R.id.imageView_comment_like)
                                val commentHeartCnt = v.findViewById<TextView>(R.id.comment_heart_count)
                                var commentFlag = true
                                if (commentHeart.isChecked) {
                                    commentHeartCnt.text = (commentHeartCnt.text.toString().toInt() + 1).toString()
                                } else {
                                    commentFlag = false
                                    commentHeartCnt.text = (commentHeartCnt.text.toString().toInt() - 1).toString()
                                }

                                val request = Login_Request(
                                    Request.Method.POST,
                                    urlCommentHeart,
                                    { response ->
                                        Log.d("commentHart", response)
//                                        items.clear()
//                                    for (i in jsonArray.length()-1  downTo  0) {
//                                        val jsonObject = JSONObject(response)

//                                        detailId = jsonObject.getString("id")
//                                        detailComment = jsonObject.getString("comment")
//                                        detailCommentTime = jsonObject.getString("comment_time")

//                                        val intent = Intent(
//                                            applicationContext,
//                                            CommentDetail::class.java
//                                        )
//                                        intent.putExtra("comment_num", data.comment_num)
//                                        intent.putExtra("id", userId)
//                                        intent.putExtra("board", board)
//                                        intent.putExtra("comment", data.comment)
//                                        intent.putExtra("comment_time", data.comment_time)
//                                        intent.putExtra("post_num", post_num)

//                                        startActivity(intent)
                                    },
                                    { Log.d("Comment failed", "error......${error(applicationContext)}") },
                                    hashMapOf(
                                        "id" to id,
                                        "post_num" to post_num,
                                        "board" to board,
                                        "comment_num" to pos.toString(),
                                        "flag" to commentFlag.toString()
                                    )
                                )
                                val queue = Volley.newRequestQueue(applicationContext)
                                queue.add(request)
                            }
                        })
                    }
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "post_num" to post_num,
                "board" to board
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    // String -> Bitmap 변환
    fun StringToBitmaps(image: String?): Bitmap? {
        try {
            val encodeByte = Base64.decode(image, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            return bitmap
        } catch (e: Exception) {
            e.message
            return null
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}

