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
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*


var items =ArrayList<BoardItem>()
var all_items = ArrayList<BoardItem>()
val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
var scroll_count = 1
//val viewModel = BoardViewModel()
//lateinit var adapter : BoardListAdapter
var adapter = BoardListAdapter(items)
lateinit var mJsonString: String
var errorString: String? = null
var scrollFlag = false
var itemIndex = ArrayList<Int>()

class Board : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart() //프레그먼트로 생길 문제들은 추후에 생각하기,,

        fetchData()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경

        items.clear()
        all_items.clear()

        boardRecyclerView.setLayoutManager(boardRecyclerView.layoutManager);

        var id = intent.getStringExtra("id")
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        writingFAB.setOnClickListener {
            var board :String = ""
            board = intent.getStringExtra("board").toString()
            val intent = Intent(applicationContext, BoardWrite::class.java)
            intent.putExtra("id", id)
            intent.putExtra("board", board)
            intent.putExtra("update", 0)
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            Log.d("Floating Button Clicked", userType)
            startActivity(intent)
        }

        boardRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(scrollFlag==false){

                    if (!boardRecyclerView.canScrollVertically(-1)) { //맨 위
                        //새로고침...
                        refresh_layout.setOnRefreshListener {
                            Log.d("omg", "hello refresh")

                            try {
                                //TODO 액티비티 화면 재갱신 시키는 코드
                                val intent = intent
                                finish() //현재 액티비티 종료 실시
                                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                                startActivity(intent) //현재 액티비티 재실행 실시
                                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            refresh_layout.isRefreshing = false //새로고침 없애기
                        }


//                       fetchData()
                    } else if (!boardRecyclerView.canScrollVertically(1)) { //맨 아래
                        //로딩
                        if(all_items.size > 20){
                            scrollFlag = true

                            if((all_items.size - item_count*scroll_count) > 20){
                                for (i in (item_count * scroll_count) + (item_count-1)  downTo   (item_count * scroll_count) + 0) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.
                                }

                                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)

                                scrollFlag = false
                            }
                            else{
                                for (i  in all_items.size-1  downTo   (item_count* scroll_count)) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.

                                }
                                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }

                            scroll_count ++
                        }
                    }
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        var board :String = ""
        board = intent.getStringExtra("board").toString()
        var userType :String = ""
        userType = intent.getStringExtra("userType").toString()
        var userDept :String = ""
        userDept = intent.getStringExtra("userDept").toString()
        val urlBoard = "http://seonho.dothome.co.kr/Board.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
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
                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                var new_items = ArrayList<BoardItem>()
                new_items.addAll(items)
                adapter = BoardListAdapter(new_items)
                boardRecyclerView.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);


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
                                "board" to board,
                                "post_num" to data.num.toString()
                            )
                        )
                        val queue = Volley.newRequestQueue(applicationContext)
                        queue.add(request)
                    }

                })



            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "board" to board,
                "userType" to userType,
                "userDept" to userDept
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

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
        Log.d("current TIme", cur)

        val newPostTime = Millis(postTime)
        var diffTime = (curTime - newPostTime)/1000
        Log.d("Time" , "cur = ${curTime.toString()}, post = ${postTime}, new post = ${newPostTime.toString()}, diff = ${diffTime.toString()} ")
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