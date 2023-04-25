package com.example.medi_nion

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

class EmployeeInfoFragment : Fragment() {

    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.employee_info, container, false)
        textView = view.findViewById(R.id.textView3)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val accessKey = "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway" // 발급받은 accessKey"
        val text = URLEncoder.encode("", "UTF-8")  //http://seonho.dothome.co.kr/EmployeeInfo.php
        //val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=$accessKey&keyword=$text"
        val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&bbs_gb=0&job_type=&job_mid_cd=6"

        val apiTask = ApiTask()
        apiTask.execute(apiURL)
    }

    private inner class ApiTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String?): String {
            val url = urls[0] ?: throw IllegalArgumentException("Url must not be null")

            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json") //응답방식 선택 json, xml
                .build()

            val client = OkHttpClient()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                return response.body?.string() ?: throw IOException("Response body must not be null")
            } catch (e: IOException) {
                throw e
            }
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            textView.text = result
        }
    }
}
