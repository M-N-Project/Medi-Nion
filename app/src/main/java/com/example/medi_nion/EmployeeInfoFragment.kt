package com.example.medi_nion

import kotlinx.coroutines.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class EmployeeInfoFragment : Fragment() {

    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.employee_info, container, false)
        textView = view.findViewById(R.id.textView3)
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accessKey = "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway" // 발급받은 accessKey"
        val text = URLEncoder.encode("", "UTF-8")
        val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&job_mid_cd=6"

        GlobalScope.launch {
            try {
                val response = getResponseFromApi(apiURL)
                val responseBody = response?.body?.string()
                withContext(Dispatchers.Main) {
                    responseBody?.let {
                        val jsonObj: JSONObject = JSONObject(responseBody)
                        val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                        val jobList:JSONArray = jobsList.get("job") as JSONArray

                        /*job {
                            url, company-detail-name,
                            position-title, location, experience-level, required-education-level,
                            expiration-timestamp
                        }
                        url, company, title, loca, experience, school, deadline*/

                        for (i in 0 until jobList.length()) {
                            val item = jobList.getJSONObject(i)

                            val url = item.getString("url")
                            val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                            val position = item.getJSONObject("position")
                            val title = position.getString("title")
                            val loca = position.getString("location")
                            val experience = position.getJSONObject("experience-level").getString("name")
                            val school = position.getJSONObject("required-education-level").getString("name")
                            val deadline = item.getString("expiration-timestamp")

                            Log.d("임플로이인포제이슨", "$url \n $company \n $title \n $loca \n $experience \n $school \n $deadline")
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    textView.text = "Error occurred: ${e.message}"
                }
            }
        }
    }

    private suspend fun getResponseFromApi(apiUrl: String): Response? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
    }
}