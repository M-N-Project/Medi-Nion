package com.example.medi_nion

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.databinding.DataBindingUtil.setContentView

class MyCustomDialog(context: Context, val imgUrl : String) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullimage)

        // 배경 투명하게
//        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val fullImg = findViewById<ImageView>(R.id.image_full)
        val task = ImageLoadTask(imgUrl, fullImg)
        task.execute()

    }
}