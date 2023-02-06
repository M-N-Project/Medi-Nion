package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener{
            var newIntent : Intent = Intent(this, MainActivity::class.java);
            startActivity(newIntent);
        }

    }
}