package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray
import org.json.JSONObject


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
    override fun onStart() {
        super.onStart() //프레그먼트로 생길 문제들은 추후에 생각하기,,

        fetchData()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

        items.clear()
        all_items.clear()

        boardRecyclerView.setLayoutManager(boardRecyclerView.layoutManager);

        var id = intent.getStringExtra("id")
        fetchData()

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        wrtingFAB.setOnClickListener {
            var board :String = ""
            board = intent.getStringExtra("board").toString()
            val intent = Intent(applicationContext, BoardWrite::class.java)
            intent.putExtra("id", id)
            intent.putExtra("board", board)
            startActivity(intent)
        }


        boardRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                fetchData()
                if(scrollFlag==false){
                    if (!boardRecyclerView.canScrollVertically(-1)) { //맨 위
                       fetchData()
                    } else if (!boardRecyclerView.canScrollVertically(1)) { //맨 아래
                        if(all_items.size > 20){
                            val scrollLocation = IntArray(2)
                            boardRecyclerView.getLocationOnScreen(scrollLocation)
                            var scroll_pos = scrollLocation[1]

                            scroll_count ++
                            scrollFlag = true

                            if(all_items.size - item_count*(scroll_count-1) > 20){
                                for (i in all_items.size - item_count*(scroll_count - 1) -1  downTo   all_items.size - item_count*scroll_count) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.
                                }

                                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);

                                scrollFlag = false
                            }
                            else{
                                for (i in all_items.size - item_count*(scroll_count - 1) -1  downTo  0) {
                                    items.add(all_items[i])
                                    itemIndex.add(all_items[i].num) //앞에다가 추가.

                                }
                                var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<BoardItem>()
                                new_items.addAll(items)
                                adapter = BoardListAdapter(new_items)
                                boardRecyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);
                            }


                        }
                    }
                }
            }
        })
    }


    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        var board :String = ""
        board = intent.getStringExtra("board").toString()
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
                        val time = item.getString("time")
                        val image = item.getString("image")
                        var heart = item.getInt("heart")
                        var comment = item.getInt("comment")
                        var bookmark = item.getInt("bookmark")

                        val boardItem = BoardItem(num, title, content, time, image, heart, comment, bookmark)

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
                                    items.clear()
//                                    for (i in jsonArray.length()-1  downTo  0) {
                                    val jsonObject = JSONObject(response)

                                    detailId = jsonObject.getString("id")
                                    detailTitle = jsonObject.getString("title")
                                    detailContent = jsonObject.getString("content")
                                    detailTime = jsonObject.getString("time")
                                    detailImg = jsonObject.getString("image")

                                    val intent = Intent(applicationContext, BoardDetail::class.java)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                    intent.putExtra("board", board)
                                    intent.putExtra("num", data.num)
                                    intent.putExtra("id", id)
                                    intent.putExtra("title", detailTitle)
                                    intent.putExtra("content", detailContent)
                                    intent.putExtra("time", detailTime)
                                    intent.putExtra("image", detailImg)
                                    startActivity(intent)


                                }, { Log.d("login failed", "error......${error(applicationContext)}") },
                                hashMapOf(
                                    "post_num" to data.num.toString()
                                )
                            )
                            val queue = Volley.newRequestQueue(applicationContext)
                            queue.add(request)
                        }

                    })



            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "board" to board
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    data class JsonObj(val result: String)
}