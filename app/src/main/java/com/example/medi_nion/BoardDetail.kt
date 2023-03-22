package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_comment_item.*
import kotlinx.android.synthetic.main.board_comment_item.view.*
import kotlinx.android.synthetic.main.board_detail.*
import kotlinx.android.synthetic.main.board_detail.CommentRecyclerView
import kotlinx.android.synthetic.main.board_detail.refresh_layout
import kotlinx.android.synthetic.main.board_detail.view.*
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.comment_comment_detail.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
var comment_items =ArrayList<CommentItem>()
var commentDetail_items =ArrayList<CommentDetailItem>()
var commentDetailadapter = CommentDetailListAdapter(commentDetail_items)
var Commentadapter = CommentListAdapter(comment_items)
val viewModel: CommentViewModel = CommentViewModel()

class BoardDetail : AppCompatActivity() {

    var comment_num = 0
    var comment_comment_num = 0
    var comment_comment_flag = false // 댓글창에 입력할때, 댓글 입력하는 건지/ 대댓글 입력하는건지
    var comment_comment_posPresent = -1 //대댓글 인덱스
    var comment_comment_posBefore = -1 //대댓글 인덱스

    var commentHeartFlag = "false"
    var comment2HeartFlag = "false"

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경
        refresh_layout.setOnRefreshListener {
            Log.d("ditto", "bye refresh")

            try {
                //TODO 액티비티 화면 재갱신 시키는 코드
                val intent = intent
                finish() //현재 액티비티 종료 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                startActivity(intent) //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
//                commentDetailadapter = CommentDetailListAdapter(commentDetail_items)
//                Commentadapter = CommentListAdapter(comment_items)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            refresh_layout.isRefreshing = false //새로고침 없애기
        }

// ================================================= 변수 ==================================================================
        //Board.kt에서 BoardDetail.kt로 데이터 intent
        var id = intent.getStringExtra("id") //접속한 유저의 아이디
        var writerId = intent.getStringExtra("writerId") //게시물을 작성한 유저의 아이디
        val post_num = intent?.getIntExtra("num", 0).toString() //현재 상세보기 중인 게시물의 num
        val title = intent.getStringExtra("title") // 게시물 제목
        val content = intent.getStringExtra("content") // 게시물 내용
        val time = intent.getStringExtra("time") // 게시물 등록 시간
        val image = intent.getStringExtra("image") // 게시물 사진

        val title_textView = findViewById<TextView>(R.id.textView_title) // 게시물 상세 - 제목
        val content_textView = findViewById<TextView>(R.id.textView_content) // 게시물 상세 - 내용
        val time_textView = findViewById<TextView>(R.id.textView_time) // 게시물 상세 - 시간

        val Comment_editText = findViewById<EditText>(R.id.Comment_editText) // 게시물 상세 댓글창
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn) //게시물 상세 댓글 등록 버튼
        val Like_Btn = findViewById<CheckBox>(R.id.imageView_like2) //게시물 상세 좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //게시물 상세 좋아요 숫자 부분
        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2) //게시물 상세 북마크 imageview 부분
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2) //게시물 상세 북마크 count 부분

        val optionBtn = findViewById<Button>(R.id.moreBtn) // 게시물 옵션 버튼(수정/삭제)
        var optionRadio = findViewById<RadioGroup>(R.id.optionRadioGroup) //게시물 옵션 버튼 눌렀을 때 보이는 수정/삭제 라디오 그룹

        //어댑터 연결
        val Commentadapter = CommentListAdapter(comment_items)
        CommentRecyclerView.adapter = Commentadapter

//=================================================== 게시물 상세 내용 fetch ================================================================================

        title_textView.setText(title) // 제목
        content_textView.setText(content) // 내용
        time_textView.setText(time) //시간

        //이미지 fetch
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

        fetchLikeData() // 좋아요 fetch
        fetchCommentData() // 댓글 fetch
        fetchBookmarkData() // 북마크 fetch


