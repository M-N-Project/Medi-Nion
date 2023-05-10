package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.board_detail.*
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private var comment_items =ArrayList<CommentItem>()
private var commentDetail_items =ArrayList<CommentDetailItem>()
private var Commentadapter = CommentListAdapter(comment_items)

class BoardDetail : AppCompatActivity() {

    var comment_num = 0
    var comment2_count = HashMap<Int, Int>()
    var comment_comment_num = 0
    var comment_comment_flag = false // 댓글창에 입력할때, 댓글 입력하는 건지/ 대댓글 입력하는건지
    var comment_comment_posPresent = -1 //대댓글 인덱스
    var comment_comment_posBefore = -1 //대댓글 인덱스

    var commentHeartFlag = "false"
    var comment2HeartFlag = "false"

    private val random = (1..100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
    private val alarmCode = random.random()
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    lateinit var notificationPermission: ActivityResultLauncher<String>

    companion object{
        private const val ALARM_REQUEST_CODE = 1000
        const val CHANNEL_ID = "medinion"
        const val CHANNEL_NAME = "comment alarm"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "CutPasteId", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_detail)
        //댓글 알림 권한
        val notificationPermissionCheck = ContextCompat.checkSelfPermission(
            this@BoardDetail,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        if (notificationPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10000
            )
        } else { //권한이 있는 경우
            Log.d("0-09123","notinoti")
        }

        notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("comment_noti", "notinoti")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notification()
                }
            } else {
                Toast.makeText(baseContext, "권한을 승인해야 댓글 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            Log.d("create", "Channel")
        }

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경
        refresh_layout.setOnRefreshListener {

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
        val device_id = intent.getStringExtra("device_id") //접속한 유저의 디바이스 고유 아이디
        var writerId = intent.getStringExtra("writerId") //게시물을 작성한 유저의 아이디
        var userMedal = intent.getIntExtra("userMedal", 0)
        val post_num = intent?.getStringExtra("num").toString() //현재 상세보기 중인 게시물의 num
        val title = intent.getStringExtra("title") // 게시물 제목
        val content = intent.getStringExtra("content") // 게시물 내용
        val time = intent.getStringExtra("time") // 게시물 등록 시간
        val image = intent.getStringExtra("image") // 게시물 사진
        val commentCnt = intent.getStringExtra("commentCnt")

        val title_textView = findViewById<TextView>(R.id.textView_title) // 게시물 상세 - 제목
        val content_textView = findViewById<TextView>(R.id.textView_content) // 게시물 상세 - 내용
        val time_textView = findViewById<TextView>(R.id.textView_time) // 게시물 상세 - 시간

        val Comment_editText = findViewById<EditText>(R.id.Comment_editText) // 게시물 상세 댓글창
        val Comment_Btn = findViewById<Button>(R.id.Comment_Btn) //게시물 상세 댓글 등록 버튼
        val Like_Btn = findViewById<CheckBox>(R.id.imageView_like2) //게시물 상세 좋아요 하트 부분
        val Like_count = findViewById<TextView>(R.id.textView_likecount2) //게시물 상세 좋아요 숫자 부분
        val Book_Btn = findViewById<CheckBox>(R.id.checkbox_bookmark2) //게시물 상세 북마크 imageview 부분
        val Book_count = findViewById<TextView>(R.id.textView_bookmarkcount2) //게시물 상세 북마크 count 부분
        val comment_count = findViewById<TextView>(R.id.textView_commentcount2)

        val optionBtn = findViewById<Button>(R.id.moreBtn) // 게시물 옵션 버튼(수정/삭제)
        var optionRadio = findViewById<RadioGroup>(R.id.optionRadioGroup) //게시물 옵션 버튼 눌렀을 때 보이는 수정/삭제 라디오 그룹

        //어댑터 연결
        val Commentadapter = CommentListAdapter(comment_items)
        CommentRecyclerView.adapter = Commentadapter

//=================================================== 게시물 상세 내용 fetch ================================================================================


        title_textView.setText(title) // 제목
        content_textView.setText(content) // 내용
        time_textView.setText(time) //시간
        comment_count.setText(commentCnt)

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

        fetchMedalData() // 메달 fetch
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
                intent.putExtra("userMedal", userMedal)

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
            //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            if(comment_comment_flag == true){ //대댓글
                comment_comment_num = if(comment2_count.containsKey(comment_comment_posPresent+1)) comment2_count[comment_comment_posPresent+1]!! else 1
                Comment2Request(comment_comment_posPresent+1 , ++comment_comment_num)
                val manager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
                Comment_editText.setText(null) //댓글입력창 clear
                comment_comment_flag = false
                findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(Color.parseColor("#ffffff"))
                findViewById<TextView>(R.id.textView_commentcount2).text = (findViewById<TextView>(R.id.textView_commentcount2).text.toString().toInt() + 1).toString()
            }
            else{ //댓글
                CommentRequest(++comment_num)
                ///헤헤헤
                val manager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS) //Comment버튼 누르면 키보드 내리기
                Comment_editText.setText(null) //댓글입력창 clear
                if( findViewById<TextView>(R.id.textView_commentcount2).text.toString() == "")  findViewById<TextView>(R.id.textView_commentcount2).text = "1"
                else findViewById<TextView>(R.id.textView_commentcount2).text = (findViewById<TextView>(R.id.textView_commentcount2).text.toString().toInt() + 1).toString()
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

    //댓글 알림
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification() {
        notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

//fetch 함수들 ====================================================================================================================
    // emdal fetch ---------------------------------------------------------------------------
    fun fetchMedalData() {
        val medalurl = "http://seonho.dothome.co.kr/Medal_Select.php"
        var writerid = intent.getStringExtra("writerId").toString()
        val medalImage = findViewById<ImageView>(R.id.medal)

        val request = Login_Request(
            Request.Method.POST,
            medalurl,
            { response ->
                Log.d("medlaSele", response)

                when (response) {
                    "king" -> {
                        medalImage.setImageResource(R.drawable.king_medal)
                    }
                    "gold" -> {
                        medalImage.setImageResource(R.drawable.gold_medal)
                    }
                    "silver" -> {
                        medalImage.setImageResource(R.drawable.silver_medal)
                    }
                    else -> {
                        medalImage.setImageResource(R.drawable.bronze_medal)
                    }
                }

            }, { Log.d("like Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to writerid
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


    // 좋아요 fetch ----------------------------------------------------------------------------
    fun fetchLikeData() {
        val url = "http://seonho.dothome.co.kr/Heart_list.php"
        var post_num = intent?.getStringExtra("num").toString()
        var id = intent?.getStringExtra("id").toString() //하트를 누른 유저의 아이디

        val board = intent?.getStringExtra("board").toString()

        val Like_Btn = findViewById<CheckBox>(R.id.imageView_like2)
        val Like_count = findViewById<TextView>(R.id.textView_likecount2)

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
                "board" to board,
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
        var post_num = intent?.getStringExtra("num").toString()
        var board = intent?.getStringExtra("board").toString()

        comment_items.clear()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("conmentmr", response.toString())
                if(response != "Comment Fetch fail"){
                    if(response == "no Comment"){
                        comment_items.clear()
                        Commentadapter =
                            CommentListAdapter(comment_items)
                        CommentRecyclerView.adapter =
                            Commentadapter

                    }
                    else{
                        comment_items.clear()

                        val jsonArray = JSONArray(response)

                        val comment_count = jsonArray.length()
                        comment_num = comment_count

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
                                            val heartUserList = ArrayList<String>() //디테일 안보이는 원인후보2,,,
                                            val jsonArrayHeart = JSONArray(response)

                                            for (i in 0 until jsonArrayHeart.length()) {

                                                val itemHeart = jsonArrayHeart.getJSONObject(i)

                                                val heartId = itemHeart.getString("id")
                                                val heart_num = itemHeart.getString("count")

                                                heartUserList.add(heartId)
                                            }

                                            if(heartUserList.contains(appUser))
                                                isHeartMap[appUser] = true ////디테일 안보이는 원인후보3,,,
                                        }

                                        // 해당되는 대댓글들 가져오기
                                        //대댓글 fetch --------------------------------------------------------
                                        val urlDetail = "http://seonho.dothome.co.kr/Comment2_list.php"
                                        val urlComment2Heartfetch = "http://seonho.dothome.co.kr/comment2Heart_list2.php"
                                        val commentDetailAdapterMap = HashMap<Int, CommentDetailListAdapter>()
                                        val commentDetailItemsMap = HashMap<Int, ArrayList<CommentDetailItem>>()

                                        // 해당 댓글에서 사용자가 좋아요 누른 항목 가져오기
                                        var comment2HeartMap = HashMap<Int, ArrayList<Int>>()

                                        val requestComment2Heart = Login_Request(
                                            Request.Method.POST,
                                            urlComment2Heartfetch,
                                            { response ->
                                                comment2HeartMap.clear()
                                                if(!response.equals("Comment2 Heart Fetch fail")) {
                                                    if(!response.equals("no Comment2 Heart")) {
                                                        val jsonArray2Heart = JSONArray(response)
                                                        for (i in 0 until jsonArray2Heart.length()) {
                                                            val item = jsonArray2Heart.getJSONObject(i)
                                                            val item1 = item.getInt("comment_num")
                                                            val item2 = item.getInt("comment2_num")
                                                            Log.d(
                                                                "대댓글 좋아요 comment_num",
                                                                item1.toString()
                                                            )
                                                            Log.d(
                                                                "대댓글 좋아요 comment2_num",
                                                                item2.toString()
                                                            )
//                                                        var comment2HeartArray = comment2HeartMap[item1]
                                                            if(!comment2HeartMap.containsKey(item1)) comment2HeartMap[item1] = ArrayList<Int>()
                                                            comment2HeartMap[item1]!!.add(item2)

                                                            Log.d("냠냠", "$comment_num -  ${comment2HeartMap[item1]} - $item1 " )
                                                        }
                                                    }

                                                    val requestComment2List = Login_Request(
                                                        Request.Method.POST,
                                                        urlDetail,
                                                        { response ->
                                                            commentDetail_items.clear()
                                                            if (response != "Comment2 Fetch fail") {
                                                                val jsonArrayComment2 =
                                                                    JSONArray(response)

                                                                comment2_count[comment_num] = jsonArrayComment2.length()
                                                                for (i in 0 until jsonArrayComment2.length()) {
                                                                    val item =
                                                                        jsonArrayComment2.getJSONObject(
                                                                            i
                                                                        )
                                                                    val writerId = item.getString("id")
                                                                    if (!comment_user.containsKey(writerId)) comment_user[writerId] =
                                                                        comment_user.size + 1

                                                                    val comment_num =
                                                                        item.getInt("comment_num")
                                                                    val comment2 =
                                                                        item.getString("comment2")
                                                                    val comment2_time =
                                                                        item.getString("comment2_time")
                                                                    val comment2_num =
                                                                        item.getInt("comment2_num")

                                                                    val heart = item.getInt("heart")

                                                                    val writerUser = comment_user[writerId]!!
                                                                    var isHeart = false
                                                                    //여기서 이상함.. 대댓글 하나만 칠해지고 하나는 안칠해짐
                                                                    Log.d("대대댓", "${comment2HeartMap[comment_num]} / $comment2_num")
//                                                                if(comment2HeartMap[comment_num] == comment2_num) isHeart = true

                                                                    if(comment2HeartMap.contains(comment_num)){
                                                                        if(comment2HeartMap[comment_num]!!.contains(comment2_num)) {
                                                                            isHeart = true

                                                                        }
                                                                    }

                                                                    val comment2medalurl = "http://seonho.dothome.co.kr/Medal_Select.php"
                                                                    val id = intent.getStringExtra("id").toString()
                                                                    var comment2_medal_text : String = "bronze"

                                                                    val request = Login_Request(
                                                                        Request.Method.POST,
                                                                        comment2medalurl,
                                                                        { medal2response ->
                                                                            Log.d("medfsfdasd", medal2response)

                                                                            when (medal2response) {
                                                                                "king" -> {
                                                                                    comment2_medal_text =
                                                                                        "king"
                                                                                }
                                                                                "gold" -> {
                                                                                    comment2_medal_text =
                                                                                        "gold"
                                                                                }
                                                                                "silver" -> {
                                                                                    comment2_medal_text =
                                                                                        "silver"
                                                                                }
                                                                                else -> {
                                                                                    comment2_medal_text =
                                                                                        "bronze"
                                                                                }
                                                                            }

                                                                    val commentDetailItem =
                                                                        CommentDetailItem(
                                                                            id,
                                                                            writerId,
                                                                            writerUser,
                                                                            comment_num,
                                                                            comment2,
                                                                            comment2_medal_text,
                                                                            comment2_num,
                                                                            comment2_time,
                                                                            heart,
                                                                            isHeart
                                                                        )


                                                                    if(commentDetailItemsMap.contains(comment_num)){
                                                                        commentDetailItemsMap[comment_num]!!.add(commentDetailItem)
                                                                    }else{
                                                                        commentDetailItemsMap[comment_num] = ArrayList<CommentDetailItem>()
                                                                        commentDetailItemsMap[comment_num]!!.add(commentDetailItem)
                                                                    }

                                                                    if(commentDetailItemsMap[comment_num]!=null){
                                                                        commentDetailAdapterMap[comment_num] = CommentDetailListAdapter(commentDetailItemsMap[comment_num]!!)
                                                                        commentDetailAdapterMap[comment_num]?.setOnItemClickListener(
                                                                            object :
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
                                                                                        intent?.getStringExtra("id")
                                                                                            .toString()
                                                                                    val comment2Heart =
                                                                                        v.findViewById<CheckBox>(
                                                                                            R.id.imageView_comment2_like
                                                                                        )
                                                                                    val comment2HeartCnt =
                                                                                        v.findViewById<TextView>(
                                                                                            R.id.comment2_heartCnt
                                                                                        )

                                                                                    if (comment2Heart.isChecked) {
                                                                                        comment2HeartFlag =
                                                                                            "true"
                                                                                        comment2HeartCnt.text =
                                                                                            (comment2HeartCnt.text.toString()
                                                                                                .toInt() + 1).toString()
                                                                                    } else {
                                                                                        comment2HeartFlag =
                                                                                            "false"
                                                                                        comment2HeartCnt.text =
                                                                                            (comment2HeartCnt.text.toString()
                                                                                                .toInt() - 1).toString()
                                                                                    }

                                                                                    val request = Login_Request(
                                                                                        Request.Method.POST,
                                                                                        urlComment2Heart,
                                                                                        { response ->
                                                                                            Log.d(
                                                                                                "comment2Hart",
                                                                                                response
                                                                                            )

                                                                                            if (comment2Heart.isChecked) comment2HeartFlag =
                                                                                                "commentHeartUP"
                                                                                            else comment2HeartFlag =
                                                                                                "commentHeartDOWN"

                                                                                            val requestCnt =
                                                                                                Login_Request(
                                                                                                    Request.Method.POST,
                                                                                                    urlUpdateComment2Cnt,
                                                                                                    { responseLike ->
                                                                                                        if (!responseLike.equals(
                                                                                                                "update fail"
                                                                                                            )
                                                                                                        ) {
                                                                                                            // comment의 heart 개수 업데이트 성공
                                                                                                        } else {
                                                                                                            Toast.makeText(
                                                                                                                applicationContext,
                                                                                                                "lion heart fail",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            )
                                                                                                                .show()
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

                                                                                            val queue =
                                                                                                Volley.newRequestQueue(
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
                                                                                    val queue =
                                                                                        Volley.newRequestQueue(
                                                                                            applicationContext
                                                                                        )
                                                                                    queue.add(request)
                                                                                }

                                                                                override fun onItemDelete(
                                                                                    v: View,
                                                                                    data: CommentDetailItem,
                                                                                    pos: Int
                                                                                ) {
                                                                                    // 대댓글 테이블에서 찾아서 삭제
                                                                                    val urlDelete2 = "http://seonho.dothome.co.kr/Comment2Delete.php"
                                                                                    val requestDelete2 = Login_Request(
                                                                                        Request.Method.POST,
                                                                                        urlDelete2,
                                                                                        { responseDelete2 ->
                                                                                            if (responseDelete2 != "comment delete2 fail") {
                                                                                                // 댓글 카운트 찾아서 삭제
                                                                                                Log.d("90234", "대댓글 삭제")
                                                                                                val urlCommentDeleteCount = "http://seonho.dothome.co.kr/updateBoardCnt.php"
                                                                                                val requestDeleteCount = Login_Request(
                                                                                                    Request.Method.POST,
                                                                                                    urlCommentDeleteCount,
                                                                                                    { requestDeleteCount ->
                                                                                                        if (requestDeleteCount != "update fail") {
                                                                                                            Toast.makeText(
                                                                                                                baseContext,
                                                                                                                String.format("댓글이 삭제되었습니다."),
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            ).show()
                                                                                                            fetchCommentData()
                                                                                                        }

                                                                                                    }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                                                                                    hashMapOf(
                                                                                                        "board" to board,
                                                                                                        "post_num" to post_num,
                                                                                                        "flag" to "commentDOWN"
                                                                                                    )
                                                                                                )
                                                                                                val queue = Volley.newRequestQueue(applicationContext)
                                                                                                queue.add(requestDeleteCount)

                                                                                            }

                                                                                        }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                                                                        hashMapOf(
                                                                                            "board" to board,
                                                                                            "post_num" to post_num,
                                                                                            "comment_num" to data.comment_num.toString()
                                                                                        )
                                                                                    )
                                                                                    val queue = Volley.newRequestQueue(applicationContext)
                                                                                    queue.add(requestDelete2)
                                                                                }


                                                                            })
                                                                    }



                                                                    Log.d(
                                                                        "commmentDetailItem",
                                                                        "$id, $post_num, $comment_num,  $comment2_num, $comment2, $writerUser, $comment2_time , $heart"
                                                                    )
                                                                }, { Log.d("like Failed", "error......${error(applicationContext)}") },
                                                                hashMapOf(
                                                                    "id" to id
                                                                )
                                                                )
                                                                val queue = Volley.newRequestQueue(this)
                                                                queue.add(request)

                                                            }


                                                                if (!comment_user.containsKey(writerId)) comment_user[writerId] =
                                                                    comment_user.size + 1

                                                                val isHeart =
                                                                    if (isHeartMap.containsKey(appUser)) (isHeartMap[appUser]) else false

                                                                val medalurl = "http://seonho.dothome.co.kr/Medal_Select.php"
                                                                var id = intent.getStringExtra("id").toString()
                                                                var comment_medal_text : String = "bronze"

                                                                val request = Login_Request(
                                                                    Request.Method.POST,
                                                                    medalurl,
                                                                    { medalresponse ->
                                                                        Log.d("medfsfdasd", medalresponse)

                                                                        when (medalresponse) {
                                                                            "king" -> {
                                                                                comment_medal_text =
                                                                                    "king"
                                                                            }
                                                                            "gold" -> {
                                                                                comment_medal_text =
                                                                                    "gold"
                                                                            }
                                                                            "silver" -> {
                                                                                comment_medal_text =
                                                                                    "silver"
                                                                            }
                                                                            else -> {
                                                                                comment_medal_text =
                                                                                    "bronze"
                                                                            }
                                                                        }

                                                                val commentItem = CommentItem(
                                                                    id,
                                                                    writerId,
                                                                    comment_user[writerId]!!,
                                                                    comment,
                                                                    comment_medal_text,
                                                                    comment_num,
                                                                    comment_time,
                                                                    heart,
                                                                    isHeart,
                                                                    commentDetailAdapterMap[comment_num]
                                                                )
                                                                comment_items.add(commentItem)

                                                                // 댓글에 대댓글 붙이기 000000000000000000000000000000000000000000000000000
                                                                //각 댓글마다..
                                                                comment_items.sortBy { it.comment_num }
                                                                Commentadapter =
                                                                    CommentListAdapter(comment_items)
                                                                CommentRecyclerView.adapter =
                                                                    Commentadapter

                                                                //댓글 클릭 리스너 ++++++++++++++++++++++++++++++++++++++++++++++++
                                                                Commentadapter.setOnItemClickListener(
                                                                    object :
                                                                        CommentListAdapter.OnItemClickListener {
                                                                        override fun onItemClick(
                                                                            v: View,
                                                                            data: CommentItem,
                                                                            pos: Int
                                                                        ) {
                                                                            //댓글 눌렀을때. -> 대댓글
                                                                            comment_comment_posPresent =
                                                                                pos
                                                                            if (comment_comment_flag == true) {
                                                                                if (comment_comment_posPresent == comment_comment_posBefore) {
                                                                                    comment_comment_flag =
                                                                                        false
                                                                                    CommentRecyclerView.get(
                                                                                        comment_comment_posPresent
                                                                                    )
                                                                                        .findViewById<LinearLayout>(
                                                                                            R.id.comment_linearLayout
                                                                                        )
                                                                                        .setBackgroundColor(
                                                                                            Color.parseColor(
                                                                                                "#ffffff"
                                                                                            )
                                                                                        )
                                                                                } else {

                                                                                    CommentRecyclerView.get(
                                                                                        comment_comment_posBefore
                                                                                    )
                                                                                        .findViewById<LinearLayout>(
                                                                                            R.id.comment_linearLayout
                                                                                        )
                                                                                        .setBackgroundColor(
                                                                                            Color.parseColor(
                                                                                                "#ffffff"
                                                                                            )
                                                                                        )
                                                                                    CommentRecyclerView.get(
                                                                                        comment_comment_posPresent
                                                                                    )
                                                                                        .findViewById<LinearLayout>(
                                                                                            R.id.comment_linearLayout
                                                                                        )
                                                                                        .setBackgroundColor(
                                                                                            Color.parseColor(
                                                                                                "#308BE0C4"
                                                                                            )
                                                                                        )
                                                                                    comment_comment_posBefore =
                                                                                        pos
                                                                                }

                                                                                //                                    findViewById<LinearLayout>(R.id.comment_linearLayout).setBackgroundColor(Color.parseColor("#ffffff"))
                                                                                //자동 키보드 내리기
                                                                            } else {
                                                                                comment_comment_flag =
                                                                                    true
                                                                                comment_comment_posBefore =
                                                                                    pos
                                                                                comment_comment_posPresent =
                                                                                    pos

                                                                                CommentRecyclerView.get(
                                                                                    pos
                                                                                )
                                                                                    .findViewById<LinearLayout>(
                                                                                        R.id.comment_linearLayout
                                                                                    )
                                                                                    .setBackgroundColor(
                                                                                        Color.parseColor(
                                                                                            "#308BE0C4"
                                                                                        )
                                                                                    )
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
                                                                            var id =
                                                                                intent?.getStringExtra("id")
                                                                                    .toString()
                                                                            val commentHeart =
                                                                                v.findViewById<CheckBox>(
                                                                                    R.id.imageView_comment_like
                                                                                )
                                                                            val commentHeartCnt =
                                                                                v.findViewById<TextView>(
                                                                                    R.id.comment_heart_count
                                                                                )

                                                                            if (commentHeart.isChecked) {
                                                                                commentHeartFlag =
                                                                                    "true"
                                                                                commentHeartCnt.text =
                                                                                    (commentHeartCnt.text.toString()
                                                                                        .toInt() + 1).toString()
                                                                            } else {
                                                                                commentHeartFlag =
                                                                                    "false"
                                                                                commentHeartCnt.text =
                                                                                    (commentHeartCnt.text.toString()
                                                                                        .toInt() - 1).toString()
                                                                            }


                                                                            val request = Login_Request(
                                                                                Request.Method.POST,
                                                                                urlCommentHeart,
                                                                                { response ->
                                                                                    Log.d(
                                                                                        "commentHart",
                                                                                        response
                                                                                    )

                                                                                    var commentHeartFlag =
                                                                                        ""
                                                                                    if (commentHeart.isChecked) commentHeartFlag =
                                                                                        "commentHeartUP"
                                                                                    else commentHeartFlag =
                                                                                        "commentHeartDOWN"

                                                                                    val requestCnt =
                                                                                        Login_Request(
                                                                                            Request.Method.POST,
                                                                                            urlUpdateCommentCnt,
                                                                                            { responseLike ->
                                                                                                if (!responseLike.equals(
                                                                                                        "update fail"
                                                                                                    )
                                                                                                ) {
                                                                                                    // comment의 heart 개수 업데이트 성공
                                                                                                } else {
                                                                                                    Toast.makeText(
                                                                                                        applicationContext,
                                                                                                        "lion heart fail",
                                                                                                        Toast.LENGTH_SHORT
                                                                                                    )
                                                                                                        .show()
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

                                                                                    val queue =
                                                                                        Volley.newRequestQueue(
                                                                                            applicationContext
                                                                                        )
                                                                                    queue.add(requestCnt)

                                                                                    //좋아요 개수 fetch

                                                                                },
                                                                                {
                                                                                    Log.d(
                                                                                        "Comment failed",
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
                                                                                    "comment_num" to (pos + 1).toString(),
                                                                                    "flag" to commentHeartFlag.toString()
                                                                                )
                                                                            )
                                                                            val queue =
                                                                                Volley.newRequestQueue(
                                                                                    applicationContext
                                                                                )
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
                                                                                comment_comment_flag =
                                                                                    false
                                                                                findViewById<LinearLayout>(
                                                                                    R.id.comment_linearLayout
                                                                                ).setBackgroundColor(
                                                                                    Color.parseColor("#ffffff")
                                                                                )
                                                                                //자동 키보드 내리기
                                                                            } else {
                                                                                comment_comment_flag =
                                                                                    true
                                                                                comment_comment_posPresent =
                                                                                    pos
                                                                                findViewById<LinearLayout>(
                                                                                    R.id.comment_linearLayout
                                                                                ).setBackgroundColor(
                                                                                    Color.parseColor("#5085D6A4")
                                                                                )
                                                                                //자동 키보드 올리기
                                                                            }

                                                                        }

                                                                        override fun onItemDelete(
                                                                            v: View,
                                                                            data: CommentItem,
                                                                            pos: Int
                                                                        ) {
                                                                            // 대댓글 테이블에서 찾아서 삭제
                                                                            val urlDelete2 = "http://seonho.dothome.co.kr/Comment2Delete.php"
                                                                            val requestDelete2 = Login_Request(
                                                                                Request.Method.POST,
                                                                                urlDelete2,
                                                                                { responseDelete2 ->
                                                                                    if (responseDelete2 != "comment delete2 fail") {
                                                                                        // 댓글 테이블에서 찾아서 삭제
                                                                                        val urlDelete = "http://seonho.dothome.co.kr/CommentDelete.php"
                                                                                        val requestDelete = Login_Request(
                                                                                            Request.Method.POST,
                                                                                            urlDelete,
                                                                                            { responseDelete ->
                                                                                                if (responseDelete != "comment delete fail") {
                                                                                                    //board 테이블에서 댓글 개수 줄이기
                                                                                                    val urlCommentDeleteCount = "http://seonho.dothome.co.kr/updateBoardCnt.php"
                                                                                                    val requestDeleteCount = Login_Request(
                                                                                                        Request.Method.POST,
                                                                                                        urlCommentDeleteCount,
                                                                                                        { requestDeleteCount ->
                                                                                                            if (requestDeleteCount != "update fail") {
                                                                                                                Toast.makeText(
                                                                                                                    baseContext,
                                                                                                                    String.format("댓글이 삭제되었습니다."),
                                                                                                                    Toast.LENGTH_SHORT
                                                                                                                ).show()

//                                                                                                            for(i in 0 until comment_items.size){
//                                                                                                                if(comment_items.get(i).comment_num == data.comment_num)
//                                                                                                                    comment_items.removeAt(i)
//                                                                                                            }
//                                                                                                            findViewById<TextView>(R.id.textView_commentcount2).text = (findViewById<TextView>(R.id.textView_commentcount2).text.toString().toInt() - 1).toString()
//                                                                                                            Commentadapter =
//                                                                                                                CommentListAdapter(comment_items)
//                                                                                                            CommentRecyclerView.adapter =
//                                                                                                                Commentadapter

                                                                                                                fetchCommentData()
                                                                                                            }

                                                                                                        }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                                                                                        hashMapOf(
                                                                                                            "board" to board,
                                                                                                            "post_num" to post_num,
                                                                                                            "flag" to "commentDOWN"
                                                                                                        )
                                                                                                    )
                                                                                                    val queue = Volley.newRequestQueue(applicationContext)
                                                                                                    queue.add(requestDeleteCount)
                                                                                                }

                                                                                            }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                                                                            hashMapOf(
                                                                                                "board" to board,
                                                                                                "post_num" to post_num,
                                                                                                "comment_num" to data.comment_num.toString()
                                                                                            )
                                                                                        )
                                                                                        val queue = Volley.newRequestQueue(applicationContext)
                                                                                        queue.add(requestDelete)

                                                                                    }

                                                                                }, { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                                                                hashMapOf(
                                                                                    "board" to board,
                                                                                    "post_num" to post_num,
                                                                                    "comment_num" to data.comment_num.toString()
                                                                                )
                                                                            )
                                                                            val queue = Volley.newRequestQueue(applicationContext)
                                                                            queue.add(requestDelete2)


                                                                            // board에서 comment count 줄이기
                                                                        }
                                                                    })
                                                                    }, { Log.d("like Failed", "error......${error(applicationContext)}") },
                                                                    hashMapOf(
                                                                        "id" to id
                                                                    )
                                                                )
                                                                val queue = Volley.newRequestQueue(this)
                                                                queue.add(request)

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
                                                    queue.add(requestComment2List)
                                                } else{
                                                    Toast.makeText(
                                                        baseContext,
                                                        String.format("대댓글 좋아요fail"),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                            },
                                            { Log.d("Comment Failed", "error......${error(applicationContext)}") },
                                            hashMapOf(
                                                "post_num" to post_num,
                                                "board" to board,
                                                "id" to id
                                            )
                                        )


                                        val queue = Volley.newRequestQueue(this)
                                        queue.add(requestComment2Heart)
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
        var post_num = intent?.getStringExtra("num").toString()
        var id = intent?.getStringExtra("id").toString()
        val board = intent?.getStringExtra("board").toString()

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
                "board" to board,
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
        val post_num = intent?.getStringExtra("num").toString()

        val urlDelete = "http://seonho.dothome.co.kr/postDelete.php"
        val deleteHeart = "http://seonho.dothome.co.kr/heartDeleteOnPost.php"
        val deleteComment = "http://seonho.dothome.co.kr/commentDeleteOnPost.php"
        val deleteComment2 = "http://seonho.dothome.co.kr/comment2DeleteOnPost.php"
        val deleteScrap = "http://seonho.dothome.co.kr/scrapDeleteOnPost.php"
        val deleteCommentHeart = "http://seonho.dothome.co.kr/deleteCommentHeartDeleteOnPost.php"
        val deleteCommentHeart2 = "http://seonho.dothome.co.kr/deleteComment2HeartDeleteOnPost.php"

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

                    //관련 하트 지우기
                    val requestDeleteHeart = Login_Request(
                        Request.Method.POST,
                        deleteHeart,
                        { responseHeart ->
                            if (!responseHeart.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestDeleteHeart)

                    //관련 댓글 지우기
                    val requestDeleteComment = Login_Request(
                        Request.Method.POST,
                        deleteComment,
                        { responseComment ->
                            if (!responseComment.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    queue.add(requestDeleteComment)

                    //관련 대댓글 지우기
                    val requestDeleteComment2 = Login_Request(
                        Request.Method.POST,
                        deleteComment2,
                        { responseComment2 ->
                            if (!responseComment2.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    queue.add(requestDeleteComment2)

                    //관련 북마크 삭제
                    val requestDeleteScrap = Login_Request(
                        Request.Method.POST,
                        deleteScrap,
                        { responseScrap ->
                            if (!responseScrap.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    queue.add(requestDeleteScrap)

                    //관련 댓글 좋아요 삭제
                    val requestDeleteCommentHeart = Login_Request(
                        Request.Method.POST,
                        deleteCommentHeart,
                        { responseCommentHeart ->
                            if (!responseCommentHeart.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    queue.add(requestDeleteCommentHeart)

                    //관련 대댓글 좋아요 삭제
                    val requestDeleteCommentHeart2 = Login_Request(
                        Request.Method.POST,
                        deleteCommentHeart2,
                        { responseCommentHeart2 ->
                            if (!responseCommentHeart2.equals("update fail")) {

                            } else {

                            }
                        }, { Log.d("lion heart Failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "board" to board,
                            "post_num" to post_num
                        )
                    )
                    queue.add(requestDeleteCommentHeart2)


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
        var post_num = intent?.getStringExtra("num").toString()
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

                                // medal ---------------------------------------------------------------------------
                                val medalurl = "http://seonho.dothome.co.kr/Medal.php"
                                var writerid = intent.getStringExtra("writerId").toString()

                                val request = Login_Request(
                                    Request.Method.POST,
                                    medalurl,
                                    { response ->
                                        Log.d("medlall", response)
                                        if(response != "medal fail") {
                                            Log.d("userGraade", "up")
                                        }

                                    }, { Log.d("like Failed", "error......${error(applicationContext)}") },
                                    hashMapOf(
                                        "id" to writerid
                                    )
                                )
                                val queue = Volley.newRequestQueue(this)
                                queue.add(request)


                                Log.d("writerId", "$writerid, $id")

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

                } else {

                    Toast.makeText(
                        applicationContext,
                        "like fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
//    /** DynamicLink */
//    private fun initDynamicLink() {
//        val dynamicLinkData = intent.extras
//        if (dynamicLinkData != null) {
//            var dataStr = "DynamicLink 수신받은 값\n"
//            for (key in dynamicLinkData.keySet()) {
//                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
//            }
//
//        }
//    }
    // 댓글 request ------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun CommentRequest(comment_num : Int) {
        //FCM 서비스 도전 ===================================================
//        val token = FirebaseMessagingService().getFirebaseToken()
        val key = "AIzaSyCqmTtARfMVzB5TFE2Je6UOwreSNiHk_lU"
//        initDynamicLink()

        // ==================================================================
        val receiverIntent: Intent = Intent(
            this@BoardDetail,
            AlarmReceiver_comment::class.java
        )
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@BoardDetail,
                BoardDetail.ALARM_REQUEST_CODE, receiverIntent,
                PendingIntent.FLAG_MUTABLE
            )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var id = intent?.getStringExtra("id").toString()
        val device_id = intent.getStringExtra("device_id").toString()
        var writerId = intent.getStringExtra("writerId").toString() //게시물을 작성한 유저의 아이디
        var board = intent?.getStringExtra("board").toString()
        var post_num = intent?.getStringExtra("num").toString()
        var comment = findViewById<EditText>(R.id.Comment_editText).text.toString()

        Log.d("as456", post_num)

        val url = "http://seonho.dothome.co.kr/Comment.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"
        val urlNotification = "http://seonho.dothome.co.kr/notification_comment.php"
        val urlNotification_select = "http://seonho.dothome.co.kr/notification_comment_select.php"
        val urlNotification_select2 = "http://seonho.dothome.co.kr/notification_comment_select2.php"

        val noti_FCM = "http://seonho.dothome.co.kr/notification_FCM.php"

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

                    UpdateGrade()

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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(time: String, alarm_code: Int, content: String){
        AlarmFunctions_comment(applicationContext).callAlarm(time, alarmCode, content)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    //대댓글 request-------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun Comment2Request(comment_num : Int, comment2_num : Int) {
        var id = intent?.getStringExtra("id").toString()
        var post_num = intent?.getStringExtra("num").toString()
        var board = intent?.getStringExtra("board").toString()
        var comment2 = findViewById<EditText>(R.id.Comment_editText).text.toString()


        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val comment2_time = current.format(formatter)

        val url = "http://seonho.dothome.co.kr/Comment2.php"
        val urlUpdateCnt = "http://seonho.dothome.co.kr/updateBoardCnt.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("Comment2 fail")) {
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
                        String.format("대댓글이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    UpdateGrade()

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
        var post_num = intent?.getStringExtra("num").toString()
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


    fun UpdateGrade() {
        val medalurl = "http://seonho.dothome.co.kr/Grade.php"
        var nickname = intent.getStringExtra("nickname").toString()

        val request = Login_Request(
            Request.Method.POST,
            medalurl,
            { response ->
                Log.d("gradeeeeeeeeeee", response)
                Log.d("gradeNickname", nickname)
                if(response != "grade fail") {
                    Log.d("userGraade", "up")
                }

            }, { Log.d("grade Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "nickname" to nickname
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
