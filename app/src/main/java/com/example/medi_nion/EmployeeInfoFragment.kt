package com.example.medi_nion

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
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

        val accessKey = "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway2023" // 발급받은 accessKey"
        val text = URLEncoder.encode("http://seonho.dothome.co.kr/EmployeeInfo.php", "UTF-8")
        val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=$accessKey&keyword=$text"

        val apiTask = ApiTask()
        apiTask.execute(apiURL)
    }

    private inner class ApiTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String?): String {
            val url = URL(urls[0])
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("Accept", "application/json")

            val responseCode = con.responseCode

            val br: BufferedReader = if (responseCode == 200) { // 정상 호출
                BufferedReader(InputStreamReader(con.inputStream))
            } else {  // 에러 발생
                BufferedReader(InputStreamReader(con.errorStream))
            }

            var inputLine: String
            val response = StringBuffer()
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            br.close()

            return response.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            textView.text = result
        }
    }
}

