package com.example.medi_nion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.business_first1.*
import kotlinx.android.synthetic.main.business_writing_img_item.*
import kotlinx.android.synthetic.main.signup_done.*

class BusinessManageFirstFragment1 : Fragment() {

    private var image: Int? = null
    private var text1: String? = null
    private var text2: String? = null
    private var text3: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getInt("image", 0)
            text1 = it.getString("text1", "")
            text2 = it.getString("text2", "")
            text3 = it.getString("text3", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.business_first1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bf1_imageView1.setImageResource(image!!)
        bf1_textView1.text = text1
        bf1_textView2.text = text2
        bf1_textView3.text = text3
    }

    companion object {
        fun newInstance(image: Int, text1: String, test2: String, text3: String) =
            BusinessManageFirstFragment1().apply {
                    arguments = Bundle().apply {
                        putInt("image", image)
                        putString("text1", text1)
                        putString("text2", text2)
                        putString("text3", text3)
                    }
            }
    }
}