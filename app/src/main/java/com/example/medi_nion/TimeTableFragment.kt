package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.json.JSONArray
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*


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
//        table.updateSchedules(scheduleList)

        table.baseSetting(30, 20, 60) //default (20, 30, 50)
        table.isFullWidth(true)
        table.isTwentyFourHourClock(true)

        val schedule = ScheduleEntity(
            id, //originId
            "Database", //scheduleName
            "2023-05-01-Tue", //roomInfo
            "18:20", //ScheduleDay object (MONDAY ~ SUNDAY)
            "20:30", //startTime format: "HH:mm"
            "#73fcae68", //endTime  format: "HH:mm"
            "설정 안함", //backgroundColor (optional)
            "설정 안함", //textcolor (optional)
        "",
            false
        )

        scheduleList.add(schedule)
        table.updateSchedules(scheduleList)



        return view
    }

    fun fetchEvent() {
        val id = arguments?.getString("id").toString()
        val presentDate = arguments?.getString("presentDate").toString()
        val year = arguments?.getString("year").toString()
        val month = arguments?.getString("month").toString()
        val date = arguments?.getString("date").toString()
        val week = arguments?.getString("week").toString()

        val table = view?.findViewById<MinTimeTableView>(R.id.table)
        val url = "http://seonho.dothome.co.kr/Events2.php"
        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("j2132",response)
                if(response != "Event fetch Fail") {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val id = item.getString("id")
                        val schedule_name = item.getString("schedule_name")
                        val schedule_date = item.getString("schedule_date")
                        val schedule_start = item.getString("schedule_start")
                        val schedule_end = item.getString("schedule_end")
                        val schedule_color = item.getString("schedule_color")
                        val schedule_alarm = item.getString("schedule_alarm")
                        val schedule_repeat = item.getString("schedule_repeat")
                        val schedule_memo = item.getString("schedule_memo")
                        val isDone = item.getString("isDone").toBoolean()

                        val CalendarItem = ScheduleEntity(
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

                        table?.initTable(day)
                        scheduleList.add(CalendarItem)
                        table?.updateSchedules(scheduleList)
                    }
                }
            }, { Log.d("login failed", "error......${error(this)}") },
            hashMapOf(
                "id" to id,
                "day" to presentDate,
                "year" to year,
                "month" to month,
                "date" to date,
                "week" to week
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
}



