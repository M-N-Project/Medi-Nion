package com.example.medi_nion

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.board_home.*

class Board : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_home)

        val boards : List<BoardItem> = listOf(
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!내용내용!!",
                1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                 1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                 1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                 1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                 1, 2, 3),
            BoardItem("제목입니다ㅏ아아!!","2023년 2월 14일 오후 3월 23일", "내용내용!!",
                 1, 2, 3)
        )

        val adapter = BoardListAdapter(this, boards)
        boardRecyclerView.adapter = adapter

        //게시판 상세
        adapter.setOnItemClickListener(object : BoardListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: BoardItem, pos : Int) {
                Intent(this@Board, BoardDetail::class.java).apply {
                    putExtra("data", data.toString())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })

        //글쓰기
        val writingFAB = findViewById<FloatingActionButton>(R.id.wrtingFAB)
        wrtingFAB.setOnClickListener{
            val intent = Intent(applicationContext, CreateBoard::class.java)
            startActivity(intent)
            //SignUp::class.java 대신 글쓰기 kt 파일로 이동.
            val intent = Intent(applicationContext, BoardWrite::class.java)
            startActivity(intent)
        }

    }

}
