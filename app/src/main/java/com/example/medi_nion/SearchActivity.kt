package com.example.medi_nion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BoardListAdapter
    private var items = ArrayList<BoardItem>()
    private var filteredItems = ArrayList<BoardItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchview)

        // Retrieve references to the SearchView and RecyclerView
        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.boardRecyclerView)

        // Set up the RecyclerView and its adapter
        adapter = BoardListAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the SearchView query listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(query: String): Boolean {
                // Perform the search when the search button is clicked
                Log.d("ditto", "wowowowo")
                performSearch(query)
                return true
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextChange(newText: String): Boolean {
                // Optional: Perform incremental search as the user types
                performSearch(newText)
                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun performSearch(query: String) {
        Log.d("ditto1", "$filteredItems")
        fetchData()

        filteredItems.clear()

        if (query.isEmpty()) {
            // If the query is empty, show all items without filtering
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
            filteredItems.addAll(items)
        } else {
            // Filter the items based on the query

            for (item in items) {
                if (item.title.contains(query, ignoreCase = true) || //title, content에서 동일한 단어가 있으면
                    item.contents.contains(query, ignoreCase = true)
                ) {
                    filteredItems.add(item)
                }
            }
        }
        adapter.setItems(filteredItems)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        var nickname = intent.getStringExtra("nickname")
        var board = intent.getStringExtra("board").toString()
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()
        var userMedal = intent.getIntExtra("userMedal", 0)
        val urlBoard = "http://seonho.dothome.co.kr/Board.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                filteredItems.clear()

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

//                    if(i >= jsonArray.length() - item_count*scroll_count){
//                        items.add(boardItem)
//                        itemIndex.add(num) //앞에다가 추가.
//                    }

                    filteredItems.add(boardItem)
                }
                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                var new_items = java.util.ArrayList<BoardItem>()
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
                var detailCommentCnt : String = ""
                //var detailCommentComment : String = ""

                //게시판 상세
                adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, data: BoardItem, pos: Int) {

                        val request = Login_Request(
                            Request.Method.POST,
                            urlDetail,
                            { responseDetail ->
                                if(responseDetail!="Detail Info Error"){
                                    val jsonArray = JSONArray(responseDetail)
                                    items.clear()
                                    for (i in jsonArray.length()-1  downTo  0) {
                                        val item = jsonArray.getJSONObject(i)

                                        detailId = item.getString("id")
                                        detailTitle = item.getString("title")
                                        detailContent = item.getString("content")
                                        detailTime = item.getString("time")
                                        detailImg = item.getString("image")
                                        detailCommentCnt = item.getString("comment")

                                        val intent = Intent(applicationContext, BoardDetail::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                        intent.putExtra("board", board)
                                        intent.putExtra("num", data.num)
                                        intent.putExtra("id", id)
                                        intent.putExtra("nickname", nickname)
                                        intent.putExtra("writerId", detailId)
                                        intent.putExtra("title", detailTitle)
                                        intent.putExtra("content", detailContent)
                                        intent.putExtra("time", detailTime)
                                        intent.putExtra("image", detailImg)
                                        intent.putExtra("userType", userType)
                                        intent.putExtra("userDept", userDept)
                                        intent.putExtra("userMedal", userMedal)
                                        intent.putExtra("commentCnt", detailCommentCnt)
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
