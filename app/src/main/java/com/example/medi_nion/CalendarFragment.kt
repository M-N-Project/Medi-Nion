package com.example.medi_nion

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import org.json.JSONArray
import java.util.*


class CalendarFragment : Fragment() { //간호사 스케쥴표 화면(구현 어케하누,,)
    private lateinit var CalendarRecyclerView : RecyclerView
    var items = ArrayList<CalendarItem>()
    var adapter = CalendarRecyclerAdapter(items)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.calendar, container, false)

        CalendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        CalendarRecyclerView.adapter = adapter
        //CalendarDay.today()를 가지고와서 오늘 날짜에 맞는 일정 가져와서 adapter 붙여주고 시작하기.

        val calendar = view.findViewById<MaterialCalendarView>(R.id.calendarView)
        val makeEventBtn = view.findViewById<FloatingActionButton>(R.id.makeEvent)
        val makeEventRadiogroup = view.findViewById<RadioGroup>(R.id.select_RadioGroup_MakeEvent)
        val makeEventScheduleRadioBtn= view.findViewById<RadioButton>(R.id.schedule_RadioBtn)
        val makeEventButtonRadioBtn = view.findViewById<RadioButton>(R.id.customBtn_RadioBtn)

        calendar.setSelectedDate(CalendarDay.today())
        calendar.apply {
            //캘린더 오늘 날짜 -> 원 색상 변경, 글씨 크기 변경
            calendar.addDecorators(TodayDecorator(), SaturdayDecorator(), SundayDecorator())
            // 요일을 지정
            setWeekDayLabels(arrayOf("일", "월", "화", "수", "목", "금", "토"))
        }
        calendar.setOnDateChangedListener(MyDaySelected())
        DateFormatTitleFormatter()

        
        //스케줄 만들기
        makeEventBtn.setOnClickListener{
            if(makeEventRadiogroup.visibility == View.VISIBLE)
                makeEventRadiogroup.visibility = View.GONE
            else makeEventRadiogroup.visibility = View.VISIBLE

            makeEventScheduleRadioBtn.bringToFront()
            makeEventButtonRadioBtn.bringToFront()

            //새로운 일정 만들기
            makeEventScheduleRadioBtn.setOnClickListener{
                val intent = Intent(context, Calendar_Add::class.java)
                startActivity(intent)
            }
            //커스터마이징 일정 버튼 만들기
            makeEventButtonRadioBtn.setOnClickListener{
                Log.d("90182312", "click")
            }
        }
        return view
    }

    //오늘 날짜의 디자인 변경
    inner class TodayDecorator : DayViewDecorator {
        private var date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(RelativeSizeSpan(1.0f))
        }
    }

    //선택 날짜가 달라지면 그에 맞는 일정들 가져와서 adapter붙여주기.
    inner class MyDaySelected : OnDateSelectedListener{
        override fun onDateSelected(
            widget: MaterialCalendarView,
            date: CalendarDay,
            selected: Boolean
        ) {

            Log.d("hihiDate", date.toString())

        }

    }
    
    //토요일 색상 변경
    inner class SaturdayDecorator: DayViewDecorator {

        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val saturday = calendar.get(Calendar.DAY_OF_WEEK)
            return saturday == Calendar.SATURDAY
        }
        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object: ForegroundColorSpan(Color.BLUE){})
        }
    }

    inner class SundayDecorator: DayViewDecorator {

        private var calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val saturday = calendar.get(Calendar.DAY_OF_WEEK)
            return saturday == Calendar.SUNDAY
        }
        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object: ForegroundColorSpan(Color.RED){})
        }
    }

    private fun fetchEvents(){
        @RequiresApi(Build.VERSION_CODES.O)
        fun fetchData() {
            // url to post our data
            val id = arguments?.getString("id")

            val urlBoard = "http://seonho.dothome.co.kr/Events.php"

            val request = Board_Request(
                Request.Method.POST,
                urlBoard,
                { response ->
                    val jsonArray = JSONArray(response)
                    items.clear()

                    for (i in jsonArray.length()-1  downTo  0) {

                    }
//                    var recyclerViewState = boardRecyclerView.layoutManager?.onSaveInstanceState()
//                    var new_items = ArrayList<BoardItem>()
//                    new_items.addAll(items)
//                    adapter = BoardListAdapter(new_items)
//                    boardRecyclerView.adapter = adapter
//                    adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
//                    boardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);



                }, { Log.d("login failed",
                    "error......${context?.let { it1 -> error(it1) }}") },
                hashMapOf(
                    "id" to id.toString()
                )
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(request)

        }
    }
}



