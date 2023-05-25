package com.example.medi_nion

import android.app.Service
import android.content.Intent
import android.os.IBinder

import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class BusinessProfileService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var id : String = ""
    private var isFirst : Boolean = false
    private var channel_name : String = ""
    private var channel_desc : String = ""
    private var profile_img : String = ""
    private var isProfileChanged : Boolean = true

    // 메인 스레드로부터 메세지를 전달받을 핸들러 선언
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // 보통 서비스에서는 파일 다운로드 같은 작업을 수행함
            // 이 예제에서는 sleep. 5초를 주도록 함

            val urlBusinessProfileInsert = "http://seonho.dothome.co.kr/BusinessProfileInsert.php"
            val urlBusinessProfileUpdate = "http://seonho.dothome.co.kr/BusinessProfileUpdate2.php"

            try {
                if(isFirst){
                    val request: StringRequest =
                        object : StringRequest(Method.POST, urlBusinessProfileInsert, object : Response.Listener<String?> {
                            override fun onResponse(response: String?) {

                            }
                        }, object : Response.ErrorListener {
                            override fun onErrorResponse(error: VolleyError) {
                                error.printStackTrace()
                            }
                        }) {
                            @Throws(AuthFailureError::class)
                            override fun getParams(): Map<String, String>? {
                                val map: MutableMap<String, String> = HashMap()
                                // 1번 인자는 PHP 파일의 $_POST['']; 부분과 똑같이 해줘야 한다
                                map["id"] = id
                                map["Channel_Name"] = channel_name
                                map["Channel_Desc"] = channel_desc
                                map["Channel_Profile_Img"] = profile_img
                                return map
                            }
                        }

                    request.setRetryPolicy(
                        DefaultRetryPolicy(
                            40000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )
                    val queue = Volley.newRequestQueue(applicationContext)
                    queue.add(request)
                }
                else{
                    val request: StringRequest =
                        object : StringRequest(
                            Method.POST,
                            urlBusinessProfileUpdate,
                            object : Response.Listener<String?>
                            {
                            override fun onResponse(response: String?) {
                            }
                        }, object : Response.ErrorListener {
                            override fun onErrorResponse(error: VolleyError) {
                                Log.d("비즈니스 수정5", error.toString())
                            }
                        }) {
                            @Throws(AuthFailureError::class)
                            override fun getParams(): Map<String, String>? {
                                val map: MutableMap<String, String> = HashMap()
                                // 1번 인자는 PHP 파일의 $_POST['']; 부분과 똑같이 해줘야 한다
                                map["id"] = id
                                map["Channel_Name"] = channel_name
                                map["Channel_Desc"] = channel_desc
                                map["Channel_Profile_Img"] = profile_img
                                map["profileChanged"] = isProfileChanged.toString()
                                return map
                            }
                        }

                    request.setRetryPolicy(
                        DefaultRetryPolicy(
                            40000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )
                    val queue = Volley.newRequestQueue(applicationContext)
                    queue.add(request)
                }

            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
            // 서비스를 사용하였다면 서비스를 종료해 주어야 함.
            // 아래 메소드는 작업 startId가 가장 최신일때만 서비스를 stop하게 함
            // 이렇게 하면 동시에 여러 작업할 때, 모든작업이 끝나야 stop이 된다
            // 이게뭔지는 이후 설명에서 나옴
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // 서비스는 메인스레드에서 동작하므로 서비스의 작업은 따로 스레드를 선언해 주어야 한다.
        // CPU priority를 background로 선언하여 ui의 버벅임을 방지해야 한다.
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // 스레드의 루퍼를 얻어와 핸들러를 만들어준다.
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        id = intent.getStringExtra("id").toString()
        isFirst = intent.getBooleanExtra("isFirst", true)
        channel_name = intent.getStringExtra("channel_name").toString()
        channel_desc = intent.getStringExtra("channel_desc").toString()
        profile_img = intent.getStringExtra("profile_img").toString()
        if(profile_img.equals("")) isProfileChanged = false


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
    }
}