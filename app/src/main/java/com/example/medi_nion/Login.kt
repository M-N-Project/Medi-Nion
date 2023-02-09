package com.example.medi_nion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val url = "http://seonho.dothome.co.kr/Login.php"

    }
}