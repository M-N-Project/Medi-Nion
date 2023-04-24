package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medi_nion.databinding.EmployeeInfoBinding
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


// Define constants for Saramin API
private const val saraminApiBaseUrl = "https://oapi.saramin.co.kr"
private const val saraminApiEndpoint = "/job-search"
private const val saraminApiKey = "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway"

class EmployeeInfoFragment : Fragment() {
    private var _binding: EmployeeInfoBinding?   = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = EmployeeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Make API call and update UI components here
        makeSaraminApiCall()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeSaraminApiCall() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$saraminApiBaseUrl$saraminApiEndpoint?keywords=Android&count=10&access-key=$saraminApiKey") //수정하지 말기
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Saramin API", "Failed to execute request", e)
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body?.string()
                    Log.d("Saramin API", "Response body: $responseBody")

                    val jsonObject = JSONObject(responseBody)
                    jsonObject.put("access-key", "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway")
                    jsonObject.put("keyword", "의료")
                    Log.d("qwerqwerqwer", "$jsonObject")

                    val jobsArray = jsonObject.optJSONArray("jobs")
                    Log.d("qazqaz", "$jobsArray")
                    if (jobsArray != null && jobsArray.length() > 0) {
                        val jobObject = jobsArray.optJSONObject(0)
                        Log.d("Saramin API1", "$jobsArray")
                        val position = jobObject?.optString("position")
                        if (position != null) {
                            // Update UI component with position value from the main thread
                            activity?.runOnUiThread {
                                binding.textView3.text = position
                            }
                        } else {
                            Log.d("Saramin API1", "$jobsArray")
                        }
                    } else {
                        Log.d("Saramin API1", "jobsArray is null or empty")
                    }
                }
            }
        })
    }
}
