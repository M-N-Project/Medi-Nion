package com.example.medi_nion

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.notificationview.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity: AppCompatActivity() {
    private var items = ArrayList<NotificationItem>()
    private var all_items = ArrayList<NotificationItem>()
    private val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    private var scroll_count = 1
    private var adapter = NotificationListAdapter(items)
    private var scrollFlag = false
    private var itemIndex = ArrayList<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        fetchNoti()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notificationview)

        refresh_layout.setColorSchemeResources(R.color.color5) //새로고침 색상 변경

        items.clear()
        all_items.clear()

        notification_recyclerView.setLayoutManager(notification_recyclerView.layoutManager)

        val id = intent.getStringExtra("id")
        val nickname = intent.getStringExtra("nickname")


        notification_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(scrollFlag==false){
                    if (!notification_recyclerView.canScrollVertically(-1)) { //맨 위
                        refresh_layout.setOnRefreshListener { //새로고침
                            try {
                                val intent = intent
                                finish() //현재 액티비티 종료 실시
                                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                                startActivity(intent) //현재 액티비티 재실행 실시
                                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                                refresh_layout.isRefreshing = false //새로고침 없애기
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else if (!boardRecyclerView.canScrollVertically(1)) { //맨 아래
                        //로딩
                        if(all_items.size > 20){
                            scrollFlag = true

                            var progressBar : ProgressBar = findViewById(R.id.progressBar2)
                            progressBar.visibility = View.VISIBLE

                            Handler(Looper.getMainLooper()).postDelayed({
                                progressBar.visibility = View.INVISIBLE
                            }, 2500)

                            if((all_items.size - item_count*scroll_count) > 20){
                                for (i in (item_count * scroll_count) + (item_count-1)  downTo   (item_count * scroll_count) + 0) {
                                    items.add(all_items[i])
//                                    itemIndex.add(all_items[i].num) //앞에다가 추가.
                                }

                                var recyclerViewState = notification_recyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<NotificationItem>()
                                new_items.addAll(items)
                                adapter = NotificationListAdapter(new_items)
                                notification_recyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                notification_recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                                scrollFlag = false
                            }
                            else{
                                for (i  in all_items.size-1  downTo   (item_count* scroll_count)) {
                                    items.add(all_items[i])
//                                    itemIndex.add(all_items[i].num) //앞에다가 추가.
                                }
                                var recyclerViewState = notification_recyclerView.layoutManager?.onSaveInstanceState()
                                var new_items = ArrayList<NotificationItem>()
                                new_items.addAll(items)
                                adapter = NotificationListAdapter(new_items)
                                notification_recyclerView.adapter = adapter
                                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                notification_recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }
                            scroll_count ++
                        }
                    }
                }
            }
        })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchNoti() {
        // url to post our data
        var id = intent.getStringExtra("id").toString()
        var nickname = intent.getStringExtra("nickname").toString()
        val urlBoard = "http://seonho.dothome.co.kr/Board.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"
        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                all_items.clear()

                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")

                    val simpleTime = timeDiff(time)

                    val notificationItem = NotificationItem(title, content, simpleTime)

                    if(i >= jsonArray.length() - item_count*scroll_count){
                        items.add(notificationItem)
//                        itemIndex.add(num) //앞에다가 추가.
                    }
                    all_items.add(notificationItem)
                }

                var recyclerViewState = notification_recyclerView.layoutManager?.onSaveInstanceState()
                var new_items = ArrayList<NotificationItem>()
                new_items.addAll(items)
                adapter = NotificationListAdapter(new_items)
                notification_recyclerView.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                notification_recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);

                var detailTitle : String = ""
                var detailContent : String = ""
                var detailTime : String = ""

                //알람 누르면
                adapter.setOnItemClickListener(object : NotificationListAdapter.OnItemClickListener {
                   override fun onItemClick(v: View, data: NotificationItem, pos: Int) {
                       val intent = Intent(this@NotificationActivity, Profile_opencv::class.java)
                       intent.putExtra("id", id)
                       intent.putExtra("nickname", nickname)
                       startActivity(intent)
                    }
                })
            }, { Log.d("login failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Millis(postTime : String) : Long {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date1: Date = simpleDateFormat.parse(postTime)
        return date1.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeDiff(postTime : String): String {
        var SEC = 60
        var MIN = 60
        var HOUR = 24
        var DAY = 30
        var MONTH = 12

        val curTime = System.currentTimeMillis()

        val newPostTime = Millis(postTime)
        var diffTime = (curTime - newPostTime)/1000
        var msg: String = ""

        if (diffTime  < SEC) {
            msg = "방금 전";
        } else if ((diffTime / SEC) < MIN) {
            msg = (diffTime / SEC).toString() + "분 전";
        } else if (((diffTime / SEC) / MIN) < HOUR) {
            msg = ((diffTime / SEC) / MIN).toString() + "시간 전";
        } else if ((((diffTime / SEC) / MIN) / HOUR) < DAY) {
            msg = (((diffTime / SEC) / MIN) / HOUR).toString() + "일 전";
        } else if (((((diffTime / SEC) / MIN) / HOUR) / DAY) < MONTH) {
            msg = ((((diffTime / SEC) / MIN) / HOUR) / DAY).toString() + "달 전";
        } else {
            msg = (((((diffTime / SEC) / MIN) / HOUR) / DAY) / MONTH ).toString() + "년 전"
        }
        return msg
    }

}