//============================================= 클릭 리스너 ================================================================================
        // 게시물 옵션 버튼. (작성자에 한해서 옵션 버튼 visible)---------------------------------------
        if(id==writerId){
            optionBtn.visibility = View.VISIBLE

            //게시물 옵션 버튼 눌렀을때 수정/삭제 버튼 visible / 다시 누르면 invisible
            optionBtn.setOnClickListener{
                if(optionRadio.visibility == View.GONE)
                    optionRadio.visibility = View.VISIBLE
                else optionRadio.visibility = View.GONE
            }

            // 게시물 수정 버튼 클릭
            val option_updatePost = findViewById<RadioButton>(R.id.postUpdate_RadioBtn)
            option_updatePost.setOnClickListener{
                // 글쓰기 화면으로 이동
                val board = intent.getStringExtra("board")
                var userType = intent.getStringExtra("userType")
                var userDept = intent.getStringExtra("userDept")
                val intent = Intent(applicationContext, BoardWrite::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", board)
                intent.putExtra("post_num", post_num)
                intent.putExtra("title", title)
                intent.putExtra("content", content)
                intent.putExtra("image", image)
                intent.putExtra("update", 1)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)

                startActivity(intent)
            }

            // 게시물 삭제 버튼 클릭
            val option_deletePost = findViewById<RadioButton>(R.id.postDelete_RadioBtn)
            option_deletePost.setOnClickListener{
                // 지우기.
                PostDeleteRequest()
            }
        }

        // 좋아요 버튼 눌렀을 때 -----------------------------------------------------------------
        Like_Btn.setOnClickListener {
            if (Like_Btn.isChecked) {
                LikeRequest(true)
                Like_count.text = (Like_count.text.toString().toInt() + 1).toString()
            } else {
                LikeRequest(false)
                Like_count.text = (Like_count.text.toString().toInt() - 1).toString()
            }
        }


        // 댓글 버튼 눌렀을때----------------------------------------------------------------------
        Comment_Btn.setOnClickListener {
            if(comment_comment_flag == true){ //대댓글
                Comment2Request(comment_comment_posPresent +1 , ++comment_comment_num)
                val manager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
                Comment_editText.setText(null) //댓글입력창 clear
                comment_comment_flag = false
                findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(Color.parseColor("#ffffff"))
            }
            else{ //댓글
                CommentRequest(++comment_num)
                val manager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
                Comment_editText.setText(null) //댓글입력창 clear
            }

        }

        // 북마크 버튼 눌렀을 때 ------------------------------------------------------------------
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


