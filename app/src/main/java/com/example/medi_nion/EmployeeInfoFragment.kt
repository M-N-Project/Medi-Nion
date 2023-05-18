package com.example.medi_nion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.employee_info.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.bytedeco.javacpp.opencv_core.finish
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EmployeeInfoFragment : Fragment() {
    private lateinit var locaSelected : String
    private lateinit var deptSelected : String
    private lateinit var hosSelected : String
    fun setter(location:String, dept:String, hospital:String){
        this.locaSelected=location
        this.deptSelected=dept
        this.hosSelected=hospital
    }

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
        setHasOptionsMenu(true)
        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar2)
        //toolbar.inflateMenu(R.menu.employee_titlebar)
        //toolbar.setNavigationIcon(R.drawable.arrow_resize)
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_resize1)
        if(locaSelected=="null" && deptSelected == "null" && hosSelected == "null") toolbar.subtitle = "채용 정보"
            else toolbar.subtitle="$locaSelected $deptSelected $hosSelected"
        toolbar.setNavigationOnClickListener{
            activity?.onBackPressed()
        }
        toolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.filter -> {
                    activity?.let {
                        val intent = Intent(context, EmployeeFiltering::class.java)
                        startActivity(intent)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        refresh_layout1.setColorSchemeResources(R.color.color5)
        refresh_layout1.setOnRefreshListener {
            try {
                val intent = activity?.intent
                activity?.finish() // Finish the current Activity
                activity?.overridePendingTransition(0, 0) // Disable transition animation
                intent?.let {
                    activity?.startActivity(it) // Start the Activity again
                }
                activity?.overridePendingTransition(0, 0) // Disable transition animation

                refresh_layout1.isRefreshing = false // Hide the refresh indicator if needed
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }



        val locaCode:String = if(locaSelected?.equals("서울") == true) "&loc_cd=101000"
        else if(locaSelected?.equals("경기") == true) "&loc_cd=102000"
        else if(locaSelected?.equals("광주") == true) "&loc_cd=103000"
        else if(locaSelected?.equals("대전") == true) "&loc_cd=105000"
        else if(locaSelected?.equals("부산") == true) "&loc_cd=106000"
        else if(locaSelected?.equals("대구") == true) "&loc_cd=104000"
        else if(locaSelected?.equals("울산") == true) "&loc_cd=107000"
        else if(locaSelected?.equals("인천") == true) "&loc_cd=108000"
        else if(locaSelected?.equals("강원") == true) "&loc_cd=109000"
        else if(locaSelected?.equals("경남") == true) "&loc_cd=110000"
        else if(locaSelected?.equals("경북") == true) "&loc_cd=111000"
        else if(locaSelected?.equals("전남") == true) "&loc_cd=112000"
        else if(locaSelected?.equals("전북") == true) "&loc_cd=113000"
        else if(locaSelected?.equals("충북") == true) "&loc_cd=114000"
        else if(locaSelected?.equals("충남") == true) "&loc_cd=115000"
        else if(locaSelected?.equals("제주") == true) "&loc_cd=116000"
        else if(locaSelected?.equals("전국") == true) "&loc_cd=117000"
        else if(locaSelected?.equals("세종") == true) "&loc_cd=118000"
        else ""

        val deptCode:String = if(deptSelected?.equals("병원·진료")==true) "70101"
        else if(deptSelected?.equals("산부인과")==true) "70102"
        else if(deptSelected?.equals("치과")==true) "70103"
        else if(deptSelected?.equals("안과")==true) "70104"
        else if(deptSelected?.equals("정형외과")==true) "70105"
        else if(deptSelected?.equals("이비인후과")==true) "70106"
        else if(deptSelected?.equals("소아과")==true) "70107"
        else if(deptSelected?.equals("성형외과")==true) "70108"
        else if(deptSelected?.equals("임상병리")==true) "70109"
        else if(deptSelected?.equals("한의원")==true) "70110"
        else if(deptSelected?.equals("내과")==true) "70112"
        else if(deptSelected?.equals("외과")==true) "70113"
        else if(deptSelected?.equals("의료기기")==true) "70115"
        else if(deptSelected?.equals("응급구조")==true) "70116"
        else if(deptSelected?.equals("피부과")==true) "70117"
        else if(deptSelected?.equals("물리치료")==true) "70118"
        else if(deptSelected?.equals("가정의학과")==true) "70119"
        else if(deptSelected?.equals("신경외과")==true) "70120"
        else if(deptSelected?.equals("대장항문과")==true) "70121"
        else if(deptSelected?.equals("비뇨기과")==true) "70122"
        else if(deptSelected?.equals("신경정신과")==true) "70123"
        else if(deptSelected?.equals("재활의학과")==true) "70124"
        else if(deptSelected?.equals("영상의학과")==true) "70125"
        else if(deptSelected?.equals("중환자실")==true) "70126"
        else if(deptSelected?.equals("인공신장실")==true) "70127"
        else ""

        val hosCode:String = if(hosSelected?.equals("대학병원")== true) "70201"
        else if (hosSelected?.equals("종합병원")== true) "70202"
        else if (hosSelected?.equals("전문병원")== true) "70203"
        else if (hosSelected?.equals("검진병원")== true) "70204"
        else if (hosSelected?.equals("정신병원")== true) "70205"
        else if (hosSelected?.equals("요양병원")== true) "70206"
        else if (hosSelected?.equals("국공립병원")== true) "70207"
        else if (hosSelected?.equals("보건소")== true) "70208"
        else if (hosSelected?.equals("노인병원")== true) "70209"
        else if (hosSelected?.equals("한방병원")== true) "70210"
        else ""

        val indCode: String = if (deptSelected == null && hosSelected == null) {
            ""
        } else if (deptSelected != null && hosSelected == null) {
            "ind_cd=$deptCode"
        } else if (deptSelected == null && hosSelected != null) {
            "ind_cd=$hosCode"
        } else {
            "&ind_cd=$deptCode,$hosCode"
        }


        val apiURL = "https://oapi.saramin.co.kr/job-search?access-key=jyadKDRGVi7FGKeg03ZM6FS3nQiSVB9TCENCtBIimhWDywFEway$locaCode$indCode&fields=expiration-date+keyword-code&count=110&job_mid_cd=6"
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