package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import kotlinx.android.synthetic.main.calendar_add.*
import ru.ifr0z.timepickercompact.TimePickerCompact
import java.text.SimpleDateFormat
import java.util.*


class Calendar_History_Add : AppCompatActivity() {
    private var selectedColor: Int = ColorSheet.NO_COLOR
    private val random = (1..100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
    private val alarmCode = random.random()
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    lateinit var notificationPermission: ActivityResultLauncher<String>

    private var lastSelected = ""
//    var builder: NotificationCompat.Builder? = null

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val ALARM_REQUEST_CODE = 1000
        const val CHANNEL_ID = "medinion"
        const val CHANNEL_NAME = "schedule alarm"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_history_add)

        val id = intent?.getStringExtra("id")
        val date = intent?.getStringExtra("date")
        Log.d("ID", id.toString())

        val fragment = CalendarFragment()
//        viewModel = fragment.viewModel = // Set the ViewModel for the Fragment
        Log.d("iviosidf_add", CalendarFragment.viewModel.toString())

        val schedule_title = findViewById<EditText>(R.id.schedule_title)
//        val start_date = findViewById<TextView>(R.id.start_date_pick)
//        val end_date = findViewById<TextView>(R.id.end_date_pick)
        val start = findViewById<LinearLayout>(R.id.start_time_linear)
        val start_result = findViewById<TextView>(R.id.start_result)
        val end_result = findViewById<TextView>(R.id.end_result)
        val day_night1 = findViewById<TextView>(R.id.day_night1)
        val day_night2 = findViewById<TextView>(R.id.day_night2)
        val end = findViewById<LinearLayout>(R.id.end_time_linear)
        val schedule_btn = findViewById<Button>(R.id.schedule_btn)

        val color = findViewById<Button>(R.id.schedule_color_imageView)

        val calender = Calendar.getInstance()
        var startString = ""
        var endString = ""

        val notificationPermissionCheck = ContextCompat.checkSelfPermission(
            this@Calendar_History_Add,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        if (notificationPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10000
            )
        } else { //권한이 있는 경우
            Log.d("0-09123","notinoti")
        }

        notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("ontintno", "notinoti")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notification()
                }
            } else {
                Toast.makeText(baseContext, "권한을 승인해야 일정 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            Log.d("create", "Channel")
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

//        start_date.setOnClickListener {
//            val startDatePicker = DatePickerDialog(
//                this@Calendar_History_Add,
//                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
//                { _, year, monthOfYear, dayOfMonth ->
////                  월이 0부터 시작하여 1을 더해주어야함
//                    val month = monthOfYear + 1
////                   선택한 날짜의 요일을 구하기 위한 calendar
//                    val calendar = Calendar.getInstance()
////                    선택한 날짜 세팅
//                    calendar.set(year, monthOfYear, dayOfMonth)
//                    val date = calendar.time
//                    val week = calendar.get(Calendar.WEEK_OF_MONTH)
//                    val simpledateformat = SimpleDateFormat("EEEE", Locale.getDefault())
//                    var dayName: String = simpledateformat.format(date)
//                    when(dayName) {
//                        "일요일" -> dayName = "Sun"
//                        "월요일" -> dayName = "Mon"
//                        "화요일" -> dayName = "Tue"
//                        "수요일" -> dayName = "Wed"
//                        "목요일" -> dayName = "Thu"
//                        "금요일" -> dayName = "Fri"
//                        "토요일" -> dayName = "Sat"
//                    }
//                    if (month < 10) {
//                        if (dayOfMonth < 10) {
//                            start_date.text = "$year-0$month-0$dayOfMonth-$dayName-$week"
//                        } else {
//                            start_date.text = "$year-0$month-$dayOfMonth-$dayName-$week"
//                        }
//                    } else {
//                        if (dayOfMonth < 10) {
//                            start_date.text = "$year-$month-0$dayOfMonth-$dayName-$week"
//                        } else {
//                            start_date.text = "$year-$month-$dayOfMonth-$dayName-$week"
//                        }
//                    }
//                },
//                year,
//                month,
//                day
//            )
////           최소 날짜를 현재 시각 이후로
//            startDatePicker.datePicker.minDate = System.currentTimeMillis() - 1000;
//            startDatePicker.show()
//        }
//
//
//        end_date.setOnClickListener {
//            val endDatePicker = DatePickerDialog(
//                this@Calendar_History_Add,
//                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
//                { _, year, monthOfYear, dayOfMonth ->
////                  월이 0부터 시작하여 1을 더해주어야함
//                    val month = monthOfYear + 1
////                   선택한 날짜의 요일을 구하기 위한 calendar
//                    val calendar = Calendar.getInstance()
////                    선택한 날짜 세팅
//                    calendar.set(year, monthOfYear, dayOfMonth)
//                    val date = calendar.time
//                    val week = calendar.get(Calendar.WEEK_OF_MONTH)
//                    val simpledateformat = SimpleDateFormat("EEEE", Locale.getDefault())
//                    var dayName: String = simpledateformat.format(date)
//                    when(dayName) {
//                        "일요일" -> dayName = "Sun"
//                        "월요일" -> dayName = "Mon"
//                        "화요일" -> dayName = "Tue"
//                        "수요일" -> dayName = "Wed"
//                        "목요일" -> dayName = "Thu"
//                        "금요일" -> dayName = "Fri"
//                        "토요일" -> dayName = "Sat"
//                    }
//                    if (month < 10) {
//                        if (dayOfMonth < 10) {
//                            end_date.text = "$year-0$month-0$dayOfMonth-$dayName-$week"
//                        } else {
//                            end_date.text = "$year-0$month-$dayOfMonth-$dayName-$week"
//                        }
//                    } else {
//                        if (dayOfMonth < 10) {
//                            end_date.text = "$year-$month-0$dayOfMonth-$dayName-$week"
//                        } else {
//                            end_date.text = "$year-$month-$dayOfMonth-$dayName-$week"
//                        }
//                    }
//                },
//                year,
//                month,
//                day
//            )
////           최소 날짜를 현재 시각 이후로
//            endDatePicker.datePicker.minDate = System.currentTimeMillis() - 1000;
//            endDatePicker.show()
//        }
        start.setOnClickListener {
            showBottomSheet_eventAdd("start_time")
        }

        end.setOnClickListener {
            showBottomSheet_eventAdd("end_time")
        }

        // 스케줄 색상
        color.setOnClickListener {
            val color =  findViewById<Button>(R.id.schedule_color_imageView)
            setupColorSheet(color)

        }

        val selectAlarmLinearLayout = findViewById<LinearLayout>(R.id.alarm_linearLayout)
        selectAlarmLinearLayout.setOnClickListener{
            var alarmArray = resources.getStringArray(R.array.times)
            var alarmArrayList = ArrayList<String>()
            for(i in alarmArray){
                alarmArrayList.add(i)
            }
            showBottomSheet(alarmArrayList, "alarm")
        }

        val selectRepeatLinearLayout = findViewById<LinearLayout>(R.id.repeat_linearLayout)
        selectRepeatLinearLayout.setOnClickListener{
            var repeatArray = resources.getStringArray(R.array.repeat)
            var repeatArrayList = ArrayList<String>()
            for(i in repeatArray){
                repeatArrayList.add(i)
            }
            showBottomSheet(repeatArrayList, "repeat")
        }

        schedule_btn.setOnClickListener {
            if(TextUtils.isEmpty(schedule_title.text.toString())) {
                Toast.makeText(applicationContext,
                    "일정 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                CalendarRequest()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification() {
        notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun setupColorSheet(colorView : Button) {
        val colors = resources.getIntArray(R.array.colors)
        ColorSheet().cornerRadius(8)
            //colorPicker 설정
            .colorPicker(
                colors = colors,
                selectedColor = selectedColor,
                listener = { color ->
                    selectedColor = color
                    val selColor = ColorSheetUtils.colorToHex(selectedColor)

                    val drawable = ContextCompat.getDrawable(this, R.drawable.calendar_color_oval)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable!!.colorFilter = BlendModeColorFilter(Color.parseColor(selColor), BlendMode.SRC_ATOP)
                    } else {
                        drawable!!.setColorFilter(Color.parseColor(selColor), PorterDuff.Mode.SRC_ATOP)
                    }
                    colorView.background = drawable

                })
            .show(supportFragmentManager)

    }
    private fun setColor(@ColorInt color: Int) {
        val color_picker = findViewById<Button>(R.id.schedule_color_imageView)
        color_picker.backgroundTintList = ColorStateList.valueOf(color)

        Log.d("COLOE", color.toString())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SimpleDateFormat", "UnspecifiedImmutableFlag")
    private fun CalendarRequest() {
        val receiverIntent: Intent = Intent(
            this@Calendar_History_Add,
            AlarmReceiver::class.java
        )

        val id = intent?.getStringExtra("id").toString()
        var day = intent?.getStringExtra("day").toString()
        val postUrl = "http://seonho.dothome.co.kr/createCalendarHistory.php"
        val schedule_title = findViewById<EditText>(R.id.schedule_title).text.toString()
//        val start_date = findViewById<TextView>(R.id.start_date_pick).text.toString()
//        val end_date = findViewById<TextView>(R.id.end_date_pick).text.toString()
        var start_result = findViewById<TextView>(R.id.start_result).text.toString()
        var end_result = findViewById<TextView>(R.id.end_result).text.toString()
        val alarm_spinner = findViewById<TextView>(R.id.alarm_spinner)
        var alarm = alarm_spinner.text.toString()
        val repeat_spinner = findViewById<TextView>(R.id.repeat_spinner)
        var repeat = repeat_spinner.text.toString()
        val schedule_memo = findViewById<EditText>(R.id.schedule_memo_textView).text.toString()
        start_result = start_result.replace(" ", "")
        end_result = end_result.replace(" ", "")

//        date = date.substring(12 until 21)

        Log.d("89731231", day)
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

        Log.d("dsa", "$id , $year , $month, $date")

        val calendar = Calendar.getInstance()
        calendar.set(year.toInt(), month.toInt(), date.toInt())
        val dayOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

        Log.d("8712321", "${year.toInt()} // ${month.toInt()} // ${date.toInt()} //${dayOfMonth}")

//        val presentDate = "$year-$month-$date-$week-$dayOfMonth"
        val presentDate = "$year-$month-$date"

        var alarm_hour = start_result.substring(0, 2).toInt()
        var alarm_minute = start_result.substring(3, 5)

        if (alarm.equals("1시간 전")) {
            alarm_hour -= 1
        } else if (alarm.equals("2시간 전")) {
            alarm_hour -= 2
        } else if (alarm.equals("3시간 전")) {
            alarm_hour -= 3
        } else {
            alarm_hour -= 6
        }

        if (alarm_minute == "0")
            alarm_minute = "00"

        val alarm_setting = "$presentDate $alarm_hour:$alarm_minute"

        Log.d("DFDFS", "$alarm_hour, $alarm_minute")
        Log.d("alarm_setting", alarm_setting)

        if(!alarm.equals("설정 안함")) {
            setAlarm(alarm_setting, alarmCode, schedule_title)
        }

        val request = Upload_Request(
            Request.Method.POST,
            postUrl,
            { response ->
                Log.d("CDCD", response.toString())
                if (!response.equals("schedule fail")) {
                    Toast.makeText(
                        baseContext,
                        String.format("일정이 등록되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    val item = CalendarItem(id, schedule_title ,presentDate,
                        start_result ,end_result, ColorSheetUtils.colorToHex(selectedColor),alarm, repeat, schedule_memo, false)
                    CalendarFragment.viewModel.addItemLiveList(item)

                    this.finish()

                } else {
                    Toast.makeText(
                        applicationContext,
                        "게시물 업로드가 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { Log.d("failed", "error......${error(applicationContext)}") },
            if (ColorSheetUtils.colorToHex(selectedColor) == "#FFFFFF") {
                mutableMapOf(
                    "id" to id,
                    "schedule_name" to schedule_title,
                    "start_date" to presentDate,
                    "end_date" to "",
                    "schedule_start" to start_result,
                    "schedule_end" to end_result,
                    "schedule_color" to "#BADFD2",
                    "schedule_alarm" to alarm,
                    "schedule_repeat" to repeat,
                    "schedule_memo" to schedule_memo
                )
            } else {
                mutableMapOf(
                    "id" to id,
                    "schedule_name" to schedule_title,
                    "start_date" to presentDate,
                    "end_date" to "",
                    "schedule_start" to start_result,
                    "schedule_end" to end_result,
                    "schedule_color" to ColorSheetUtils.colorToHex(selectedColor),
                    "schedule_alarm" to alarm,
                    "schedule_repeat" to repeat,
                    "schedule_memo" to schedule_memo
                )
            }
        )
        request.retryPolicy = DefaultRetryPolicy(
            0,
            -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(time: String, alarm_code: Int, content: String){
        AlarmFunctions(applicationContext).callAlarm(time, alarmCode, content)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

    }

    private fun showBottomSheet(items : ArrayList<String> , type : String){
        val bottomSheetView = layoutInflater.inflate(R.layout.normal_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        val alarmTextView = findViewById<TextView>(R.id.alarm_spinner)
        val repeatTextView = findViewById<TextView>(R.id.repeat_spinner)

        val cancelBtn = bottomSheetDialog.findViewById<TextView>(R.id.cancel)
        cancelBtn?.setOnClickListener{
            bottomSheetDialog.dismiss()
        }

        val selectBtn = bottomSheetDialog.findViewById<TextView>(R.id.select)

        selectBtn?.setOnClickListener{
            if(type == "alarm"){
                alarmTextView.text = lastSelected
            }
            else{
                repeatTextView.text = lastSelected
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

    private fun showBottomSheet_eventAdd(type : String){
        val bottomSheetView = layoutInflater.inflate(R.layout.calendar_add_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        val timePicker = bottomSheetDialog?.findViewById<TimePickerCompact>(R.id.time_picker)
        val cancelBtn = bottomSheetDialog.findViewById<TextView>(R.id.cancel)
        cancelBtn?.setOnClickListener{
            bottomSheetDialog.dismiss()
        }
        val selectBtn = bottomSheetDialog?.findViewById<TextView>(R.id.select)

        selectBtn?.setOnClickListener{
            if(type=="start_time"){
                var start_hour=""
                var start_min=""
                var start_time=""

                if(timePicker?.hour!! < 10) start_hour =  "0${timePicker?.hour}"
                else start_hour =  "${timePicker?.hour}"
                if(timePicker?.minute!! < 10) start_min =  "0${timePicker?.minute}"
                else start_min =  "${timePicker?.minute}"

                start_time = "${start_hour}:${start_min}" //set/get hour and minute with support all API
                Log.d("timemmee","${start_hour}:${start_min}")

                val start_result = findViewById<TextView>(R.id.start_result)
                val startString = "${start_hour}   :   ${start_min}"
                start_result.setText(startString)

                day_night1.setText("오전")
                if(start_hour.toInt() >= 12) day_night1.setText("오후")
            }
            else{
                var end_hour=""
                var end_min=""
                var end_time=""

                if(timePicker?.hour!! < 10) end_hour =  "0${timePicker?.hour}"
                else end_hour =  "${timePicker?.hour}"
                if(timePicker?.minute!! < 10) end_min =  "0${timePicker?.minute}"
                else end_min =  "${timePicker?.minute}"

                end_time = "${end_hour}:${end_min}" //set/get hour and minute with support all API
                Log.d("timemmee","${end_hour}:${end_min}")

                val end_result = findViewById<TextView>(R.id.end_result)
                val endString = "${end_hour}   :   ${end_min}"
                end_result.setText(endString)

                day_night2.setText("오전")
                if(end_hour.toInt() >= 12) day_night2.setText("오후")
            }

            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.show()
    }
}