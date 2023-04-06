package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray

const val PREFERENCES_NAME = "rebuild_preference"
private const val DEFAULT_VALUE_STRING = ""
private const val DEFAULT_VALUE_BOOLEAN = false
private const val DEFAULT_VALUE_INT = -1
private const val DEFAULT_VALUE_LONG = -1L
private const val DEFAULT_VALUE_FLOAT = -1f

class Login : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //최초 실행 여부 판단하는 구문
        
//        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
//        val first = pref.getBoolean("isFirst", false)
//        if (first == false) {
//            val firstIntent = intent.getStringExtra("first")
//            Log.d("0-1023", firstIntent.toString())
//            if(firstIntent == "false") {
//                Log.d("Is first Time?", "first")
//                val editor = pref.edit()
//                editor.putBoolean("isFirst", true)
//                editor.commit()
//
//                val url = "http://seonho.dothome.co.kr/Login.php"
//
//                val id = findViewById<EditText>(R.id.id)
//                val password = findViewById<EditText>(R.id.password)
//                val loginBtn = findViewById<Button>(R.id.loginBtn)
//                val signupBtn = findViewById<Button>(R.id.signupBtn)
//                val autologin = findViewById<CheckBox>(R.id.autologin)
//
//                var checkID: String = ""
//                var checkPW: String = ""
//                var mContext = this
//
//                signupBtn.setOnClickListener {
//                    //회원가입 화면으로 이동
//                    val intent = Intent(applicationContext, Retrofit_SignUp::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
//                    startActivity(intent)
//                }
//
//                var boo = getBoolean(mContext, "check")
//                if(boo) {
//                    id.setText(getString(mContext, "id"))
//                    password.setText(getString(mContext, "pw"))
//                    autologin.isChecked = true
//                }
//
//                loginBtn.setOnClickListener {
//                    if (id.length() == 0 || password.length() == 0) {
//                        Toast.makeText(
//                            baseContext,
//                            String.format("아이디와 비밀번호 모두 입력해주세요."),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        setString(mContext, "id", id.text.toString())
//                        setString(mContext, "pw", password.text.toString())
//                        checkID = getString(mContext, "id")
//                        checkPW = getString(mContext, "pw")
//                        Log.d("99999", "$checkID, $checkPW")
////                    intent.putExtra("checkID", checkID)
////                    intent.putExtra("checkPW", checkPW)
//                        loginRequest(url)
//                    }
//                }
//
//                if(autologin.isChecked) {
//                    setString(mContext, "id", id.text.toString())
//                    setString(mContext, "pw", password.text.toString())
//                    setBoolean(mContext, "check", autologin.isChecked)
//                    Log.d("mmmmmmm", "123")
//                }
//                else {
//                    //setBoolean(mContext, "check", autologin.isChecked)
//                    clear(mContext)
//                    Log.d("fffffff", "456")
//                }
//            }
//            else{
//                var newIntent: Intent = Intent(this, FirstTimeActivity::class.java);
//                startActivity(newIntent);
//            }
//
//        } else {
//            Log.d("Is first Time?", "first")
//            val editor = pref.edit()
//            editor.putBoolean("isFirst", true)
//            editor.commit()
//
//            val url = "http://seonho.dothome.co.kr/Login.php"
//
//            val id = findViewById<EditText>(R.id.id)
//            val password = findViewById<EditText>(R.id.password)
//            val loginBtn = findViewById<Button>(R.id.loginBtn)
//            val signupBtn = findViewById<Button>(R.id.signupBtn)
//            val autologin = findViewById<CheckBox>(R.id.autologin)
//
//            var checkID: String = ""
//            var checkPW: String = ""
//            var mContext = this
//
//            signupBtn.setOnClickListener {
//                //회원가입 화면으로 이동
//                val intent = Intent(applicationContext, Retrofit_SignUp::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
//                startActivity(intent)
//            }
//
//            var boo = getBoolean(mContext, "check")
//            if(boo) {
//                id.setText(getString(mContext, "id"))
//                password.setText(getString(mContext, "pw"))
//                autologin.isChecked = true
//            }
//
//            loginBtn.setOnClickListener {
//                if (id.length() == 0 || password.length() == 0) {
//                    Toast.makeText(
//                        baseContext,
//                        String.format("아이디와 비밀번호 모두 입력해주세요."),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    setString(mContext, "id", id.text.toString())
//                    setString(mContext, "pw", password.text.toString())
//                    checkID = getString(mContext, "id")
//                    checkPW = getString(mContext, "pw")
//                    Log.d("99999", "$checkID, $checkPW")
////                    intent.putExtra("checkID", checkID)
////                    intent.putExtra("checkPW", checkPW)
//                    loginRequest(url)
//                }
//            }
//
//            if(autologin.isChecked) {
//                setString(mContext, "id", id.text.toString())
//                setString(mContext, "pw", password.text.toString())
//                setBoolean(mContext, "check", autologin.isChecked)
//                Log.d("mmmmmmm", "123")
//            }
//            else {
//                //setBoolean(mContext, "check", autologin.isChecked)
//                clear(mContext)
//                Log.d("fffffff", "456")
//            }
//        }


        val url = "http://seonho.dothome.co.kr/Login.php"

        val id = findViewById<EditText>(R.id.id)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupBtn = findViewById<Button>(R.id.signupBtn)
        val autologin = findViewById<CheckBox>(R.id.autologin)

        var checkID: String = ""
        var checkPW: String = ""
        var mContext = this

        signupBtn.setOnClickListener {
            //회원가입 화면으로 이동
            val intent = Intent(applicationContext, Retrofit_SignUp::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
            startActivity(intent)
        }

        var boo = getBoolean(mContext, "check")
        if(boo) {
            id.setText(getString(mContext, "id"))
            password.setText(getString(mContext, "pw"))
            autologin.isChecked = true
        }

        loginBtn.setOnClickListener {
            if (id.length() == 0 || password.length() == 0) {
                Toast.makeText(
                    baseContext,
                    String.format("아이디와 비밀번호 모두 입력해주세요."),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setString(mContext, "id", id.text.toString())
                setString(mContext, "pw", password.text.toString())
                checkID = getString(mContext, "id")
                checkPW = getString(mContext, "pw")
                Log.d("99999", "$checkID, $checkPW")
//                    intent.putExtra("checkID", checkID)
//                    intent.putExtra("checkPW", checkPW)
                loginRequest(url)
            }
        }

        if(autologin.isChecked) {
            setString(mContext, "id", id.text.toString())
            setString(mContext, "pw", password.text.toString())
            setBoolean(mContext, "check", autologin.isChecked)
            Log.d("mmmmmmm", "123")
        }
        else {
            //setBoolean(mContext, "check", autologin.isChecked)
            clear(mContext)
            Log.d("fffffff", "456")
        }
    }

    @SuppressLint("HardwareIds", "SuspiciousIndentation")
    private fun loginRequest(url: String) {
        var id = findViewById<EditText>(R.id.id).text.toString()
        var passwd = findViewById<EditText>(R.id.password).text.toString()
        var getDeviceID: String = ""   //디바이스 장치의 고유 아이디
        var intent = Intent(this, MainActivity::class.java)

        var userSearchUrl = "http://seonho.dothome.co.kr/userSearch.php"

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                var userType = ""
                var userDept = ""
                var userMedal = 0
                var nickname = ""
                var intent = Intent(this, MainActivity::class.java)
                if (!response.equals("Login Failed")) {
                    val userSearch = Login_Request(
                        Request.Method.POST,
                        userSearchUrl,
                        { responseUser ->
                            Log.d("responseUser", responseUser)

                            if (!responseUser.equals("userSearch fail")) {
                                val jsonArray = JSONArray(responseUser)


                                for (i in jsonArray.length() - 1 downTo 0) {
                                    val item = jsonArray.getJSONObject(i)

                                    userType = item.getString("userType")
                                    userDept = item.getString("userDept")
                                    userMedal = item.getInt("userMedal")
                                    nickname = item.getString("nickname")

                                    intent.putExtra("userType", userType)
                                    intent.putExtra("userDept", userDept)
                                    intent.putExtra("userMedal", userMedal)
                                    intent.putExtra("nickname", nickname)

                                    Log.d("userSearch", "type : ${item.getString("userType")}, dept : ${item.getString("userDept")}")
                                    Log.d("userMedal", userMedal.toString())
                                    Log.d("nickname", nickname)

                                    Toast.makeText(
                                        baseContext,
                                        String.format("로그인하였습니다."),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    intent.putExtra("id", id)
                                    intent.putExtra("passwd", passwd)
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
                        },
                        { Log.d("UserSearch Failed", "error......${error(applicationContext)}") },
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
                "passwd" to passwd
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

    override fun onStop() {
        var autologin = findViewById<CheckBox>(R.id.autologin)
        super.onStop()
        if(autologin.isChecked) {

        }
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    /* String 값 저장 */
    fun setString(context: Context, key: String?, value: String?) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }


    /* boolean 값 저장 */
    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }


    /* int 값 저장 */
    fun setInt(context: Context, key: String?, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }


    /* long 값 저장 */
    fun setLong(context: Context, key: String?, value: Long) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }


    /* float 값 저장 */
    fun setFloat(context: Context, key: String?, value: Float) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.commit()
    }


    /* String 값 로드 */
    fun getString(context: Context, key: String?): String {
        val prefs = getPreferences(context)
        val value: String = prefs.getString(key, DEFAULT_VALUE_STRING)!!
        return value
    }


    /* boolean 값 로드 */
    fun getBoolean(context: Context, key: String?): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }


    /* int 값 로드 */
    fun getInt(context: Context, key: String?): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }


    /* long 값 로드 */
    fun getLong(context: Context, key: String?): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }


    /* float 값 로드 */
    fun getFloat(context: Context, key: String?): Float {
        val prefs = getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }


    /* 키 값 삭제 */
    fun removeKey(context: Context, key: String?) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.commit()
    }


    /* 모든 저장 데이터 삭제 */
    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.clear()
        edit.commit()
    }
}