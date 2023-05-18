package com.example.medi_nion

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class BusinessManageEdit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_edit)
        val chanName = intent.getStringExtra("chanName")
        Log.d("비즈니스 수정", "$chanName")
    }
}