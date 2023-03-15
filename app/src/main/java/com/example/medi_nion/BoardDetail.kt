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
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.state.ToggleableState
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_detail.*
import kotlinx.android.synthetic.main.board_detail.CommentRecyclerView
import kotlinx.android.synthetic.main.comment_comment_detail.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

        var count = 0
        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
        val Like_Btn = findViewById<ImageView>(R.id.imageView_like2) //좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //좋아요 숫자 부분
        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2) //북마크 imageview 부분
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2) //북마크 count 부분

        //val scroll = findViewById<NestedScrollView>(R.id.scroll).isNestedScrollingEnabled



        val manager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        fetchData()
        fetchLikeData()

        //Board.kt에서 BoardDetail.kt로 데이터 intent
        val board = intent.getStringExtra("board")
        val itemPos = intent.getIntExtra("itemIndex", -1)
        var id = intent.getStringExtra("id")
        val post_num = intent?.getIntExtra("num", 0).toString()
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val time = intent.getStringExtra("time")
        val image = intent.getStringExtra("image")

        val heart = intent?.getStringExtra("heart")

        val title_textView = findViewById<TextView>(R.id.textView_title)
        val content_textView = findViewById<TextView>(R.id.textView_content)
        val time_textView = findViewById<TextView>(R.id.textView_time)
        val comment_num = findViewById<TextView>(R.id.comment_num)


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
//            comment_num.text = comment_count.toString()
        }



            Like_Btn.setOnClickListener {
                //좋아요 눌렀을때,,

                //likeRequest()

                isDefault = !isDefault

                if (isDefault) { // 좋아요.
                    val likecnt = findViewById<TextView>(R.id.textView_likecount2).text.toString().toInt() + 1
                    findViewById<TextView>(R.id.textView_likecount2).text = likecnt.toString()
                    Like_Btn.setImageResource(R.drawable.favorite_fill)
                    LikeRequest(isDefault.toString())

                } else { //좋아요 취소
                    val likecnt = findViewById<TextView>(R.id.textView_likecount2).text.toString().toInt() - 1
                    findViewById<TextView>(R.id.textView_likecount2).text = likecnt.toString()
                    Like_Btn.setImageResource(R.drawable.favorite_border)
                    LikeRequest(isDefault.toString())
                }

            }

