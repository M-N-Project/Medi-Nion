package com.example.medi_nion

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley

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

            val id = findViewById<EditText>(R.id.id)
            val password = findViewById<EditText>(R.id.password)
            val loginBtn = findViewById<Button>(R.id.loginBtn)
            val signupBtn = findViewById<Button>(R.id.signupBtn)

            signupBtn.setOnClickListener {
                //회원가입 화면으로 이동
                val intent = Intent(applicationContext, SignUp::class.java)
                startActivity(intent)
            }

            loginBtn.setOnClickListener {
                if (id.length() == 0 || password.length() == 0) {
                    Toast.makeText(
                        baseContext,
                        String.format("아이디와 비밀번호 모두 입력해주세요."),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    loginRequest(url)
                }

            }
            var newIntent: Intent = Intent(this, MainActivity::class.java);
            startActivity(newIntent);
        } else {
            Log.d("slkdjslf", "first not")
            var newIntent: Intent = Intent(this, FirstTimeActivity::class.java);
            startActivity(newIntent);
        }
    }


    private fun loginRequest(url: String) {
        var id = findViewById<EditText>(R.id.id).text.toString()
        var password = findViewById<EditText>(R.id.password).text.toString()

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
            if (response.equals("Login Success")) {
                id = response.toString()
                password = response.toString()

                Toast.makeText(
                    baseContext,
                    String.format("로그인하였습니다."),
                    Toast.LENGTH_SHORT
                ).show()

                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                Log.d(
                    "login success",
                    "$id, $password"
                )
            } else {
                Toast.makeText(
                    baseContext,
                    String.format("로그인할 수 없습니다. 아이디 혹은 비밀번호를 다시 확인해주세요."),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
            "id" to id,
            "passwd" to password
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}