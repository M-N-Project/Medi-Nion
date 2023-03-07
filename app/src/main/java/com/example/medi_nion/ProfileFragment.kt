package com.example.medi_nion

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

        binding.item1.setOnClickListener{
            val item1ListLayout = binding.item1ListLayout
            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
            else item1ListLayout.visibility = View.GONE
        }

        binding.item2.setOnClickListener{
            val item2ListLayout = binding.item2ListLayout
            if(item2ListLayout.visibility == View.GONE) item2ListLayout.visibility = View.VISIBLE
            else item2ListLayout.visibility = View.GONE
        }
        binding.item3.setOnClickListener{
            val item3ListLayout = binding.item3ListLayout
            if(item3ListLayout.visibility == View.GONE) item3ListLayout.visibility = View.VISIBLE
            else item3ListLayout.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}

//    private var _binding: ProfileBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = ProfileBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        binding.item1.setOnClickListener{
//            val item1ListLayout = binding.item1ListLayout
//            Log.d("ssosososo", "ssosososo" )
//            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
//            else item1ListLayout.visibility = View.GONE
//        }
//
//        binding.item2.setOnClickListener{
//            val item2ListLayout = binding.item2ListLayout
//            if(item2ListLayout.visibility == View.GONE) item2ListLayout.visibility = View.VISIBLE
//            else item2ListLayout.visibility = View.GONE
//        }
//        binding.item3.setOnClickListener{
//            val item3ListLayout = binding.item3ListLayout
//            if(item3ListLayout.visibility == View.GONE) item3ListLayout.visibility = View.VISIBLE
//            else item3ListLayout.visibility = View.GONE
//        }
//
//        return view
//    }

//}