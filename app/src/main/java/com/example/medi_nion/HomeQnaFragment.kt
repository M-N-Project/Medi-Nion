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

class HomeQnaFragment : Fragment(R.layout.home){
    class PagerRecyclerAdapter(private val bgColors: ArrayList<String>) : RecyclerView.Adapter<PagerRecyclerAdapter.PagerViewHolder>() {

        inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val qna_title: TextView = itemView.findViewById(R.id.home_qna_title)

            fun bind(position: Int) {
                qna_title.text = "Page ${position+1}"
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

        override fun getItemCount(): Int = bgColors.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_qna, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Adapter를 생성하면서 넘길 색상이 담긴 ArrayList<Int> 생성
        var bgColors = arrayListOf<String>(
            "page1",
            "page2",
            "page3",
            "page4",
            "page5",
        )

        // RecyclerView.Adapter<ViewHolder>()
        viewPager.adapter = PagerRecyclerAdapter(bgColors)
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