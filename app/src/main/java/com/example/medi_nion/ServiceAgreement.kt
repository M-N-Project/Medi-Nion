package com.example.medi_nion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ServiceAgreement : AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_agreement)
    }
}