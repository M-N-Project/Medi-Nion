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


var items =ArrayList<BoardItem>()
//val viewModel = BoardViewModel()
lateinit var adapter : BoardListAdapter
lateinit var mJsonString: String
var errorString: String? = null

class Board : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

        val url = "http://seonho.dothome.co.kr/Board.php"


        fetchData()

        val adapter = BoardListAdapter(items)
        boardRecyclerView.adapter = adapter

//        val dataObserver: Observer<ArrayList<BoardItem>> = Observer {
//            items.value = it
//            val adapter = BoardListAdapter(items)
//            boardRecyclerView.adapter = adapter
//        }
//
//        viewModel.itemList.observe(this, dataObserver)


        //게시판 상세
        adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: BoardItem, pos: Int) {
                Log.d("---", "---")
                Intent(this@Board, BoardDetail::class.java).apply {
                    putExtra("data", data.toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        wrtingFAB.setOnClickListener {
            val intent = Intent(applicationContext, BoardWrite::class.java)
            startActivity(intent)
        }
    }

    fun fetchData() {
        // url to post our data
        val url = "http://seonho.dothome.co.kr/Board.php"
        val jsonArray : JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                val jsonArray = JSONArray(response)
                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")
                    val image = item.getString("image")
                    val boardItem = BoardItem(title, content, time, image)
                    items.add(boardItem)
                    val adapter = BoardListAdapter(items)
                    boardRecyclerView.adapter = adapter

                    //게시판 상세
                    adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
                        override fun onItemClick(v: View, data: BoardItem, pos: Int) {
                            Intent(this@Board, BoardDetail::class.java).apply {
                                putExtra("data", data.toString())
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.run { startActivity(this) }
                        }

                    })

                }

            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    data class JsonObj(val result: List<BoardItem>)
}