//fetch 함수들 =====================================================================================================================

    // 좋아요 fetch ----------------------------------------------------------------------------
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

    // 댓글 fetch -------------------------------------------------------------------------------
    @SuppressLint("SuspiciousIndentation")
    fun fetchCommentData() {
        val url = "http://seonho.dothome.co.kr/Comment_list.php"
        val urlCommentHeart = "http://seonho.dothome.co.kr/commentHeart.php"
        val urlCommentHeartFetch = "http://seonho.dothome.co.kr/commentHeart_list.php"

        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var board = intent?.getStringExtra("board").toString()

        items.clear()
        comment_items.clear()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if(response != "no Comment"){
                    items.clear()
                    comment_items.clear()

                    val jsonArray = JSONArray(response)

                    val comment_count = jsonArray.length()
                    comment_num = comment_count
                    findViewById<TextView>(R.id.textView_commentcount2).text =
                        comment_count.toString()

                    var comment_user = HashMap<String, Int>()

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val writerId = item.getString("id")
                        val appUser = intent?.getStringExtra("id").toString()
                        val comment = item.getString("comment")
                        val comment_time = item.getString("comment_time")
                        val comment_num = item.getInt("comment_num")
                        val heart = item.getInt("heart")
                        var isHeartMap = HashMap<String, Boolean>()

                        if (!comment_user.containsKey(writerId)) comment_user[writerId] =
                            comment_user.size + 1

                        //댓글 좋아요 가져오기...
                        val requestCommentLike = Login_Request(
                            Request.Method.POST,
                            urlCommentHeartFetch,
                            { response ->
                                if(response != "Comment Heart Fetch fail") {
                                    if (response != "no Comment Heart") {
                                        val jsonArrayHeart = JSONArray(response)

                                        for (i in 0 until jsonArrayHeart.length()) {

                                            val itemHeart = jsonArrayHeart.getJSONObject(i)


                                            val heartId = itemHeart.getString("id")
                                            val heart_num = itemHeart.getString("count")

                                            isHeartMap[appUser] = appUser == heartId
                                        }
                                    }

                                    // 해당되는 대댓글들 가져오기
                                    var commentDetailAdapterMap =
                                        HashMap<Int, CommentDetailListAdapter>()
                                    //대댓글 fetch --------------------------------------------------------
                                    val urlDetail = "http://seonho.dothome.co.kr/Comment2_list.php"

                                    val request = Login_Request(
                                        Request.Method.POST,
                                        urlDetail,
                                        { response ->
                                            commentDetail_items.clear()
                                            if (response != "Comment2 Fetch fail") {
                                                val jsonArrayComment2 = JSONArray(response)

                                                for (i in 0 until jsonArrayComment2.length()) {
                                                    val item = jsonArrayComment2.getJSONObject(i)
                                                    val id = item.getString("id")
                                                    if (!comment_user.containsKey(id)) comment_user[id] =
                                                        comment_user.size + 1
                                                }

                                                for (i in 0 until jsonArrayComment2.length()) {
                                                    val item = jsonArrayComment2.getJSONObject(i)

                                                    val id = item.getString("id")
                                                    val comment_num = item.getInt("comment_num")
                                                    val comment2 = item.getString("comment2")
                                                    val comment2_time = item.getString("comment2_time")
                                                    val comment2_num = item.getInt("comment2_num")
                                                    val writerUser = comment_user[id]!!
                                                    val heart = item.getInt("heart")

                                                    Log.d("=2-304", heart.toString())

                                                    val commentDetailItem =
                                                        CommentDetailItem(
                                                            id,
                                                            writerUser,
                                                            comment_num,
                                                            comment2,
                                                            comment2_num,
                                                            comment2_time,
                                                            heart
                                                        )

                                                    commentDetail_items.add(commentDetailItem) //comment_num에 맞는 대댓글들 들어가있음.

                                                    Log.d(
                                                        "commmentDetailItem",
                                                        "$id, $post_num, $comment2_num, $comment2, $writerUser, $comment2_time , $heart"
                                                    )
                                                }

                                                // 댓글에 대댓글 붙이기 000000000000000000000000000000000000000000000000000
                                                //각 댓글마다..
                                                var commentDetailadapter =
                                                    CommentDetailListAdapter(commentDetail_items)

                                                //대댓글 클릭 리스너 +++++++++++++++++++++++++++++++++++++++++++++++++++
                                                commentDetailadapter.setOnItemClickListener(object :
                                                    CommentDetailListAdapter.OnItemClickListener {
                                                    //대댓글 좋아요 눌렀을때.
                                                    override fun onItemHeart(
                                                        v: View,
                                                        data: CommentDetailItem,
                                                        pos: Int
                                                    ) {
                                                        var urlUpdateComment2Cnt =
                                                            "http://seonho.dothome.co.kr/updateComment2Cnt.php"
                                                        var urlComment2Heart =
                                                            "http://seonho.dothome.co.kr/comment2Heart.php"
                                                        var id =
                                                            intent?.getStringExtra("id").toString()
                                                        val comment2Heart =
                                                            v.findViewById<CheckBox>(R.id.imageView_comment2_like)
                                                        val comment2HeartCnt =
                                                            v.findViewById<TextView>(R.id.comment2_heartCnt)

                                                        if (comment2Heart.isChecked) {
                                                            comment2HeartFlag = "true"
                                                            comment2HeartCnt.text =
                                                                (comment2HeartCnt.text.toString()
                                                                    .toInt() + 1).toString()
                                                        } else {
                                                            comment2HeartFlag = "false"
                                                            comment2HeartCnt.text =
                                                                (comment2HeartCnt.text.toString()
                                                                    .toInt() - 1).toString()
                                                        }

                                                        val request = Login_Request(
                                                            Request.Method.POST,
                                                            urlComment2Heart,
                                                            { response ->
                                                                Log.d("comment2Hart", response)

                                                                if (comment2Heart.isChecked) comment2HeartFlag =
                                                                    "commentHeartUP"
                                                                else comment2HeartFlag =
                                                                    "commentHeartDOWN"

                                                                val requestCnt = Login_Request(
                                                                    Request.Method.POST,
                                                                    urlUpdateComment2Cnt,
                                                                    { responseLike ->
                                                                        if (!responseLike.equals("update fail")) {
                                                                            // comment의 heart 개수 업데이트 성공
                                                                        } else {
                                                                            Toast.makeText(
                                                                                applicationContext,
                                                                                "lion heart fail",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        }

                                                                    },
                                                                    {
                                                                        Log.d(
                                                                            "lion heart Failed",
                                                                            "error......${
                                                                                error(
                                                                                    applicationContext
                                                                                )
                                                                            }"
                                                                        )
                                                                    },

                                                                    hashMapOf(
                                                                        "board" to board,
                                                                        "post_num" to post_num,
                                                                        "comment_num" to comment_num.toString(),
                                                                        "comment2_num" to (pos + 1).toString(),
                                                                        "flag" to comment2HeartFlag
                                                                    )
                                                                )

                                                                val queue = Volley.newRequestQueue(
                                                                    applicationContext
                                                                )
                                                                queue.add(requestCnt)
                                                            },
                                                            {
                                                                Log.d(
                                                                    "Comment2 failed",
                                                                    "error......${
                                                                        error(
                                                                            applicationContext
                                                                        )
                                                                    }"
                                                                )
                                                            },
                                                            hashMapOf(
                                                                "id" to id,
                                                                "post_num" to post_num,
                                                                "board" to board,
                                                                "comment_num" to comment_num.toString(),
                                                                "comment2_num" to (pos + 1).toString(),
                                                                "flag" to comment2HeartFlag.toString()
                                                            )
                                                        )
                                                        val queue = Volley.newRequestQueue(
                                                            applicationContext
                                                        )
                                                        queue.add(request)
                                                    }
                                                })
                                                if (!comment_user.containsKey(writerId)) comment_user[writerId] =
                                                    comment_user.size + 1

                                                val isHeart = if(isHeartMap.containsKey(appUser))(isHeartMap[appUser]) else false

                                                val commentItem = CommentItem(
                                                    writerId,
                                                    comment_user[writerId]!!,
                                                    comment,
                                                    comment_num,
                                                    comment_time,
                                                    heart,
                                                    isHeart,
                                                    commentDetailadapter
                                                )
                                                comment_items.add(commentItem)
                                                Commentadapter = CommentListAdapter(comment_items)
                                                CommentRecyclerView.adapter = Commentadapter

                                                //댓글 클릭 리스너 ++++++++++++++++++++++++++++++++++++++++++++++++
                                                Commentadapter.setOnItemClickListener(object :
                                                    CommentListAdapter.OnItemClickListener {
                                                    override fun onItemClick(
                                                        v: View,
                                                        data: CommentItem,
                                                        pos: Int
                                                    ) {
                                                        //댓글 눌렀을때. -> 대댓글
                                                        comment_comment_posPresent = pos
                                                        if (comment_comment_flag == true) {
                                                            if (comment_comment_posPresent == comment_comment_posBefore) {
                                                                comment_comment_flag = false
                                                                CommentRecyclerView.get(
                                                                    comment_comment_posPresent
                                                                )
                                                                    .findViewById<LinearLayout>(R.id.comment_linearLayout)
                                                                    .setBackgroundColor(
                                                                        Color.parseColor("#ffffff")
                                                                    )
                                                            } else {

                                                                CommentRecyclerView.get(
                                                                    comment_comment_posBefore
                                                                )
                                                                    .findViewById<LinearLayout>(R.id.comment_linearLayout)
                                                                    .setBackgroundColor(
                                                                        Color.parseColor("#ffffff")
                                                                    )
                                                                CommentRecyclerView.get(
                                                                    comment_comment_posPresent
                                                                )
                                                                    .findViewById<LinearLayout>(R.id.comment_linearLayout)
                                                                    .setBackgroundColor(
                                                                        Color.parseColor("#5085D6A4")
                                                                    )
                                                                comment_comment_posBefore = pos
                                                            }

                                                            //                                    findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(Color.parseColor("#ffffff"))
                                                            //자동 키보드 내리기
                                                        } else {
                                                            comment_comment_flag = true
                                                            comment_comment_posBefore = pos
                                                            comment_comment_posPresent = pos

                                                            CommentRecyclerView.get(pos)
                                                                .findViewById<LinearLayout>(R.id.comment_linearLayout)
                                                                .setBackgroundColor(Color.parseColor("#5085D6A4"))
                                                            //                                    findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(Color.parseColor("#5085D6A4"))
                                                            //자동 키보드 올리기
                                                        }
                                                    }

                                                    //댓글 좋아요 눌렀을때.
                                                    override fun onItemHeart(
                                                        v: View,
                                                        data: CommentItem,
                                                        pos: Int
                                                    ) {
                                                        var urlUpdateCommentCnt =
                                                            "http://seonho.dothome.co.kr/updateCommentCnt.php"
                                                        var id = intent?.getStringExtra("id").toString()
                                                        val commentHeart =
                                                            v.findViewById<CheckBox>(R.id.imageView_comment_like)
                                                        val commentHeartCnt =
                                                            v.findViewById<TextView>(R.id.comment_heart_count)

                                                        if (commentHeart.isChecked) {
                                                            commentHeartFlag = "true"
                                                            commentHeartCnt.text =
                                                                (commentHeartCnt.text.toString()
                                                                    .toInt() + 1).toString()
                                                        } else {
                                                            commentHeartFlag = "false"
                                                            commentHeartCnt.text =
                                                                (commentHeartCnt.text.toString()
                                                                    .toInt() - 1).toString()
                                                        }

                                                        Log.d(
                                                            "---",
                                                            "$id // $post_num // $board // $pos // $commentHeartFlag"
                                                        )

                                                        val request = Login_Request(
                                                            Request.Method.POST,
                                                            urlCommentHeart,
                                                            { response ->
                                                                Log.d("commentHart", response)

                                                                var commentHeartFlag = ""
                                                                if (commentHeart.isChecked) commentHeartFlag =
                                                                    "commentHeartUP"
                                                                else commentHeartFlag =
                                                                    "commentHeartDOWN"

                                                                val requestCnt = Login_Request(
                                                                    Request.Method.POST,
                                                                    urlUpdateCommentCnt,
                                                                    { responseLike ->
                                                                        if (!responseLike.equals("update fail")) {
                                                                            // comment의 heart 개수 업데이트 성공
                                                                        } else {
                                                                            Toast.makeText(
                                                                                applicationContext,
                                                                                "lion heart fail",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        }

                                                                    },
                                                                    {
                                                                        Log.d(
                                                                            "lion heart Failed",
                                                                            "error......${
                                                                                error(
                                                                                    applicationContext
                                                                                )
                                                                            }"
                                                                        )
                                                                    },

                                                                    hashMapOf(
                                                                        "board" to board,
                                                                        "post_num" to post_num,
                                                                        "comment_num" to (pos + 1).toString(),
                                                                        "flag" to commentHeartFlag
                                                                    )
                                                                )

                                                                val queue = Volley.newRequestQueue(
                                                                    applicationContext
                                                                )
                                                                queue.add(requestCnt)

                                                                //좋아요 개수 fetch

                                            },
                                            { Log.d("Comment failed", "error......${error(applicationContext)}") },
                                            hashMapOf(
                                                "id" to id,
                                                "post_num" to post_num,
                                                "board" to board,
                                                "comment_num" to (pos+1).toString(),
                                                "flag" to commentHeartFlag.toString()
                                            )
                                        )
                                        val queue = Volley.newRequestQueue(applicationContext)
                                        queue.add(request)
                                    }

                                                    //대댓글 버튼 눌렀을때.
                                                    @RequiresApi(Build.VERSION_CODES.O)
                                                    override fun onItemComment(
                                                        v: View,
                                                        data: CommentItem,
                                                        pos: Int
                                                    ) {
                                                        if (comment_comment_flag == true) {
                                                            comment_comment_flag = false
                                                            findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(
                                                                Color.parseColor("#ffffff")
                                                            )
                                                            //자동 키보드 내리기
                                                        } else {
                                                            comment_comment_flag = true
                                                            comment_comment_posPresent = pos
                                                            findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(
                                                                Color.parseColor("#5085D6A4")
                                                            )
                                                            //자동 키보드 올리기
                                                        }

                                                    }
                                                })


                                            }
                                        },
                                        {
                                            Log.d(
                                                "login failed",
                                                "error......${error(applicationContext)}"
                                            )
                                        },
                                        hashMapOf(
                                            "comment_num" to comment_num.toString(),
                                            "post_num" to post_num,
                                            "board" to board
                                        )
                                    )
                                    val queue = Volley.newRequestQueue(this)
                                    queue.add(request)
                                }

                            }, { Log.d("login failed", "error......${error(applicationContext)}") },
                            hashMapOf(
                                "comment_num" to comment_num.toString(),
                                "post_num" to post_num,
                                "board" to board
                            )
                        )
                        val queue = Volley.newRequestQueue(this)
                        queue.add(requestCommentLike)
                        //------------------------------------------------------------------------------------------------

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


    // 북마크 fetch -----------------------------------------------------------------------------
    fun fetchBookmarkData() {
        val url = "http://seonho.dothome.co.kr/Bookmark_list.php"
        var post_num = intent?.getIntExtra("num", 0).toString()
        var id = intent?.getStringExtra("id").toString()

        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2)
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2)


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

                }

            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }



    //request 함수들 =====================================================================================================================
    // 게시물 삭제 request -------------------------------------------------------------------
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

    // 좋아요 request ------------------------------------------------------------------------
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

    // 댓글 request ------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun CommentRequest(comment_num : Int) {
        var id = intent?.getStringExtra("id").toString()
        var board = intent?.getStringExtra("board").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()

        val url = "http://seonho.dothome.co.kr/Comment.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var comment_time = current.format(formatter)

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

                    fetchCommentData()

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
                "comment_num" to comment_num.toString(),
                "comment" to comment,
                "comment_time" to comment_time
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    //대댓글 request-------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun Comment2Request(comment_num : Int, comment2_num : Int) {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getIntExtra("num", 0).toString()
        var board = intent?.getStringExtra("board").toString()
        var comment2 = findViewById<EditText>(R.id.Comment_editText).text.toString()


        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val comment2_time = current.format(formatter)

        val url = "http://seonho.dothome.co.kr/Comment2.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment2 fail")) {

                    Toast.makeText(
                        baseContext,
                        String.format("대댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "comment2 success",
                        "$board, $id, $post_num, $comment_num, $comment2, $comment2_time"
                    )
                    fetchCommentData()

                } else {

                    Toast.makeText(
                        applicationContext,
                        "대댓글을 등록할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("Comment2 Failed", "error......${error(applicationContext)}") },

            hashMapOf(
                "board" to board,
                "id" to id,
                "post_num" to post_num,
                "comment_num" to comment_num.toString(),
                "comment2_num" to comment2_num.toString(),
                "comment2" to comment2
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

//        fetchCommentData()
    }

    // 북마크 request -------------------------------------------------------------------------------
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




    // 이미지 : String -> Bitmap 변환 =====================================================================================================
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

    // 화면 터치 감지 ( 키보드 내리기 등.. ) ==================================================================================================
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
