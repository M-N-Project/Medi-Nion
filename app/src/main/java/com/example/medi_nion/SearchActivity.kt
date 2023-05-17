package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    private var items = java.util.ArrayList<BoardItem>()
    private var all_items = java.util.ArrayList<BoardItem>()
    private val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    private var scroll_count = 1
    private var adapter = SearchListAdapter(ArrayList())
    private var itemIndex = java.util.ArrayList<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchview)

        var searchView: SearchView = findViewById(R.id.search_view)

        fetchData()

        // SearchView listener to filter posts
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Do nothing when submit button is clicked

                // 키보드를 자동으로 올립니다.
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                var filter_items = java.util.ArrayList<BoardItem>()
                filter_items.addAll(items)
                adapter = SearchListAdapter(filter_items)
                boardRecyclerView.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);

                adapter.filter(query)

                adapter.setOnItemClickListener(object : SearchListAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, data: BoardItem, pos: Int) {

                        val urlDetail = "http://seonho.dothome.co.kr/Search_board_detail.php"
                        var id = intent.getStringExtra("id")
                        var nickname = intent.getStringExtra("nickname")
                        var board = intent.getStringExtra("board").toString()
                        var userType = intent.getStringExtra("userType").toString()
                        var userDept = intent.getStringExtra("userDept").toString()
                        var userMedal = intent.getIntExtra("userMedal", 0)

                        val request = Login_Request(
                            Request.Method.POST,
                            urlDetail,
                            { responseDetail ->
                                if(responseDetail!="Detail Info Error"){
                                    val jsonArray = JSONArray(responseDetail)
                                    items.clear()
                                    for (i in jsonArray.length()-1  downTo  0) {
                                        val item = jsonArray.getJSONObject(i)

                                        val detailId = item.getString("id")
                                        val detailTitle = item.getString("title")
                                        val detailContent = item.getString("content")
                                        val detailTime = item.getString("time")
                                        val detailImg = item.getString("image")
                                        val detailCommentCnt = item.getString("comment")

                                        val intent = Intent(applicationContext, BoardDetail::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                        intent.putExtra("board", "자유 게시판")
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

                return true
            }


            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextChange(newText: String?): Boolean { //검색하는거 인식
                // Filter posts by title or content

                if (newText == "") {
                    adapter.filter(newText)
                }

                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchData() {
        // url to post our data
        var board = intent.getStringExtra("board").toString()
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()
        val urlBoard = "http://seonho.dothome.co.kr/Search_board.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                all_items.clear()

                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)
                    val num = item.getString("num")
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
                        itemIndex.add(num.toInt()) //앞에다가 추가.
                    }
                    all_items.add(boardItem)
                }

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
