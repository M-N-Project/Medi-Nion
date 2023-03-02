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
//lateinit var adapter : BoardListAdapter
val adapter = BoardListAdapter(items)
lateinit var mJsonString: String
var errorString: String? = null

class Board : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

        var id = intent.getStringExtra("id")

        val url = "http://seonho.dothome.co.kr/Board.php"

        fetchData()

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        wrtingFAB.setOnClickListener {
            val intent = Intent(applicationContext, BoardWrite::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }

    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        val url = "http://seonho.dothome.co.kr/Board.php"
        val jsonArray : JSONArray

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("//", response)
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
//                    val adapter = BoardListAdapter(items)
                    boardRecyclerView.adapter = adapter

                    Log.d("><><><><><><", "$num, $title, $content, $time")

                        /*val intent = Intent(applicationContext, BoardDetail::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("num", num)
                        intent.putExtra("title", title)
                        intent.putExtra("content", content)
                        intent.putExtra("time", time)
                        startActivity(intent)
                        Log.d("123", "$num")*/



                    //게시판 상세
                    adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener {
                        override fun onItemClick(v: View, data: BoardItem, pos: Int) {
                            val intent = Intent(applicationContext, BoardDetail::class.java)
                            intent.putExtra("id", id)
                            intent.putExtra("num", num)
                            intent.putExtra("title", title)
                            intent.putExtra("content", content)
                            intent.putExtra("time", time)
                            startActivity(intent)
                            Log.d("123", "$num")
//                            Intent(this@Board, BoardDetail::class.java).apply {
//                                putExtra("id", id)
//                                putExtra("num", num)
//                                putExtra("title", title)
//                                putExtra("content", content)
//                                putExtra("time", time)
//                                putExtra("data", data.toString())
//                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//                                Log.d(
//                                    "wowwowwowwoww",
//                                    "$num"
//                                )
//                            }.run { startActivity(this) }
                        }

                    })

                }

            }, { Log.d("login failed", "error......${error(applicationContext)}") }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    data class JsonObj(val result: String)
}