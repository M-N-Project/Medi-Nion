package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class FindPasswd: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.findpasswd)

        val findpasswdBtn = findViewById<Button>(R.id.findpasswdBtn)
        val loginBtn = findViewById<Button>(R.id.login)

        findpasswdBtn.setOnClickListener {
            FindPasswd()
        }

        loginBtn.setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
        }
    }


    @SuppressLint("NotConstructor")
    fun FindPasswd() {
        val id = findViewById<EditText>(R.id.userid_editText).text.toString()
        val findPasswdLinear = findViewById<LinearLayout>(R.id.findpasswd_linear)
        val userid = findViewById<TextView>(R.id.userid_textView)
        val userpasswd = findViewById<TextView>(R.id.userpasswd_textView)
        val notfindPasswd = findViewById<TextView>(R.id.notfindpasswd)
        val url = "http://seonho.dothome.co.kr/FindPasswd.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                if (!response.equals("no User")) {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {

                        val item = jsonArray.getJSONObject(i)

                        val passwd = item.getString("passwd")

                        userid.text = id
                        userpasswd.text = passwd
                        findPasswdLinear.visibility = View.VISIBLE
                        if (findPasswdLinear.visibility == View.VISIBLE) {
                            notfindPasswd.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    notfindPasswd.visibility = View.VISIBLE
                    if(notfindPasswd.visibility == View.VISIBLE) {
                        findPasswdLinear.visibility = View.INVISIBLE
                    }
                }
            }, { Log.d("User find Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}