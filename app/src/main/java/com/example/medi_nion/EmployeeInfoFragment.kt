package com.example.medi_nion

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.employee_info.*
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
import java.util.ArrayList

class EmployeeInfoFragment : Fragment() {

    private lateinit var textView: TextView

    private lateinit var name1: TextView
    private lateinit var name2: TextView
    private lateinit var name3: TextView
    private lateinit var name4: TextView
    private lateinit var name5: TextView
    private lateinit var name6: TextView
    private lateinit var name7: TextView
    private lateinit var name8: TextView
    private lateinit var name9: TextView
    private lateinit var name10: TextView
    private lateinit var name11: TextView
    private lateinit var name12: TextView
    private lateinit var name13: TextView
    private lateinit var name14: TextView
    private lateinit var name15: TextView

    private var items = ArrayList<EmployeeRecyItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.employee_info, container, false)
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val accessKey = "jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway" // 발급받은 accessKey"
//        val text = URLEncoder.encode("", "UTF-8")

        name1 = view.findViewById(R.id.name1)
        name2 = view.findViewById(R.id.name2)
        name3= view.findViewById(R.id.name3)
        name4 = view.findViewById(R.id.name4)
        name5 = view.findViewById(R.id.name5)
        name6 = view.findViewById(R.id.name6)
        name7 = view.findViewById(R.id.name7)
        name8 = view.findViewById(R.id.name8)
        name9 = view.findViewById(R.id.name9)
        name10 = view.findViewById(R.id.name10)
        name11 = view.findViewById(R.id.name11)
        name12 = view.findViewById(R.id.name12)
        name13 = view.findViewById(R.id.name13)
        name14 = view.findViewById(R.id.name14)
        name15 = view.findViewById(R.id.name15)


        name1.setOnClickListener {
            Toast.makeText(context, "전체 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&fields=expiration-date&count=110&job_mid_cd=6"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name2.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name3.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name4.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name5.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name6.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name7.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name8.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }


        name9.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name10.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name11.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name12.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name13.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name14.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=576"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList:JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList:JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail").getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience = position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level").getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(url, company, title, loca, experience, school, deadlineType, deadline)
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object:EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(v: View, data: EmployeeRecyItem, pos: Int) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
                }
            }
        }

        name15.setOnClickListener {
            Toast.makeText(context, "정형외과 선택", Toast.LENGTH_SHORT).show()
            val apiURL =
                "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway&count=110&job_mid_cd=6&job_cd=572"
            GlobalScope.launch {
                try {
                    val response = getResponseFromApi(apiURL)
                    val responseBody = response?.body?.string()
                    withContext(Dispatchers.Main) {
                        responseBody?.let {
                            items.clear()
                            val jsonObj: JSONObject = JSONObject(responseBody)
                            val jobsList: JSONObject = jsonObj.get("jobs") as JSONObject
                            val jobList: JSONArray = jobsList.get("job") as JSONArray

                            for (i in 0 until jobList.length()) {
                                val item = jobList.getJSONObject(i)

                                val url = item.getString("url")
                                val company = item.getJSONObject("company").getJSONObject("detail")
                                    .getString("name")
                                val position = item.getJSONObject("position")
                                val title = position.getString("title")
                                val loca = position.getJSONObject("location").optString("name")
                                val experience =
                                    position.getJSONObject("experience-level").getString("name")
                                val school = position.getJSONObject("required-education-level")
                                    .getString("name")
                                val deadlineType = item.getJSONObject("close-type").optInt("code")
                                val deadline = item.optString("expiration-date")

                                val infoItem = EmployeeRecyItem(
                                    url,
                                    company,
                                    title,
                                    loca,
                                    experience,
                                    school,
                                    deadlineType,
                                    deadline
                                )
                                items.add(infoItem)
                            }
                            val adapter = EmployeeRecyAdapter(items)
                            employee_recycler.adapter = adapter

                            adapter.setOnItemClickListener(object :
                                EmployeeRecyAdapter.OnItemClickListener {
                                override fun onItemClick(
                                    v: View,
                                    data: EmployeeRecyItem,
                                    pos: Int
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    startActivity(intent)
                                }
                            })
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        //textView.text = "Error occurred: ${e.message}"
                    }
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