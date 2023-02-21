package com.example.medi_nion

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_home.*

class BusinessMainFragment : Fragment() { //bussiness 체널 보여주는 프레그먼트


    // RecyclerView.adapter에 지정할 Adapter
    private lateinit var listAdapter: BusinessRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.business_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val businessBoard = ArrayList<BusinessBoardItem>() //일단 더미데이터, db 연동해야함
        businessBoard.add(BusinessBoardItem("개강전 이벤트!!", "2023년 2월 15일 오후 1시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "이것은 내용입니다. 약사세요~ 줄바꿈도 해야한답니다", 1, 2))

        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))

        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))
        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))
        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))
        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))
        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getResources().getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))


        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        val adapter = BusinessRecyclerAdapter(businessBoard)
        BusinessBoardRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        BusinessBoardRecyclerView.adapter = adapter
    }
}