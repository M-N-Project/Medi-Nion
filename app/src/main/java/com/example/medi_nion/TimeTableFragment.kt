package com.example.medi_nion

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleEntity
import org.json.JSONArray


class TimeTableFragment : Fragment() { //간호사 스케쥴표 화면(구현 어케하누,,) -> 어케든 하고있는 멋진 혹은 불쌍한 우리;
    private val day = arrayOf("Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun")
    private val scheduleList: ArrayList<ScheduleEntity> = ArrayList()
    val id = arguments?.getString("id").toString()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.time_table, container, false)
        val table = view.findViewById<MinTimeTableView>(R.id.table)
        table.initTable(day)

        table.baseSetting(30, 20, 60) //default (20, 30, 50)
        table.isFullWidth(true)
        table.isTwentyFourHourClock(true)

        fetchEvent(view)

        return view
    }

    override fun onPause() {
        super.onPause()
        getFragmentManager()?.let { refreshFragment(this, it) }
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun fetchEvent(view: View) {
        val id = arguments?.getString("id").toString()
        val table = view.findViewById<MinTimeTableView>(R.id.table)
        val url = "http://seonho.dothome.co.kr/timeTableEvents.php"

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("ㅇㄹㅇㄴ", response)
                if(response != "Event fetch Fail") {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val schedule_name = item.getString("schedule_name")
                        val schedule_date = item.getString("schedule_date")
                        val schedule_start = item.getString("schedule_start")
                        val schedule_end = item.getString("schedule_end")
                        val schedule_color = item.getString("schedule_color")
                        val schedule_alarm = item.getString("schedule_alarm")
                        val schedule_repeat = item.getString("schedule_repeat")
                        val schedule_memo = item.getString("schedule_memo")
                        val isDone = item.getString("isDone").toBoolean()

                        val schedule = ScheduleEntity(
                            id,
                            schedule_name,
                            schedule_date,
                            schedule_start,
                            schedule_end,
                            schedule_color,
                            schedule_alarm,
                            schedule_repeat,
                            schedule_memo,
                            isDone
                        )

                        scheduleList.add(schedule)
                        Log.d("SCHE", scheduleList.toString())
                        table.updateSchedules(scheduleList)

                        val ft = fragmentManager!!.beginTransaction()
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false)
                        }
                        Log.d("SDFDF", "SDF6")
                        ft.detach(this).attach(this).commit()

                    }
                }
            }, { Log.d("login failed", "error......${error(this)}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
}



