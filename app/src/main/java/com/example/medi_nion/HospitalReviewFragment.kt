package com.example.medi_nion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medi_nion.databinding.HospitalReviewBinding

class HospitalReviewFragment : Fragment() {
    private var _binding: HospitalReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = HospitalReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

}