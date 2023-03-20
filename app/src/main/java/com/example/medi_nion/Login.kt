package com.example.medi_nion

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

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
//                    var newIntent: Intent = Intent(this, MainActivity::class.java);
//                    startActivity(newIntent);
                }
            }

        } else {
            var newIntent: Intent = Intent(this, FirstTimeActivity::class.java);
            startActivity(newIntent);
        }
    }


    private fun loginRequest(url: String) {
        var id = findViewById<EditText>(R.id.id).text.toString()
        var password = findViewById<EditText>(R.id.password).text.toString()

        var userSearchUrl = "http://seonho.dothome.co.kr/userSearch.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                var userType = ""
                var userDept = ""
                var intent = Intent(this, MainActivity::class.java)
                if (!response.equals("Login Failed")) {
                    val userSearch = Login_Request(
                        Request.Method.POST,
                        userSearchUrl,
                        { responseUser ->
                            Log.d("responseUser", responseUser)

                            if (!responseUser.equals("userSearch fail")){
                                val jsonArray = JSONArray(responseUser)

                                for (i in jsonArray.length()-1  downTo  0) {
                                    val item = jsonArray.getJSONObject(i)

                                    userType = item.getString("userType")
                                    userDept = item.getString("userDept")

                                    intent.putExtra("userType", userType)
                                    intent.putExtra("userDept", userDept)
                                    //Log.d("userSearch", "type : ${item.getString("userType")}, dept : ${item.getString("userDept")}")

                                    Toast.makeText(
                                        baseContext,
                                        String.format("로그인하였습니다."),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    intent.putExtra("id", id)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
                                    startActivity(intent)
                                }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    String.format("userSearch fail"),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("userSearch", "Failed")
                            }
                        }, { Log.d("UserSearch Failed", "error......${error(applicationContext)}") },
                        hashMapOf(
                            "id" to id
                        )
                    )
                    val queue = Volley.newRequestQueue(this)
                    queue.add(userSearch)

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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}