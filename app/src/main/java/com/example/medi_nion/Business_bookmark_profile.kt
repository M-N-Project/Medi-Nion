package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*




class Business_bookmark__profile : AppCompatActivity() {
    private lateinit var BusinessBoardRecyclerView : RecyclerView
    private lateinit var BusinessSubRecycler : RecyclerView
    private lateinit var activity : Activity
    var items = ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    var new_items = ArrayList<BusinessBoardItem>()
    var adapter = BusinessProfileRecyclerAdapter(items)  //문제야 문제~

    var profileImg = "" //프로필 이미지

    var num = 0 //비즈니스 채널 번호
    var writerId = ""
    var channel_name = ""
    var title = "" //비즈니스 채널명
    var content = "" //비즈니스 채널 내용
    var time = "" //비즈니스 채널 게시글 업로드 시간
    var image1 = "" //비즈니스 채널 사진 1
    var image2 = "" //비즈니스 채널 사진 2
    var image3 = "" //비즈니스 채널 사진 3
    var isHeart = false // 좋아요 정보
    var isBookmark = false // 북마크 정보
    var isSub = false
    var scrollFlag = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_bookmark)

        BusinessBoardRecyclerView = findViewById<RecyclerView>(R.id.BusinessBoardRecyclerView)
        BusinessBoardRecyclerView.adapter = adapter

        fetchPost()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchPost() {
        // url to post our data
        var appUser = intent.getStringExtra("id").toString()
        val urlBookmarkNum = "http://seonho.dothome.co.kr/BusinessProfileBookmarkNum.php"
        val urlBoard = "http://seonho.dothome.co.kr/BusinessProfileBookmark.php"
        val urlBookmark = "http://seonho.dothome.co.kr/BusinessBookmark_list.php"
        val urlLike = "http://seonho.dothome.co.kr/BusinessLike_list.php"

        val requestNum = Board_Request(
            Request.Method.POST,
            urlBookmarkNum,
            { responseNum ->
                Log.d("-=1231", responseNum)
                if(responseNum != "Business Bookmark Fetch Fail"){
                    val jsonArrayNum = JSONArray(responseNum)

                    items.clear()
                    all_items.clear()


                    val noItemText = findViewById<TextView>(R.id.no_myitem)
                    if (jsonArrayNum.length() == 0) {
                        noItemText.visibility = View.VISIBLE
                        noItemText.text = "북마크 한 비즈니스 게시물이 없습니다."
                        noItemText.bringToFront()

//                        adapter = BusinessRecyclerAdapter(items)
//                        BusinessBoardRecyclerView.adapter = adapter
                    } else {
                        noItemText.visibility = View.GONE
                        for (i in jsonArrayNum.length() - 1 downTo 0) {
                            val itemNum = jsonArrayNum.getJSONObject(i)

                            val post_num = itemNum.getInt("post_num")
                            val count = itemNum.getInt("count")

                            //여기까진 정상
                            Log.d("099123", "$post_num ,," )

                            items.clear()
                            all_items.clear()

                            val request = Board_Request(
                                Request.Method.POST,
                                urlBoard,
                                { response ->
                                    Log.d("-=1231", response)
                                    if (response != "business board list fail") {
                                        if (response != "business board no Item") {
                                            val jsonArray = JSONArray(response)
                                            items.clear()
                                            all_items.clear()


                                            for (i in jsonArray.length() - 1 downTo 0) {
                                                val item = jsonArray.getJSONObject(i)

                                                val num = item.getInt("num")
                                                val writerId = item.getString("id")
                                                val channel_name = item.getString("channel_name")
                                                val title = item.getString("title")
                                                val content = item.getString("content")
                                                val time = item.getString("time")
                                                val image1 = item.getString("image1")
                                                val image2 = item.getString("image2")
                                                val image3 = item.getString("image3")

                                                val urlProfile = "http://seonho.dothome.co.kr/BusinessProfile.php"

                                                val request = Board_Request(
                                                    Request.Method.POST,
                                                    urlProfile,
                                                    { response ->
                                                        if (!response.equals("business profile fail")) {
                                                            val jsonArray = JSONArray(response)

                                                            for (i in jsonArray.length() - 1 downTo 0) {
                                                                val item = jsonArray.getJSONObject(i)

                                                                val image_profile =
                                                                    item.getString("Channel_Profile_Img")

                                                                val bookfetchrequest = Login_Request(
                                                                    Request.Method.POST,
                                                                    urlBookmark,
                                                                    { responseBookmark ->
                                                                        if (responseBookmark.equals("Success Bookmark")) {
                                                                            isBookmark = true
                                                                        } else if (responseBookmark.equals("No Bookmark")) {
                                                                            isBookmark = false
                                                                        }
                                                                        val likerequest = Login_Request(
                                                                            Request.Method.POST,
                                                                            urlLike,
                                                                            { responseLike ->
                                                                                if (responseLike.equals("Success Heart")) {
                                                                                    isHeart = true
                                                                                } else if (responseLike.equals("No Heart")) {
                                                                                    isHeart = false
                                                                                }

                                                                                val BusinessItem = BusinessBoardItem(
                                                                                    num,
                                                                                    writerId,
                                                                                    image_profile,
                                                                                    channel_name,
                                                                                    title,
                                                                                    content,
                                                                                    time,
                                                                                    image1,
                                                                                    image2,
                                                                                    image3,
                                                                                    isHeart,
                                                                                    isBookmark,
                                                                                    false
                                                                                )
                                                                                items.add(BusinessItem)
                                                                                all_items.add(BusinessItem)

//                                                            var recyclerViewState = BusinessBoardRecyclerView.layoutManager?.onSaveInstanceState()
                                                                                new_items.clear()
                                                                                new_items.addAll(items)
                                                                                adapter =
                                                                                    BusinessProfileRecyclerAdapter(new_items)

                                                                                BusinessBoardRecyclerView.adapter =
                                                                                    adapter
                                                                                adapter.stateRestorationPolicy =
                                                                                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT
//                                                            BusinessBoardRecyclerView.layoutManager?.onRestoreInstanceState( recyclerViewState );

                                                                                adapter.setOnItemClickListener(
                                                                                    object :
                                                                                        BusinessProfileRecyclerAdapter.OnItemClickListener {
                                                                                        override fun onProfileClick(
                                                                                            v: View,
                                                                                            data: BusinessBoardItem,
                                                                                            pos: Int
                                                                                        ) {
                                                                                            val intent =
                                                                                                Intent(
                                                                                                    applicationContext,
                                                                                                    BusinessProfileActivity::class.java
                                                                                                )
                                                                                            intent.putExtra(
                                                                                                "appUser",
                                                                                                appUser
                                                                                            )
                                                                                            intent.putExtra(
                                                                                                "channel_name",
                                                                                                data.channel_name
                                                                                            )
                                                                                            startActivity(intent)
                                                                                        }

                                                                                        override fun onItemHeart(
                                                                                            v: View,
                                                                                            data: BusinessBoardItem,
                                                                                            pos: Int
                                                                                        ) {
                                                                                            val heartrequest =
                                                                                                Board_Request(
                                                                                                    Request.Method.POST,
                                                                                                    "http://seonho.dothome.co.kr/BusinessLike.php",
                                                                                                    { response ->
                                                                                                        if (response != "Heart fail") {
                                                                                                            data.isHeart =
                                                                                                                !data.isHeart
                                                                                                            Toast.makeText(
                                                                                                                applicationContext,
                                                                                                                "좋아요 완료",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            )
                                                                                                                .show()
                                                                                                        } else Log.d(
                                                                                                            "heartrequest fail",
                                                                                                            response
                                                                                                        )
                                                                                                    },
                                                                                                    {
                                                                                                        Log.d(
                                                                                                            "b-heart failed",
                                                                                                            "error......${
                                                                                                                this?.let { it1 ->
                                                                                                                    error(
                                                                                                                        it1
                                                                                                                    )
                                                                                                                }
                                                                                                            }"
                                                                                                        )
                                                                                                    },
                                                                                                    hashMapOf(
                                                                                                        "id" to appUser,
                                                                                                        "post_num" to data.post_num.toString(),
                                                                                                        "flag" to (!data.isHeart).toString()
                                                                                                    )
                                                                                                )

                                                                                            val queue =
                                                                                                Volley.newRequestQueue(
                                                                                                    applicationContext
                                                                                                )
                                                                                            queue.add(heartrequest)
                                                                                        }

                                                                                        override fun onItemBook(
                                                                                            v: View,
                                                                                            data: BusinessBoardItem,
                                                                                            pos: Int
                                                                                        ) {
                                                                                            val bookrequest =
                                                                                                Board_Request(
                                                                                                    Request.Method.POST,
                                                                                                    "http://seonho.dothome.co.kr/BusinessBookmark.php",
                                                                                                    { response ->
                                                                                                        if (response != "Bookmark fail") {
                                                                                                            data.isBookm =
                                                                                                                !data.isBookm
                                                                                                            Toast.makeText(
                                                                                                                applicationContext,
                                                                                                                "북마크 완료",
                                                                                                                Toast.LENGTH_SHORT
                                                                                                            )
                                                                                                                .show()
                                                                                                        } else {
                                                                                                            Log.d(
                                                                                                                "bookrequest fail",
                                                                                                                response
                                                                                                            )
                                                                                                        }
                                                                                                    },
                                                                                                    {
                                                                                                        Log.d(
                                                                                                            "b-bookmark failed",
                                                                                                            "error......${
                                                                                                                this?.let { it1 ->
                                                                                                                    error(
                                                                                                                        it1
                                                                                                                    )
                                                                                                                }
                                                                                                            }"
                                                                                                        )
                                                                                                    },
                                                                                                    hashMapOf(
                                                                                                        "id" to appUser,
                                                                                                        "post_num" to data.post_num.toString(),
                                                                                                        "flag" to (!data.isBookm).toString()
                                                                                                    )
                                                                                                )

                                                                                            val queue =
                                                                                                Volley.newRequestQueue(
                                                                                                    applicationContext
                                                                                                )
                                                                                            queue.add(bookrequest)

                                                                                        }
                                                                                    })

                                                                            },
                                                                            {
                                                                                Log.d(
                                                                                    "login failed",
                                                                                    "error......${
                                                                                        this?.let { it1 ->
                                                                                            error(
                                                                                                it1
                                                                                            )
                                                                                        }
                                                                                    }"
                                                                                )
                                                                            },
                                                                            hashMapOf(
                                                                                "id" to appUser,
                                                                                "post_num" to num.toString()
                                                                            )
                                                                        )
                                                                        val queue =
                                                                            Volley.newRequestQueue(applicationContext)
                                                                        queue.add(likerequest)
                                                                    },
                                                                    {
                                                                        Log.d(
                                                                            "login failed",
                                                                            "error......${
                                                                                this?.let { it1 ->
                                                                                    error(
                                                                                        it1
                                                                                    )
                                                                                }
                                                                            }"
                                                                        )
                                                                    },
                                                                    hashMapOf(
                                                                        "id" to appUser,
                                                                        "post_num" to num.toString()
                                                                    )
                                                                )
                                                                val queue =
                                                                    Volley.newRequestQueue(applicationContext)
                                                                queue.add(bookfetchrequest)
                                                            }
                                                        }

                                                    },
                                                    {
                                                        Log.d(
                                                            "login failed",
                                                            "error......${applicationContext.let { it1 -> error(it1) }}"
                                                        )
                                                    },
                                                    hashMapOf(
                                                        "id" to writerId
                                                    )
                                                )
                                                request.retryPolicy = DefaultRetryPolicy(
                                                    0,
                                                    -1,
                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                )
                                                val queue = Volley.newRequestQueue(applicationContext)
                                                queue.add(request)


                                            }
                                        } else Log.d("fffffffail", "business board no Item")
                                    } else Log.d("fffffffffffffail", "business board list fail")
                                }, {
                                    Log.d(
                                        "login failed",
                                        "error......${applicationContext?.let { it1 -> error(it1) }}"
                                    )
                                },
                                hashMapOf(
                                    "post_num" to post_num.toString()
                                )
                            )
                            val queue = Volley.newRequestQueue(applicationContext)
                            queue.add(request)
                        }

                    }
                }

            }, {
                Log.d(
                    "login failed",
                    "error......${applicationContext?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf(
                "id" to appUser
            )
        )
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(requestNum)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Millis(postTime : String) : Long {
        // YY-MM-DD HH:MM:SS

        //val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd, hh:mm:ss")
        //val date = LocalDateTime.parse(dateString, formatter)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date1: Date = simpleDateFormat.parse(postTime)
        return date1.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeDiff(postTime : String): String {
        var SEC = 60
        var MIN = 60
        var HOUR = 24
        var DAY = 30
        var MONTH = 12

        val curTime = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR"))
        val cur: String = simpleDateFormat.format(Date(curTime))

        val newPostTime = Millis(postTime)
        var diffTime = (curTime - newPostTime)/1000
        var msg: String = ""

        if (diffTime  < SEC) {
            msg = "방금 전";
        } else if ((diffTime / SEC) < MIN) {
            msg = (diffTime / SEC).toString() + "분 전";
        } else if (((diffTime / SEC) / MIN) < HOUR) {
            msg = ((diffTime / SEC) / MIN).toString() + "시간 전";
        } else if ((((diffTime / SEC) / MIN) / HOUR) < DAY) {
            msg = (((diffTime / SEC) / MIN) / HOUR).toString() + "일 전";
        } else if (((((diffTime / SEC) / MIN) / HOUR) / DAY) < MONTH) {
            msg = ((((diffTime / SEC) / MIN) / HOUR) / DAY).toString() + "달 전";
        } else {
            msg = (((((diffTime / SEC) / MIN) / HOUR) / DAY) / MONTH ).toString() + "년 전"
        }

        return msg
    }

}