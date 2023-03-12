package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.home_qna.*

class HomeFragment : Fragment(R.layout.home) { //피드 보여주는 홈화면 프레그먼트(게시판 구분은 추후에)

    class PagerRecyclerAdapter(private val qnaTitle: ArrayList<String>, private val qnaDetail: ArrayList<String>) : RecyclerView.Adapter<PagerRecyclerAdapter.PagerViewHolder>() {

        inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val qna_title: TextView = itemView.findViewById(R.id.home_qna_title)
            private val qna_content: TextView = itemView.findViewById(R.id.home_qna_detail)

            val all_board : TextView = itemView.findViewById(R.id.home_boardList1)
            val job_board : TextView = itemView.findViewById(R.id.home_boardList2)
            val medical_board : TextView = itemView.findViewById(R.id.home_boardList3)
            val ourmedi_board : TextView = itemView.findViewById(R.id.home_boardList4)
            val qa_board : TextView = itemView.findViewById(R.id.home_boardList5)

            fun bind(position: Int) {
                qna_title.text = qnaTitle[position]
                qna_content.text = qnaDetail[position]
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.home_qna_detail,
                parent,
                false
            )
            return PagerViewHolder(view)
        }

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 나중에 DB에서 받아올 것
        var qnaTitle = arrayListOf<String>(
            "page1",
            "page2",
            "page3",
            "page4",
            "page5",
        )

        var qnaContent = arrayListOf<String>(
            "page1111111111111111111111111",
            "page22222222222222222222222222222",
            "page333333333333333333333333333333",
            "page44444444444444444444444444",
            "page55555555555555555555555",
        )

        // RecyclerView.Adapter<ViewHolder>()
        viewPager.adapter = PagerRecyclerAdapter(qnaTitle, qnaContent)
        // ViewPager의 Paging 방향은 Horizontal
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // Paging 완료되면 호출
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("ViewPagerFragment", "Page ${position+1}")
            }
        })
    }
}