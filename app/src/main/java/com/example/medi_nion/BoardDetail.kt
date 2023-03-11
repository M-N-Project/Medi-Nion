package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.state.ToggleableState
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)

        var count = 0
        var isDefault = true //좋아요 빈하트, 채운하트 구분하기위함
        val Comment_editText = findViewById<EditText>(R.id.Comment_editText)
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn)
        val Like_Btn = findViewById<ImageView>(R.id.imageView_like2) //좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //좋아요 숫자 부분
        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2) //북마크 imageview 부분
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2) //북마크 count 부분

        val manager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        window.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING)
        fetchData()

//        val dataObserver: Observer<ArrayList<CommentItem>> =
//            Observer { livedata ->
//                Comment_items = livedata
//                var newAdapter = CommentListAdapter(Comment_items)
//                CommentRecyclerView.adapter = newAdapter
//
//            }
//
//        viewModel.itemList.observe(this, dataObserver)

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
            var postImg = findViewById<ImageView>(R.id.post_imgView)
            postImg.visibility = View.VISIBLE
            val bitmap: Bitmap? = StringToBitmaps(image)
            postImg.setImageBitmap(bitmap)
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

            if (isDefault) {
                count--
                Like_count.text = count.toString() //Like_count를 감소시키기
                Like_Btn.setImageResource(R.drawable.favorite_border)
                //Like_Delete_Request()
            } else {
                count++
                Like_count.text = count.toString() //Like_count를 증가시키기
                Like_Btn.setImageResource(R.drawable.favorite_fill)
                LikeRequest()
            }
        }

        Book_Btn.setOnClickListener {
            if (Book_Btn.isChecked()) {
                Book_Create_request()
            } else {
                Book_Delete_request()
            }
        }
    }


//    override fun onBackPressed() {
//        var id = intent.getStringExtra("id")
//        val board = intent.getStringExtra("board")
//
//        val intent =
//            Intent(this@BoardDetail, Board::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
//        intent.putExtra("id", id)
//        intent.putExtra("board", board)
//        startActivity(intent) //인텐트 이동
//        finish() //현재 액티비티 종료
//    }



//    fun likeRequest() {
//        val url = "http://seonho.dothome.co.kr/Heart.php"
//        val postParams = hashMapOf("id" to "1", "heart_count" to "10")
//
//        val client = OkHttpClient()
//        val formBodyBuilder = FormBody.Builder()
//
//        for ((key, value) in postParams) {
//            formBodyBuilder.add(key, value)
//        }
//
//        val requestBody = formBodyBuilder.build()
//        val request = Request.Method
//            .url(url)
//            .post(requestBody)
//            .build()
//
//        val response = client.newCall(request).execute()
//        val responseBody = response.body()?.string()
//    }


    fun LikeRequest() {  //좋아요 DB연동중
        var id = intent?.getStringExtra("id").toString() //user id 받아오기, 내가 좋아요 한 글 보기 위함
        var post_num = intent?.getIntExtra("num", 0).toString() //게시물 num id 받아오기, 게시물 좋아요 개수 구분하기 위함
//        var heart =
//            findViewById<ImageView>(R.id.imageView_like2).toString() //좋아요 클릭만 가져오게 하기(익명이라 누가 눌렀는진 의미 없을듯,,)
        var heart_count = findViewById<TextView>(R.id.textView_likecount2).text.toString()
        //var heart_count2 = findViewById<TextView>(R.id.textView_likecount).text.toString()
        val url = "http://seonho.dothome.co.kr/Heart.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Like fail")) {
                    heart_count = response.toString()
                    post_num = response.toString()

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
                "heart" to heart_count,
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun Like_Delete_Request() {
        var id = intent?.getStringExtra("id").toString()
        var heart_count = findViewById<TextView>(R.id.textView_likecount2).text.toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/HeartDelete.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Like fail")) {

                    Toast.makeText(
                        baseContext,
                        String.format("좋아요 해제되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "like delete success",
                        "$id, $post_num"
                    )
                    
                } else {

                    Toast.makeText(
                        applicationContext,
                        "좋아요 해제할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("like Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "id" to id,
                "heart" to heart_count,
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchLikeData() {
        val url = "http://seonho.dothome.co.kr/Heart_list.php"
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

    //////////////////////////////////////////////////like, book구분

    fun Book_Delete_request() {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var url = "http://seonho.dothome.co.kr/BookmarkDelete.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Bookmark fail")) {

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

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Bookmark fail")) {

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
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()
        var comment_count = 0
        var comment_num = 1

        val url = "http://seonho.dothome.co.kr/Comment.php"

        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val comment_time = current.format(formatter)

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment fail")) {
//                    comment = response.toString()
//                    comment_num = response.toString(

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

        Comment_items.clear()

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

                        val commentItem = CommentItem(comment, comment_num, comment_time)

                        Comment_items.add(commentItem)


                        Log.d("commmentItem", "$post_num, $comment, $comment_num, $comment_time")

                        //viewModel.setItemList(Comment_items)
                        CommentRecyclerView.adapter = Commentadapter

                        var detailId: String = ""
                        var detailComment: String = ""
                        var detailCommentTime: String = ""
                        var userId = intent?.getStringExtra("id").toString()

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
                                        intent.putExtra("comment", detailComment)
                                        intent.putExtra("comment_time", detailCommentTime)
                                        intent.putExtra("post_num", post_num)

                                        Log.d("888", userId)

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
}

