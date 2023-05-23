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
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.R.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.json.JSONArray
import java.util.*


class CalendarFragment : Fragment() { //간호사 스케쥴표 화면(구현 어케하누,,) -> 어케든 하고있는 멋진 혹은 불쌍한 우리;
    private var selectedColor: Int = ColorSheet.NO_COLOR
    private lateinit var calendarRecyclerView : RecyclerView
    private val items = ArrayList<CalendarItem>()
    var adapter = CalendarRecyclerAdapter(items)
    var currentDate : CalendarDay = CalendarDay.today()

    private lateinit var darkOverlay: View
    var isFABOpen : Boolean = false

    var lastSelected = ""

    private var oldTitle : String = ""
    private var oldStartTime : String = ""

    companion object {
        var viewModel: CalendarViewModel  = CalendarViewModel()
        private const val COLOR_SELECTED = "selectedColor"
    }

    override fun onPause() {
        super.onPause()
        lastSelected = ""
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(layout.calendar, container, false)

        val darkOverlay = view.findViewById<View>(R.id.darkOverlay)

        fetchEvents(currentDate)

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        adapter = CalendarRecyclerAdapter(items)
        calendarRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)

        viewModel.itemList.observe(viewLifecycleOwner) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            fetchEvents(currentDate)
        }


        //CalendarDay.today()를 가지고와서 오늘 날짜에 맞는 일정 가져와서 adapter 붙여주고 시작하기.

        val calendar = view.findViewById<MaterialCalendarView>(R.id.calendarView)

        val id = arguments?.getString("id").toString()
        val colorList: Array<String> = resources.getStringArray(array.colors)

        calendar.setSelectedDate(CalendarDay.today())
        calendar.apply {
            //캘린더 오늘 날짜 -> 원 색상 변경, 글씨 크기 변경
            calendar.addDecorators(TodayDecorator(), SaturdayDecorator(), SundayDecorator())
            // 요일을 지정
            setWeekDayLabels(arrayOf("일", "월", "화", "수", "목", "금", "토"))
        }
        calendar.setOnDateChangedListener(MyDaySelected())
        DateFormatTitleFormatter()

        val makeEventBtn = view.findViewById<FloatingActionButton>(R.id.makeEvent)
        val newEventFAB= view.findViewById<FloatingActionButton>(R.id.newEvent)
        val newEventTextView = view.findViewById<TextView>(R.id.newEvent_textView)
        val fixEventFAB = view.findViewById<FloatingActionButton>(R.id.fixEvent)
        val fixEventTextView = view.findViewById<TextView>(R.id.fixEvent_textView)


        //스케줄 만들기
        makeEventBtn.setOnClickListener {
            if (!isFABOpen) {
                darkOverlay.visibility = View.VISIBLE
                darkOverlay.bringToFront()

                makeEventBtn.setImageResource(drawable.event_cancel_button_resize)
                newEventTextView.visibility = View.VISIBLE
                fixEventTextView.visibility = View.VISIBLE

                showFABMenu(newEventFAB, newEventTextView, fixEventFAB, fixEventTextView)
                makeEventBtn.bringToFront()
                newEventTextView.bringToFront()
                fixEventTextView.bringToFront()

                newEventFAB.setOnClickListener{
                    val intent = Intent(context, Calendar_Add::class.java)
                    intent.putExtra("id", id)

                    val week = currentDate.date.toString().substring(0,3)
                    Log.d("018323", currentDate.toString())
                    intent.putExtra("day", "${currentDate.toString()}/$week")
                    intent.putExtra("flag", "calendar")
                    startActivity(intent)
                }

                fixEventFAB.setOnClickListener{
                    val intent = Intent(context, Calendar_History_Add::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("day", CalendarDay.today().toString())
                    startActivity(intent)
                }
            } else {
                darkOverlay.visibility = View.GONE

                makeEventBtn.setImageResource(drawable.create_button_resize)
                newEventTextView.visibility = View.GONE
                fixEventTextView.visibility = View.GONE


                closeFABMenu(newEventFAB, newEventTextView, fixEventFAB, fixEventTextView)
                makeEventBtn.bringToFront()
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
            currentDate = date
            fetchEvents(date)

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

    //일요일 색상 변경
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

    private fun fetchEvents(day : CalendarDay){
        // url to post our data
        val id = arguments?.getString("id").toString()

        val urlBoard = "http://seonho.dothome.co.kr/Events2.php"

        val year = day.toString().substring(12,16)
        var month = day.toString().substring(17,19)
        var date = ""
        var week = day.date.toString().substring(0,3)
        if(month.substring(1,2) == "-"){
            month = "0${(day.toString().substring(17,18)).toInt() + 1}"
            date = day.toString().substring(19,21)

            if(date.substring(1,2) == "}")
                date = "0${day.toString().substring(19,20)}"


        }
        else{
            month = (month.toInt()+1).toString()
            date = day.toString().substring(20,22)


            if(date.substring(1,2) == "}")
                date = "0${day.toString().substring(20, 21)}"

        }

        val presentDate = "$year-$month-$date-$week"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                Log.d("j2132",response)
                if(response != "Event fetch Fail"){
                    val jsonArray = JSONArray(response)
                    items.clear()

                    for (i in 0  until  jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val schedule_name = item.getString("schedule_name")
                        val schedule_date = item.getString("schedule_date")
                        val schedule_start = item.getString("schedule_start")
                        val schedule_end = item.getString("schedule_end")
                        val schedule_color = item.getString("schedule_color")
                        val schedule_alarm = item.getString("schedule_alarm")
                        val schedule_repeat = item.getString("schedule_repeat")
                        val schedule_memo = item.getString("schedule_memo")
                        val isDone = item.getString("isDone")

                        val CalendarItem = CalendarItem(id, schedule_name, schedule_date, schedule_start, schedule_end, schedule_color, schedule_alarm, schedule_repeat, schedule_memo,  if(isDone == "0") false else true)
                        items.add(CalendarItem)
                        viewModel.addItemList(CalendarItem)
                    }
                    adapter = CalendarRecyclerAdapter(items)
                    calendarRecyclerView.adapter = adapter

                    adapter.setOnItemClickListener(object :
                        CalendarRecyclerAdapter.OnItemClickListener {
                        @SuppressLint("MissingInflatedId")
                        override fun onEventClick(v: View, data: CalendarItem, pos: Int) {
                            oldTitle = data.schedule_name
                            oldStartTime = data.schedule_start
                            //이벤트 하나 누르면 그에 맞는 alert 팝업 창 -> 이벤트 정보들.
                            val bottomSheetView = layoutInflater.inflate(layout.calendar_dialog, null)
                            val bottomSheetDialog = BottomSheetDialog(requireContext())
                            bottomSheetDialog.setContentView(bottomSheetView)

                            val deleteScheduleBtn = bottomSheetView.findViewById<ImageView>(R.id.deleteScheduleBtn)
                            deleteScheduleBtn.setOnClickListener{
                                //삭제하시겠습니까??
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle("일정 삭제")
                                    .setMessage("일정을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            deleteSchedule(data)
                                        })
                                    .setNegativeButton("취소",
                                        DialogInterface.OnClickListener { dialog, id ->

                                        })

                                // 다이얼로그를 띄워주기
                                builder.show()

                            }

                            val dialog_date = bottomSheetView.findViewById<TextView>(R.id.dateTextView)
                            dialog_date.text = data.schedule_date

                            val schedule_title = bottomSheetView.findViewById<EditText>(R.id.editText_scheduleName)
                            schedule_title.setText(data.schedule_name) //스케줄 이름

                            val day_night1 = bottomSheetView.findViewById<TextView>(R.id.start_day_night)
                            val start = bottomSheetView.findViewById<LinearLayout>(R.id.start_time_linear)
                            val start_result = bottomSheetView.findViewById<TextView>(R.id.start_time)
                            var startString = ""

                            start_result.text = data.schedule_start.replace(" ", "")

                            var start_min = start_result.text.substring(0,2)
                            var start_sec = start_result.text.substring(3,5)
                            Log.d("start..", "${start_min}, ${start_sec}")
                            day_night1.setText("오전") //스케줄 시작 오전/오후
                            start_result.setText("${start_min}   :   ${start_sec}") //스케줄 시작 시간
                            Log.d("start..", start_result.text.toString())

                            start.setOnClickListener {
                                val dialog = TimePickerDialog(
                                    requireContext(),
                                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                                    { view, HourOfDay, Minutes ->
                                        if (HourOfDay >= 12)
                                            day_night1.text = "오후"
                                        else
                                            day_night1.text = "오전"

                                        if (HourOfDay>= 10) {
                                            if(Minutes >= 10) {
                                                startString = "${HourOfDay}   :   ${Minutes}"
                                            } else {
                                                startString = "${HourOfDay}   :   0${Minutes}"
                                            }
                                        } else {
                                            if(Minutes >= 10) {
                                                startString = "0${HourOfDay}   :   ${Minutes}"
                                            } else {
                                                startString = "0${HourOfDay}   :   0${Minutes}"
                                            }
                                        }
                                        start_result.setText(startString)
                                        start_result.text = start_result.text.toString().replace(" ", "")
                                        data.schedule_start = start_result.text.toString()
                                        start_result.setText(startString)
                                    },
                                    0,
                                    0,
                                    false
                                )

                                dialog.setTitle("시작 시간")
                                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                                dialog.show()
                            }

                            val day_night2 = bottomSheetView.findViewById<TextView>(R.id.end_day_night)
                            val end = bottomSheetView.findViewById<LinearLayout>(R.id.end_time_linear)
                            val end_result = bottomSheetView.findViewById<TextView>(R.id.end_time)
                            var endString = ""
                            end_result.text = data.schedule_end.replace(" ", "")

                            var end_min = end_result.text.substring(0,2)
                            var end_sec = end_result.text.substring(3,5)
                            day_night2.setText("오전") //스케줄 시작 오전/오후
                            end_result.setText("${end_min}   :   ${end_sec}") //스케줄 시작 시간

                            end.setOnClickListener {
                                val dialog1 = TimePickerDialog(
                                    requireContext(),
                                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                                    { view, HourOfDay, Minutes ->
                                        if (HourOfDay >= 12)
                                            day_night2.text = "오후"
                                        else
                                            day_night2.text = "오전"
                                        if (HourOfDay>= 10) {
                                            if(Minutes >= 10) {
                                                endString = "${HourOfDay}   :   ${Minutes}"
                                            } else {
                                                endString = "${HourOfDay}   :   0${Minutes}"
                                            }
                                        } else {
                                            if(Minutes >= 10) {
                                                endString = "0${HourOfDay}   :   ${Minutes}"
                                            } else {
                                                endString = "0${HourOfDay}   :   0${Minutes}"
                                            }
                                        }
                                        end_result.text = endString
                                        end_result.text = end_result.text.toString().replace(" ", "")
                                        data.schedule_start = end_result.text.toString()
                                        end_result.text = endString
                                    },
                                    0,
                                    0,
                                    false
                                )

                                dialog1.setTitle("종료 시간")
                                dialog1.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                                dialog1.show()
                            }

                            // 스케줄 색상
                            val color =  bottomSheetView.findViewById<Button>(R.id.schedule_color_view)
                            val drawable = ContextCompat.getDrawable(requireContext(), drawable.calendar_color)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                drawable!!.colorFilter = BlendModeColorFilter(Color.parseColor(data.schedule_color), BlendMode.SRC_ATOP)
                            } else {
                                drawable!!.setColorFilter(Color.parseColor(data.schedule_color), PorterDuff.Mode.SRC_ATOP)
                            }
                            color.background = drawable

                            color.setOnClickListener {
                                val color =  bottomSheetView.findViewById<Button>(R.id.schedule_color_view)
                                setupColorSheet(data, color)

                                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar_color)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    drawable!!.colorFilter = BlendModeColorFilter(Color.parseColor(data.schedule_color), BlendMode.SRC_ATOP)
                                } else {
                                    drawable!!.setColorFilter(Color.parseColor(data.schedule_color), PorterDuff.Mode.SRC_ATOP)
                                }
                                color.background = drawable
                            }

                            val alarmSpinner = bottomSheetView.findViewById<TextView>(R.id.alarm_spinner)
                            alarmSpinner.setText(data.schedule_alarm)
                            alarmSpinner.setOnClickListener{
                                var alarmArray = resources.getStringArray(array.times)
                                var alarmArrayList = ArrayList<String>()
                                for(i in alarmArray){
                                    alarmArrayList.add(i)
                                }
                                showBottomSheet(alarmArrayList, "alarm")
                            }

                            val repeatSpinner = bottomSheetView.findViewById<TextView>(R.id.repeat_spinner)
                            repeatSpinner.setText(data.schedule_repeat)
                            repeatSpinner.setOnClickListener{
                                var repeatArray = resources.getStringArray(array.repeat)
                                var repeatArrayList = ArrayList<String>()
                                for(i in repeatArray){
                                    repeatArrayList.add(i)
                                }
                                showBottomSheet(repeatArrayList, "repeat")
                            }

                            bottomSheetView.findViewById<EditText>(R.id.schedule_memo_textView).setText(data.schedule_memo) //스케줄 메모

                            val doneBtn = bottomSheetView.findViewById<ImageView>(R.id.doneBtn)
                            doneBtn.setOnClickListener {
                                hideKeyboard()
                                val schedule_title_2 = bottomSheetView.findViewById<EditText>(R.id.editText_scheduleName)
                                val schedule_memo_2 = bottomSheetView.findViewById<EditText>(R.id.schedule_memo_textView)
                                var start_result = bottomSheetView.findViewById<TextView>(R.id.start_time).text.toString()
                                var end_result = bottomSheetView.findViewById<TextView>(R.id.end_time).text.toString()
                                if(TextUtils.isEmpty(schedule_title_2.text.toString())) {
                                    Toast.makeText(requireContext(),
                                        "일정 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    data.schedule_name = schedule_title_2.text.toString()

//                                    start_result.text = start_result.text.toString().replace(" ", "")
//                                    end_result.text = end_result.text.toString().replace(" ", "")


                                    val year = day.toString().substring(12,16)
                                    var month = day.toString().substring(17,19)
                                    var date = ""
                                    if(month.substring(1,2) == "-"){
                                        month = "0${(day.toString().substring(17,18)).toInt() + 1}"
                                        date = day.toString().substring(19,21)

                                        if(date.substring(1,2) == "}")
                                            date = "0${day.toString().substring(19,20)}"
                                    }
                                    else{
                                        month = (month.toInt()+1).toString()
                                        date = day.toString().substring(20,22)

                                        if(date.substring(1,2) == "}")
                                            date = "0${day.toString().substring(20, 21)}"
                                    }

                                    Log.d("-=123", "${oldTitle}, ${data.schedule_name}, ${data.schedule_date} , ${data.schedule_start} , ${data.schedule_end}, ${data.schedule_color}, ${data.schedule_alarm}, ${data.schedule_memo}, ${data.schedule_isDone}")

                                    if(data.schedule_color == "#FFFFFF")
                                        data.schedule_color = "#BADFD2"
                                    else
                                        ColorSheetUtils.colorToHex(selectedColor)

                                    data.schedule_start = start_result.replace(" ", "")
                                    data.schedule_end = end_result.replace(" ", "")
                                    data.schedule_alarm = alarmSpinner.text.toString()
                                    data.schedule_repeat = repeatSpinner.text.toString()
                                    data.schedule_memo = schedule_memo_2.text.toString()
                                    CalendarRequest(data)

                                    viewModel.editItemList(data)

                                    Toast.makeText(
                                        requireContext(),
                                        String.format("스케줄 수정이 완료되었습니다."),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            bottomSheetDialog.show()

                        }

                    })

                    val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
                    itemTouchHelper.attachToRecyclerView(calendarRecyclerView)
                }


            }, { Log.d("login failed",
                "error......${context?.let { it1 -> error(it1) }}") },
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
    private var isSwipingCanceled = false
    var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    // Swiped to the right
                } else {
                    val position = viewHolder.adapterPosition
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#000000"))

                    //삭제하시겠습니까??
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("일정 삭제")
                        .setMessage("일정을 삭제하시겠습니까?")
                        .setPositiveButton("삭제",
                            DialogInterface.OnClickListener { dialog, id ->

                                val item = items.get(position)
                                items.removeAt(position)

                                //db에서 삭제.
                                deleteSchedule(item)

//                                adapter = CalendarRecyclerAdapter(items)
//                                calendarRecyclerView.adapter = adapter
                            })
                        .setNegativeButton("취소",
                            DialogInterface.OnClickListener { dialog, id ->
                                isSwipingCanceled = true
                                viewHolder.itemView.setBackgroundColor(0)

                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                            })

                    // 다이얼로그를 띄워주기
                    builder.show()

                }

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (!isSwipingCanceled) {
                    RecyclerViewSwipeDecorator.Builder(
                        c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(drawable.delete_schedule)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate()
                }
                if (isSwipingCanceled) {
                    RecyclerViewSwipeDecorator.Builder(
                        c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(0)
                        .setSwipeLeftLabelColor(Color.BLACK)
                        .create()
                        .decorate()

                    isSwipingCanceled = false
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            // 프래그먼트기 때문에 getActivity() 사용
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun deleteSchedule(item : CalendarItem){
        val deleteSchedule = "http://seonho.dothome.co.kr/deleteSchedule.php"

        val request = Upload_Request(
            Request.Method.POST,
            deleteSchedule,
            { response ->
                Log.d("CDCD", response.toString())
                if (!response.equals("schedule update fail")) {
                    Toast.makeText(requireContext(),
                        "일정 삭제가 완료되었습니다..", Toast.LENGTH_SHORT).show()

                    adapter = CalendarRecyclerAdapter(items)
                    calendarRecyclerView.adapter = adapter

                } else {

                }
            },
            { Log.d("failed", "error......${error(requireContext())}") },
            mutableMapOf(
                "id" to item.id,
                "schedule_name" to item.schedule_name,
                "schedule_date" to item.schedule_date,
                "schedule_start" to item.schedule_start
            )
        )

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(request)
    }

    @SuppressLint("SimpleDateFormat")
    private fun CalendarRequest(item : CalendarItem) {
        val id = arguments?.getString("id").toString()

        val updateScheduleUrl = "http://seonho.dothome.co.kr/updateCalendar.php"

        Log.d("-=123", "${id} , ${oldTitle}, ${oldStartTime},${item.schedule_name}, ${item.schedule_date} , ${item.schedule_start} , ${item.schedule_end}, ${item.schedule_color}, ${item.schedule_alarm}, ${item.schedule_memo}, ${item.schedule_isDone}")
        val request = Upload_Request(
            Request.Method.POST,
            updateScheduleUrl,
            { response ->
                Log.d("CDCD", response.toString())
                if (!response.equals("schedule update fail")) {


                } else {

                }
            },
            { Log.d("failed", "error......${error(requireContext())}") },
            mutableMapOf(
                "id" to id,
                "oldTitle" to oldTitle,
                "oldStartTime" to oldStartTime,
                "schedule_name" to item.schedule_name,
                "schedule_date" to item.schedule_date,
                "schedule_start" to item.schedule_start,
                "schedule_end" to item.schedule_end,
                "schedule_color" to item.schedule_color,
                "schedule_alarm" to item.schedule_alarm,
                "schedule_repeat" to item.schedule_repeat,
                "schedule_memo" to item.schedule_memo,
                "isDone" to item.schedule_isDone.toString()
            )
        )


        val queue = Volley.newRequestQueue(requireContext())
        queue.add(request)
    }

    private fun setupColorSheet(data : CalendarItem, colorView : Button) {
        val colors = resources.getIntArray(array.colors)
        ColorSheet().cornerRadius(8)
            //colorPicker 설정
            .colorPicker(
                colors = colors,
                selectedColor = selectedColor,
                listener = { color ->
                    selectedColor = color

                    data.schedule_color =  ColorSheetUtils.colorToHex(selectedColor)

                    val drawable = ContextCompat.getDrawable(requireContext(), drawable.calendar_color)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable!!.colorFilter =
                            BlendModeColorFilter(Color.parseColor(data.schedule_color), BlendMode.SRC_ATOP)
                    } else {
                        drawable!!.setColorFilter(
                            Color.parseColor(data.schedule_color),
                            PorterDuff.Mode.SRC_ATOP
                        )
                    }
                    colorView.background = drawable

                    setColor(selectedColor)
                })
            .show(parentFragmentManager)

    }

    private fun setColor(@ColorInt color: Int) {
        val color_picker = view?.findViewById<Button>(R.id.schedule_color_view)
        color_picker?.backgroundTintList = ColorStateList.valueOf(color)

    }

    private fun showFABMenu(newEventFAB : FloatingActionButton, newEventTextView :TextView , fixEventFAB : FloatingActionButton, fixEventTextView :TextView ) {
        isFABOpen = true

        newEventFAB.animate().translationY(-resources.getDimension(dimen.FABMoveTo_120))
        newEventTextView.animate().translationY(-resources.getDimension(dimen.FABMoveTo_120))
        fixEventFAB.animate().translationY(-resources.getDimension(dimen.FABMoveTo_60))
        fixEventTextView.animate().translationY(-resources.getDimension(dimen.FABMoveTo_60))

        newEventFAB.bringToFront()
        newEventTextView.bringToFront()
        fixEventFAB.bringToFront()
        fixEventTextView.bringToFront()

    }

    private fun closeFABMenu(newEventFAB : FloatingActionButton, newEventTextView :TextView , fixEventFAB : FloatingActionButton, fixEventTextView :TextView ) {
        isFABOpen = false
        newEventFAB.animate().translationY(0F)
        newEventTextView.animate().translationY(0F)
        fixEventFAB.animate().translationY(0F)
        fixEventTextView.animate().translationY(0F)
    }

    private fun showBottomSheet(items : ArrayList<String> , type : String){
        val bottomSheetView = layoutInflater.inflate(layout.normal_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        val alarmTextView = view?.findViewById<TextView>(R.id.alarm_spinner)
        val repeatTextView = view?.findViewById<TextView>(R.id.repeat_spinner)

        val cancelBtn = bottomSheetDialog.findViewById<TextView>(R.id.cancel)
        cancelBtn?.setOnClickListener{
            bottomSheetDialog.dismiss()
        }

        val selectBtn = bottomSheetDialog.findViewById<TextView>(R.id.select)

        selectBtn?.setOnClickListener{
            if(type == "alarm"){
                alarmTextView?.text = lastSelected
            }
            else{
                repeatTextView?.text = lastSelected
            }
            bottomSheetDialog.dismiss()
        }

        val dialogRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.dialog_recyclerView)
        val dialogAdapter = DialogRecyclerAdapter(items, lastSelected)
        dialogRecyclerView?.adapter = dialogAdapter


        dialogAdapter.setOnItemClickListener(
            object : DialogRecyclerAdapter.OnItemClickListener{
                override fun onItemClick(v: View, data: String) {
                    lastSelected = data

                }

            }
        )

        bottomSheetDialog.show()
    }

}



