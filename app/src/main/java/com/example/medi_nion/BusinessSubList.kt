package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.board_home.refresh_layout
import kotlinx.android.synthetic.main.business_sub_list.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*


private var items =ArrayList<BusinessChanListItem>()
private var all_items = ArrayList<BusinessChanListItem>()
private val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
private var scroll_count = 1
//val viewModel = BoardViewModel()
//lateinit var adapter : BoardListAdapter
private var adapter = BusinessSubListAdapter(items)
private var scrollFlag = false
private var itemIndex = ArrayList<Int>()

class BusinessSubList : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart() //프레그먼트로 생길 문제들은 추후에 생각하기,,

        fetchData()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_sub_list)

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경

        items.clear()
        all_items.clear()

        businessChanListRecyclerView.setLayoutManager(businessChanListRecyclerView.layoutManager);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")
        var subListUrl = "http://seonho.dothome.co.kr/profileBusinessSubChan_list.php"

        items.clear()
        val request = Login_Request(
            Request.Method.POST,
            subListUrl,
            { response ->
                Log.d("897123", response)
                if (!response.equals("business sub list fail")) {
                    if(!response.equals("business sub list no Item")){
                        val jsonArray = JSONArray(response)
                        for (i in jsonArray.length()-1  downTo  0) {
                            val item = jsonArray.getJSONObject(i)

                            val channel_name = item.getString("channel_name")
                            val channel_message = item.getString("channel_message")
                            val id = item.getString("id")
                            val profileImg = item.getString("profile_img")

                            var recyclerViewState = businessChanListRecyclerView.layoutManager?.onSaveInstanceState()
                            val businessChanItem = BusinessChanListItem(channel_name, channel_message, profileImg)
                            items.add(businessChanItem)
                            adapter = BusinessSubListAdapter(items)
                            businessChanListRecyclerView.adapter = adapter
                            adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                            businessChanListRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)

                            adapter.setOnItemClickListener(object : BusinessSubListAdapter.OnItemClickListener {
                                override fun onItemProfile(v: View, data: BusinessChanListItem, pos: Int) {
                                    var id = intent.getStringExtra("id")!!
                                    val intent = Intent(applicationContext, BusinessProfileActivity::class.java)
                                    intent.putExtra("appUser", id)
                                    intent.putExtra("channel_name", data.chan_name)
                                    startActivity(intent)
                                }

                            })

                        }
                    }
                    else{
                        findViewById<TextView>(R.id.noSubList).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.noSubList).bringToFront()
                    }
                }

            }, { Log.d("login failed", "error......${this?.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id.toString()
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun Millis(postTime : String) : Long {
//        // YY-MM-DD HH:MM:SS
//
//        //val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd, hh:mm:ss")
//        //val date = LocalDateTime.parse(dateString, formatter)
//
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date1: Date = simpleDateFormat.parse(postTime)
//        return date1.time
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun timeDiff(postTime : String): String {
//        var SEC = 60
//        var MIN = 60
//        var HOUR = 24
//        var DAY = 30
//        var MONTH = 12
//
//        val curTime = System.currentTimeMillis()
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR"))
//        val cur: String = simpleDateFormat.format(Date(curTime))
//
//        val newPostTime = Millis(postTime)
//        var diffTime = (curTime - newPostTime)/1000
//        var msg: String = ""
//
//        if (diffTime  < SEC) {
//            msg = "방금 전";
//        } else if ((diffTime / SEC) < MIN) {
//            msg = (diffTime / SEC).toString() + "분 전";
//        } else if (((diffTime / SEC) / MIN) < HOUR) {
//            msg = ((diffTime / SEC) / MIN).toString() + "시간 전";
//        } else if ((((diffTime / SEC) / MIN) / HOUR) < DAY) {
//            msg = (((diffTime / SEC) / MIN) / HOUR).toString() + "일 전";
//        } else if (((((diffTime / SEC) / MIN) / HOUR) / DAY) < MONTH) {
//            msg = ((((diffTime / SEC) / MIN) / HOUR) / DAY).toString() + "달 전";
//        } else {
//            msg = (((((diffTime / SEC) / MIN) / HOUR) / DAY) / MONTH ).toString() + "년 전"
//        }
//
//        return msg
//    }

}