package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FirstTimeActivity: AppCompatActivity() { //mainactivity, 여기서는 프레그먼트 제어를 담당

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_start)

        var startBtn = findViewById<Button>(R.id.newby_btn)
        var signBtn = findViewById<Button>(R.id.goToSign_btn)

        startBtn.setOnClickListener{
            var newIntent : Intent = Intent(this, Agreement::class.java);
            startActivity(newIntent);
        }

        signBtn.setOnClickListener{
            var newIntent : Intent = Intent(this, Login::class.java);
            startActivity(newIntent);
        }

    }

}