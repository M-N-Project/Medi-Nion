package com.example.medi_nion

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*
import org.json.JSONException
import org.json.JSONObject


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
                Intent(this@Board, BoardDetail::class.java).apply {
                    putExtra("data", data.toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        wrtingFAB.setOnClickListener {
            val intent = Intent(applicationContext, CreateBoard::class.java)
            startActivity(intent)
            //SignUp::class.java 대신 글쓰기 kt 파일로 이동.)
        }
    }

    fun fetchData() {
        // url to post our data
        val url = "http://seonho.dothome.co.kr/Board.php"

        val queue = Volley.newRequestQueue(this@Board)

        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val title = jsonObject.getString("title")
                    val content = jsonObject.getString("content")
                    val boardItem = BoardItem(title, content)
                    items.add(boardItem)
                    val adapter = BoardListAdapter(items)
                    boardRecyclerView.adapter = adapter
                    Log.d("lllaaa", boardItem.title)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> // method to handle errors.
                Log.d("pppppppp", "Fail to get course$error")
                Toast.makeText(this@Board, "Fail to get course$error", Toast.LENGTH_SHORT)
                    .show()
            }) {
            override fun getBodyContentType(): String {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

        }
        queue.add(request)

    }

    data class JsonObj(val result: List<BoardItem>)
}