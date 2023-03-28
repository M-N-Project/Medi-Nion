package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.board_home.refresh_layout
import kotlinx.android.synthetic.main.board_profile_home.*
import kotlinx.android.synthetic.main.board_scroll_paging.*
import kotlinx.android.synthetic.main.sign_up.view.*
import org.json.JSONArray
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*


private var items =ArrayList<BoardItem>()
private var all_items = ArrayList<BoardItem>()
private val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
private var scroll_count = 1
//val viewModel = BoardViewModel()
//lateinit var adapter : BoardListAdapter
private var adapter = BoardListAdapter(items)
private var scrollFlag = false
private var itemIndex = ArrayList<Int>()

class Board_profile : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart() //프레그먼트로 생길 문제들은 추후에 생각하기,,

        var board_select = findViewById<TextView>(R.id.board_select_profile) //게시판 종류 선택
        var profileType = intent.getStringExtra("profileMenuType")
        if(profileType=="post")
            fetchPost(board_select.text.toString())
        else if(profileType=="scrap")
            fetchScrap(board_select.text.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_profile_home)

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경

        var board_select = findViewById<TextView>(R.id.board_select_profile) //게시판 종류 선택
        var select_RadioGroup = findViewById<RadioGroup>(R.id.select_RadioGroup_profile) // 게시판 종류 라디오 그룹
        var free_RadioBtn = findViewById<RadioButton>(R.id.free_RadioBtn_profile)
        var job_RadioBtn = findViewById<RadioButton>(R.id.job_RadioBtn_profile)
        var department_RadioBtn = findViewById<RadioButton>(R.id.department_RadioBtn_profile)
        var my_hospital_RadioBtn = findViewById<RadioButton>(R.id.my_hospital_RadioBtn_profile)
        var market_RadioBtn = findViewById<RadioButton>(R.id.market_RadioBtn_profile)
        var QnA_RadioBtn = findViewById<RadioButton>(R.id.QnA_RadioBtn_profile)

        var profileType = intent.getStringExtra("profileMenuType")
        if(profileType=="post")
            fetchPost(board_select.text.toString())
        else if(profileType=="scrap")
            fetchScrap(board_select.text.toString())

        board_select.text = "자유 게시판"
        var boardInit = intent.getStringExtra("board")
        if(boardInit != null) board_select.text = boardInit

        if(board_select.text == "자유 게시판") free_RadioBtn.isChecked = true
        if(board_select.text == "직종별 게시판") job_RadioBtn.isChecked = true
        if(board_select.text == "진료과별 게시판") department_RadioBtn.isChecked = true
        if(board_select.text == "우리 병원 게시판") my_hospital_RadioBtn.isChecked = true
        if(board_select.text == "장터 게시판") market_RadioBtn.isChecked = true
        if(board_select.text == "QnA 게시판") QnA_RadioBtn.isChecked = true

        board_select.setOnClickListener {
            if(select_RadioGroup.visibility == View.GONE){
                select_RadioGroup.visibility = View.VISIBLE
                select_RadioGroup.bringToFront()
            }
            else{
                select_RadioGroup.visibility = View.GONE
            }

            free_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "자유 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }

            job_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "직종별 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }

            department_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "진료과별 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }

            my_hospital_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "우리 병원 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }

            market_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "장터 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }

            QnA_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "QnA 게시판"
                if(profileType=="post")
                    fetchPost(board_select.text.toString())
                else if(profileType=="scrap")
                    fetchScrap(board_select.text.toString())
            }
        }


        refresh_layout.setOnRefreshListener { //새로고침
            Log.d("omg", "hello refresh")

            try {
                //TODO 액티비티 화면 재갱신 시키는 코드
                val intent = intent
                intent.putExtra("board", board_select.text.toString())
                finish() //현재 액티비티 종료 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                startActivity(intent) //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기

                refresh_layout.isRefreshing = false //새로고침 없애기
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        items.clear()
        all_items.clear()

        boardRecyclerView_profile.setLayoutManager(boardRecyclerView_profile.layoutManager);

        boardRecyclerView_profile.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(scrollFlag==false){

                    if (!boardRecyclerView_profile.canScrollVertically(-1)) { //맨 위

                    } else if (!boardRecyclerView_profile.canScrollVertically(1)) { //맨 아래
                        //로딩
                        if(all_items.size > 20){
                            scrollFlag = true

                            Log.d("attention", "let it be")
                            var progressBar : ProgressBar = findViewById(R.id.progressBar2)
                            progressBar.visibility = View.VISIBLE

                            Handler(Looper.getMainLooper()).postDelayed({
                                progressBar.visibility = View.INVISIBLE
                            }, 2000)


                            if((all_items.size - item_count*scroll_count) > 20){
                                for (i in (item_count * scroll_count) + (item_count-1)  downTo   (item_count * scroll_count) + 0) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.
                                }

                                var recyclerViewState = boardRecyclerView_profile.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView_profile.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView_profile.layoutManager?.onRestoreInstanceState(recyclerViewState)

                                scrollFlag = false
                            }
                            else{
                                for (i  in all_items.size-1  downTo   (item_count* scroll_count)) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.

                                }
                                var recyclerViewState = boardRecyclerView_profile.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView_profile.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView_profile.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }

                            scroll_count ++
                        }
                    }
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchPost(boardSelect : String) {
        // url to post our data
        var id = intent.getStringExtra("id")
        var board :String = ""
        board = intent.getStringExtra("board").toString()
        var userType :String = ""
        userType = intent.getStringExtra("userType").toString()
        var userDept :String = ""
        userDept = intent.getStringExtra("userDept").toString()
        val urlBoard = "http://seonho.dothome.co.kr/BoardProfilePost.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)

                val noItemText = findViewById<TextView>(R.id.no_myitem)
                if (jsonArray.length() == 0) {
                    noItemText.visibility = View.VISIBLE
                    noItemText.text = "내가 쓴 게시물이 없습니다."
                    noItemText.bringToFront()
                } else noItemText.visibility = View.GONE


                items.clear()
                all_items.clear()

                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val board_time = item.getString("time")
                    val image = item.getString("image")
                    var heart = item.getInt("heart")
                    var comment = item.getInt("comment")
                    var bookmark = item.getInt("bookmark")

                    val simpleTime = timeDiff(board_time)

                    val boardItem = BoardItem(num, title, content, simpleTime, image, heart, comment, bookmark)

                    if(i >= jsonArray.length() - item_count*scroll_count){
                        items.add(boardItem)
                        itemIndex.add(num) //앞에다가 추가.
                    }

                    all_items.add(boardItem)
                }

                var recyclerViewState = boardRecyclerView_profile.layoutManager?.onSaveInstanceState()
                var new_items = ArrayList<BoardItem>()
                new_items.addAll(items)
                adapter = BoardListAdapter(new_items)
                boardRecyclerView_profile.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                boardRecyclerView_profile.layoutManager?.onRestoreInstanceState(recyclerViewState);

                var detailId : String = ""
                var detailTitle : String = ""
                var detailContent : String = ""
                var detailTime : String = ""
                var detailImg : String = ""

                //게시판 상세
                adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, data: BoardItem, pos: Int) {
                        Log.d("0998123", "$board // ${data.num}")
                        val request = Login_Request(
                            Request.Method.POST,
                            urlDetail,
                            { response ->

                                if(response!="Detail Info Error"){
                                    val jsonArray = JSONArray(response)
                                    items.clear()
                                    for (i in jsonArray.length()-1  downTo  0) {
                                        val item = jsonArray.getJSONObject(i)

                                        detailId = item.getString("id")
                                        detailTitle = item.getString("title")
                                        detailContent = item.getString("content")
                                        detailTime = item.getString("time")
                                        detailImg = item.getString("image")

                                        val intent = Intent(applicationContext, BoardDetail::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                        intent.putExtra("board", boardSelect)
                                        intent.putExtra("num", data.num)
                                        intent.putExtra("id", id)
                                        intent.putExtra("writerId", detailId)
                                        intent.putExtra("title", detailTitle)
                                        intent.putExtra("content", detailContent)
                                        intent.putExtra("time", detailTime)
                                        intent.putExtra("image", detailImg)
                                        intent.putExtra("userType", userType)
                                        intent.putExtra("userDept", userDept)
                                        startActivity(intent)
                                    }


                                }

                            }, { Log.d("login failed", "error......${error(applicationContext)}") },
                            hashMapOf(
                                "board" to boardSelect,
                                "post_num" to data.num.toString()
                            )
                        )
                        val queue = Volley.newRequestQueue(applicationContext)
                        queue.add(request)
                    }

                })


            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to id.toString(),
                "board" to boardSelect
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchScrap(boardSelect : String) {
        // url to post our data
        var id = intent.getStringExtra("id")
        var board :String = ""
        board = intent.getStringExtra("board").toString()
        var userType :String = ""
        userType = intent.getStringExtra("userType").toString()
        var userDept :String = ""
        userDept = intent.getStringExtra("userDept").toString()
        val urlScrap = "http://seonho.dothome.co.kr/BoardProfileScrapNum.php"
        val urlBoard = "http://seonho.dothome.co.kr/BoardProfileScrap.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"
        Log.d("-0123", "kiki")

        val requestScrapNum = Board_Request(
            Request.Method.POST,
            urlScrap,
            { responseScrapNum ->
                if(responseScrapNum != "Bookmark Profile Fetch Fail"){
                    val jsonArrayScrapNum = JSONArray(responseScrapNum)

                    items.clear()
                    all_items.clear()

                    val noItemText = findViewById<TextView>(R.id.no_myitem)
                    if (jsonArrayScrapNum.length() == 0) {
                        noItemText.visibility = View.VISIBLE
                        noItemText.text = "스크랩한 게시물이 없습니다."
                        noItemText.bringToFront()

                        adapter = BoardListAdapter(items)
                        boardRecyclerView_profile.adapter = adapter
                    } else {
                        noItemText.visibility = View.GONE

                        Log.d("9091233", jsonArrayScrapNum.length().toString())

                        for (i in jsonArrayScrapNum.length() - 1 downTo 0) {
                            val item = jsonArrayScrapNum.getJSONObject(i)

                            val post_num = item.getInt("post_num")
                            val count = item.getInt("count")

                            Log.d("099123", "$post_num ,, $boardSelect")

                            val requestScrapBoard = Board_Request(
                                Request.Method.POST,
                                urlBoard,
                                { responseScrapBoard ->
                                    Log.d("099123", responseScrapBoard)
                                    val jsonArrayScrapBoard = JSONArray(responseScrapBoard)

                                    items.clear()
                                    all_items.clear()

                                    for (i in jsonArrayScrapBoard.length()-1  downTo  0) {
                                        val item = jsonArrayScrapBoard.getJSONObject(i)

                                        val num = item.getInt("num")
                                        val title = item.getString("title")
                                        val content = item.getString("content")
                                        val board_time = item.getString("time")
                                        val image = item.getString("image")
                                        var heart = item.getInt("heart")
                                        var comment = item.getInt("comment")
                                        var bookmark = item.getInt("bookmark")

                                        val simpleTime = timeDiff(board_time)

                                        val boardItem = BoardItem(num, title, content, simpleTime, image, heart, comment, bookmark)

                                        if(i >= jsonArrayScrapBoard.length() - item_count*scroll_count){
                                            items.add(boardItem)
                                            itemIndex.add(num) //앞에다가 추가.
                                        }

                                        all_items.add(boardItem)
                                    }

                                    var recyclerViewState = boardRecyclerView_profile.layoutManager?.onSaveInstanceState()
                                    var new_items = ArrayList<BoardItem>()
                                    new_items.addAll(items)
                                    adapter = BoardListAdapter(new_items)
                                    boardRecyclerView_profile.adapter = adapter
                                    adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                    boardRecyclerView_profile.layoutManager?.onRestoreInstanceState(recyclerViewState);


                                    var detailId : String = ""
                                    var detailTitle : String = ""
                                    var detailContent : String = ""
                                    var detailTime : String = ""
                                    var detailImg : String = ""

                                    //게시판 상세
                                    adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
                                        override fun onItemClick(v: View, data: BoardItem, pos: Int) {
                                            val request = Login_Request(
                                                Request.Method.POST,
                                                urlDetail,
                                                { response ->
                                                    if(response!="Detail Info Error"){
                                                        val jsonArray = JSONArray(response)
                                                        items.clear()
                                                        for (i in jsonArray.length()-1  downTo  0) {
                                                            val item = jsonArray.getJSONObject(i)

                                                            detailId = item.getString("id")
                                                            detailTitle = item.getString("title")
                                                            detailContent = item.getString("content")
                                                            detailTime = item.getString("time")
                                                            detailImg = item.getString("image")

                                                            val intent = Intent(applicationContext, BoardDetail::class.java)
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                                            intent.putExtra("board", board)
                                                            intent.putExtra("num", data.num)
                                                            intent.putExtra("id", id)
                                                            intent.putExtra("writerId", detailId)
                                                            intent.putExtra("title", detailTitle)
                                                            intent.putExtra("content", detailContent)
                                                            intent.putExtra("time", detailTime)
                                                            intent.putExtra("image", detailImg)
                                                            intent.putExtra("userType", userType)
                                                            intent.putExtra("userDept", userDept)
                                                            startActivity(intent)
                                                        }


                                                    }

                                                }, { Log.d("login failed", "error......${error(applicationContext)}") },
                                                hashMapOf(
                                                    "board" to boardSelect,
                                                    "post_num" to post_num.toString()
                                                )
                                            )
                                            val queue = Volley.newRequestQueue(applicationContext)
                                            queue.add(request)
                                        }

                                    })


                                }, { Log.d("login failed", "error......${error(applicationContext)}") },
                                hashMapOf(
                                    "post_num" to post_num.toString(),
                                    "board" to boardSelect
                                )
                            )
                            val queue = Volley.newRequestQueue(this)
                            queue.add(requestScrapBoard)
                        }
                    }


                }

            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to id.toString(),
                "board" to boardSelect
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(requestScrapNum)



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