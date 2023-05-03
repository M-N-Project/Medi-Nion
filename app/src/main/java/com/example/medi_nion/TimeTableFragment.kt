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
    private val day = arrayOf("Mon", "Tue", "Wen", "Thu", "Fri","Sat", "Sun")
    private val scheduleList: ArrayList<ScheduleEntity> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.time_table, container, false)

        val table = view.findViewById<MinTimeTableView>(R.id.table)
        table.initTable(day)

        table.updateSchedules(scheduleList)

        table.baseSetting(30, 20, 60) //default (20, 30, 50)
        table.isFullWidth(true)
        table.isTwentyFourHourClock(true)

        val schedule = ScheduleEntity(
            32, //originId
            "Database", //scheduleName
            "IT Building 301", //roomInfo
            ScheduleDay.TUESDAY, //ScheduleDay object (MONDAY ~ SUNDAY)
            "18:20", //startTime format: "HH:mm"
            "20:30", //endTime  format: "HH:mm"
            "#73fcae68", //backgroundColor (optional)
            "#000000" //textcolor (optional)
        )

        val schedule2 = ScheduleEntity(
            32, //originId
            "Database", //scheduleName
            "IT Building 301", //roomInfo
            ScheduleDay.MONDAY, //ScheduleDay object (MONDAY ~ SUNDAY)
            "13:20", //startTime format: "HH:mm"
            "19:30", //endTime  format: "HH:mm"
            "#73fcae68", //backgroundColor (optional)
            "#000000" //textcolor (optional)
        )

        val schedule3 = ScheduleEntity(
            32, //originId
            "Database", //scheduleName
            "IT Building 301", //roomInfo
            ScheduleDay.WEDNESDAY, //ScheduleDay object (MONDAY ~ SUNDAY)
            "10:20", //startTime format: "HH:mm"
            "14:30", //endTime  format: "HH:mm"
            "#73fcae68", //backgroundColor (optional)
            "#000000" //textcolor (optional)
        )

        scheduleList.add(schedule)
        scheduleList.add(schedule2)
        scheduleList.add(schedule3)
        table.updateSchedules(scheduleList)


        return view
    }


}



