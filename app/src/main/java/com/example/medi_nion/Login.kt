package com.example.medi_nion

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //최초 실행 여부 판단하는 구문
        //최초 실행 여부 판단하는 구문
        val pref: SharedPreferences = getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first: Boolean = pref.getBoolean("isFirst", false)
        if (first == false) {
            val url = "http://seonho.dothome.co.kr/Login.php"

            val loginBtn = findViewById<Button>(R.id.loginBtn)

            loginBtn.setOnClickListener{
                var newIntent : Intent = Intent(this, MainActivity::class.java);
                startActivity(newIntent);
            }
        } else {
            Log.d("slkdjslf", "first not")
            var newIntent : Intent = Intent(this, FirstTimeActivity::class.java);
            startActivity(newIntent);

        }

    }
}