//            Like_Btn.setOnClickListener {
////                if(Like_Btn.isChecked()) {
////
////                }
////            }



            Book_Btn.setOnClickListener {
                if (Book_Btn.isChecked()) {
                    Book_Create_request()
                } else {
                    Book_Delete_request()
                }
            }
        }


    fun LikeRequest(flag : String) {  //좋아요 DB연동중
        var id = intent?.getStringExtra("id").toString() //user id 받아오기, 내가 좋아요 한 글 보기 위함
        val board = intent.getStringExtra("board").toString()
        var post_num = intent?.getIntExtra("num", 0).toString() //게시물 num id 받아오기, 게시물 좋아요 개수 구분하기 위함
        var heart =
            findViewById<ImageView>(R.id.imageView_like2).toString() //좋아요 클릭만 가져오게 하기(익명이라 누가 눌렀는진 의미 없을듯,,)
        var heart_count = findViewById<TextView>(R.id.textView_likecount2).text.toString()
        val url = "http://seonho.dothome.co.kr/Heart.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Like fail")) {
                    var heartFlag = ""

                    if(flag == "true"){ //좋아요 +1
                        heartFlag = "heartUP"
                    }
                    else if(flag == "false"){ //좋아요 -1
                        heartFlag = "heartDOWN"
                    }

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseLike ->
                            if (!responseLike.equals("update fail")) {
                                heart_count = responseLike.toString()
                                post_num = responseLike.toString()

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
                        String.format("조아요 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
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
                "board" to board,
                //"heart_count" to heart, //지우기
                "heart" to heart_count,
                "post_num" to post_num,
                "flag" to flag
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun Book_Delete_request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/BookmarkDelete.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Bookmark fail")) {

                    var bookmarkFlag = "bookmarkDOWN"

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseBookmark ->
                            if (!responseBookmark.equals("update fail")) {
                                post_num = responseBookmark.toString()

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
                            "post_num" to post_num,
                            "flag" to bookmarkFlag
                        )
                    )

                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestCnt)

                    Toast.makeText(
                        baseContext,
                        String.format("북마크가 해제되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "bookmark delete success",
                        "$id, $post_num"
                    )

                    //fetchBookmarkData()
                } else {

                    Toast.makeText(
                        applicationContext,
                        "북마크를 해제할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "post_num" to post_num,
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun Book_Create_request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/Bookmark.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Bookmark fail")) {
                    var bookmarkFlag = "bookmarkUP"

                    val requestCnt = Login_Request(
                        Request.Method.POST,
                        urlUpdateCnt,
                        { responseBookmark ->
                            if (!responseBookmark.equals("update fail")) {
                                post_num = responseBookmark.toString()

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
                        "북마크를 생성할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "post_num" to post_num,
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchBookmarkData() {
        val url = "http://seonho.dothome.co.kr/Comment_list.php"
        var post_num = intent?.getIntExtra("num", 0).toString()
        val jsonArray: JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                Comment_items.clear()
                if (response != "no Comment") {
                    val jsonArray = JSONArray(response)

                    var comment_user = HashMap<String, Int>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        var id = item.getString("id")
                        if (!comment_user.containsKey(id)) comment_user[id] =
                            comment_user.size + 1
                    }

                    for (i in 0 until jsonArray.length()) {

                        val item = jsonArray.getJSONObject(i)

                        val id = item.getString("id")
                        val comment = item.getString("comment")
                        val comment_time = item.getString("comment_time")
                        val comment_num = comment_user[id]!!

                        val commentItem = CommentItem(comment, comment_num, comment_time)

                        Comment_items.add(commentItem)
                        viewModel.setItemList(Comment_items)

                        //댓글 아이템 하나 누르면
                        var manager: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
                        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
                        val comment2_linearLayout =
                            findViewById<LinearLayout>(R.id.comment2_linearLayout)

                        Commentadapter.setOnItemClickListener(object :
                            CommentListAdapter.OnItemClickListener {
                            override fun onItemClick(v: View, data: CommentItem, pos: Int) {
                                Toast.makeText(
                                    applicationContext,
                                    String.format("대댓글 ? "),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Comment_editText.requestFocus()
                                manager.showSoftInput(
                                    Comment_editText,
                                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                                ) //키보드 올리기

                                Comment_Btn.setOnClickListener {
                                    Toast.makeText(
                                        applicationContext,
                                        String.format("우왕"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    //comment2_linearLayout.visibility = View.VISIBLE
                                }

                            }
                        })
                    }
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
        var comment_count = 0
        var comment_num = 1

        Log.d("comment_num", comment_num.toString())

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
                    Log.d("CCCCCCCCCCC", response)

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
                        "$id, $post_num, $comment, $comment_time, $comment_num"
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
                "comment_num" to comment_num.toString(),
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
        var post_num = intent?.getIntExtra("num", 0).toString()
        var board = intent?.getStringExtra("board").toString()

        Comment_items.clear()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (response != "no Comment") {
                    val jsonArray = JSONArray(response)

                    Log.d("jjjj", "$jsonArray")

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

                        val commentItem = CommentItem(comment, comment_num, comment_time)

                        Comment_items.add(commentItem)


                        Log.d("commmentItem", "$post_num, $comment, $comment_num, $comment_time")

                        //viewModel.setItemList(Comment_items)
                        CommentRecyclerView.adapter = Commentadapter

                        var userId = intent?.getStringExtra("id").toString()
                        var detailId: String = ""
                        var detailComment: String = ""
                        var detailCommentTime: String = ""

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

                                        //null이 찍혀요 ,, 왜일까요 ?
                                        Log.d("??????", "$detailId, $detailComment, $detailCommentTime")

                                        val intent = Intent(
                                            applicationContext,
                                            CommentDetail::class.java
                                        )
                                        intent.putExtra("comment_num", data.comment_num)
                                        intent.putExtra("id", userId)
                                        intent.putExtra("comment", detailComment)
                                        intent.putExtra("comment_time", detailCommentTime)
                                        intent.putExtra("post_num", post_num)

                                        startActivity(intent)
                                    },
                                    { Log.d("Comment failed", "error......${error(applicationContext)}") },
                                    hashMapOf(
                                        "comment_num" to data.comment_num.toString(),
                                        "post_num" to post_num
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


    @SuppressLint("SuspiciousIndentation")
    fun fetchLikeData() {
        val url = "http://seonho.dothome.co.kr/Heart_list.php"
        var id = intent?.getStringExtra("id").toString()
        val board = intent.getStringExtra("board")!!.toString()
        Log.d("13123123", board.javaClass.name)
        var post_num = intent?.getIntExtra("num", 0).toString()

        val Like_Btn = findViewById<ImageView>(R.id.imageView_like2) //좋아요 하트 부분

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("123123", response.javaClass.name)
                if (response != "no Heart") {
                    val jsonArray = JSONArray(response)

                    val like_count = jsonArray.length()
                    findViewById<TextView>(R.id.textView_likecount2).text = like_count.toString()

                    for (i in 0 until jsonArray.length()) {
                        val likeId = jsonArray.getString(i)
                        if(likeId == id){
                            Like_Btn.setImageResource(R.drawable.favorite_fill)
                            isDefault = true
                            break
                        }
                    }
                }
            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "board" to board,
                "post_num" to post_num
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

