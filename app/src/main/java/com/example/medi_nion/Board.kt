package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONArray
import org.json.JSONObject


var items =ArrayList<BoardItem>()
//val viewModel = BoardViewModel()
//lateinit var adapter : BoardListAdapter
val adapter = BoardListAdapter(items)
lateinit var mJsonString: String
var errorString: String? = null
var itemIndex = ArrayList<Int>()

class Board : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

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
    }

    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        var board :String = ""
        board = intent.getStringExtra("board").toString()
        val urlBoard = "http://seonho.dothome.co.kr/Board.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"
        val jsonArray : JSONArray

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")
                    val image = item.getString("image")
                    val boardItem = BoardItem(num, title, content, time, image)
                    items.add(boardItem)
                    itemIndex.add(num) //앞에다가 추가.
//                    val adapter = BoardListAdapter(items)
                    boardRecyclerView.adapter = adapter


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

                }

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