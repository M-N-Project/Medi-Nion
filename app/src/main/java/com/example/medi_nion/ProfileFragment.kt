package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medi_nion.databinding.ProfileBinding

class ProfileFragment : Fragment(R.layout.profile) {
    private var mBinding: ProfileBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = ProfileBinding.inflate(inflater, container, false)

        val id = arguments?.getString("id")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

        //나의 활동 
        binding.item1.setOnClickListener{
            val item1ListLayout = binding.item1ListLayout
            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
            else item1ListLayout.visibility = View.GONE
        }
//        binding.item1.setOnClickListener{
//            val item1ListLayout = binding.item1ListLayout
//            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
//            else item1ListLayout.visibility = View.GONE
//        }


        //내 게시물 -> board에 내 게시물 검색해서 만들기
        binding.item1List1Text.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "post")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 스크랩 -> board에 내 스크랩 검색해서 만들기
        binding.item1List2Text.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "scrap")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //어플 설정 -> 리스트업
        binding.item2.setOnClickListener {
            val item2ListLayout = binding.item2ListLayout
            if(item2ListLayout.visibility == View.GONE) item2ListLayout.visibility = View.VISIBLE
            else item2ListLayout.visibility = View.GONE
        }

        //계정 설정 -> 리스트업
        binding.item3.setOnClickListener {
            val item3ListLayout = binding.item3ListLayout
            if(item3ListLayout.visibility == View.GONE) item3ListLayout.visibility = View.VISIBLE
            else item3ListLayout.visibility = View.GONE
        }

        binding.item4.setOnClickListener{
            val item4ListLayout = binding.item4ListLayout
            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
            else item4ListLayout.visibility = View.GONE
        }

        binding.profileItem4.setOnClickListener{
            val item4ListLayout = binding.item4ListLayout
            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
            else item4ListLayout.visibility = View.GONE
        }

        //내가 구독한 비즈니스 채널
        binding.item4List1Text.setOnClickListener{

        }

        binding.item4List2Text.setOnClickListener{
            //businessChan이 0이면 (비즈니스 채널 없음) -> 신규 생성
            val intent = Intent(context, BusinessManageFirstActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            startActivity(intent)

            //businessChan이 1이면 (비즈니스 채널 있음) -> 관리
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

