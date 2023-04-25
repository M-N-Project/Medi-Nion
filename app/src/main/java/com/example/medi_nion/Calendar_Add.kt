package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import org.json.JSONArray
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Calendar_Add : AppCompatActivity() {
    private var selectedColor: Int = ColorSheet.NO_COLOR
    private var NOTIFICATION_ID = "medinion"
    private var NOTIFICATION_NAME = "calendar alarm"
    var viewModel: CalendarViewModel = CalendarViewModel()
    private val alarmManager: AlarmManager? = null
    private val mCalender: GregorianCalendar? = null

    private val notificationManager: NotificationManager? = null
    var builder: NotificationCompat.Builder? = null

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_add)

        val id = intent?.getStringExtra("id")
        val date = intent?.getStringExtra("date")
        Log.d("ID", id.toString())

        val fragment = CalendarFragment()
//        viewModel = fragment.viewModel = // Set the ViewModel for the Fragment
        Log.d("iviosidf_add", CalendarFragment.viewModel.toString())

        val schedule_title = findViewById<EditText>(R.id.schedule_title)
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

        //채널 생성
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val IMPORTANCE: Int = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, IMPORTANCE)
            notificationManager.createNotificationChannel(channel)
        }

//        CalendarAlarmRequest()

        start.setOnClickListener {
            val dialog = TimePickerDialog(
                this,
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
                    start_result.text = startString
                },
                0,
                0,
                false
            )
            dialog.setTitle("시작 시간")
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }

        end.setOnClickListener {
            val dialog1 = TimePickerDialog(
                this,
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
                },
                0,
                0,
                false
            )
            dialog1.setTitle("종료 시간")
            dialog1.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog1.show()
        }

        color.setOnClickListener {
            setupColorSheet()
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

    private fun setupColorSheet() {
        val colors = resources.getIntArray(R.array.colors)
        ColorSheet().cornerRadius(8)
            //colorPicker 설정
            .colorPicker(
                colors = colors,
                selectedColor = selectedColor,
                listener = { color ->
                    selectedColor = color
                    setColor(selectedColor)

                })
            .show(supportFragmentManager)
        Log.d("018321",selectedColor.toString())

    }

    private fun setColor(@ColorInt color: Int) {
        val color_picker = findViewById<Button>(R.id.schedule_color_imageView)
        color_picker.backgroundTintList = ColorStateList.valueOf(color)

        Log.d("COLOE", color.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun CalendarRequest() {
        val receiverIntent: Intent = Intent(
            this@Calendar_Add,
            AlarmRecevier::class.java
        )
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@Calendar_Add, 0, receiverIntent, FLAG_MUTABLE)
        val id = intent?.getStringExtra("id").toString()
        var day = intent?.getStringExtra("day").toString()
        val postUrl = "http://seonho.dothome.co.kr/createCalendar.php"
        val schedule_title = findViewById<EditText>(R.id.schedule_title).text.toString()
        var start_result = findViewById<TextView>(R.id.start_result).text.toString()
        var end_result = findViewById<TextView>(R.id.end_result).text.toString()
        val spinner = findViewById<Spinner>(R.id.alarm_spinner)
        var alarm = spinner.selectedItem.toString()
        val schedule_memo = findViewById<EditText>(R.id.schedule_memo).text.toString()
        start_result = start_result.replace(" ", "")
        end_result = end_result.replace(" ", "")

//        date = date.substring(12 until 21)

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

        val presentDate = "$year-$month-$date"
        val alarm_setting = "$presentDate $end_result"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var datetime: Date? = null

        val calendar = Calendar.getInstance()
        try {
            datetime = dateFormat.parse(alarm_setting)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        calendar.time = datetime

        Log.d("datetititme", datetime.toString())

        alarmManager?.set(AlarmManager.RTC, calendar.timeInMillis,pendingIntent);

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

                    val item = CalendarItem(id, schedule_title ,presentDate,start_result,end_result,ColorSheetUtils.colorToHex(selectedColor),alarm,schedule_memo, false)
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
            mutableMapOf(
                "id" to id,
                "schedule_name" to schedule_title,
                "schedule_date" to presentDate,
                "schedule_start" to start_result,
                "schedule_end" to end_result,
                "schedule_color" to if(ColorSheetUtils.colorToHex(selectedColor)=="#FFFFFF") "#508BE0C4" else ColorSheetUtils.colorToHex(selectedColor),
                "schedule_alarm" to alarm,
                "schedule_memo" to schedule_memo,
                "isDone" to "0"
            )
        )
        request.retryPolicy = DefaultRetryPolicy(
            0,
            -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun CalendarAlarmRequest() {
        val url = "http://seonho.dothome.co.kr/schedule_alarm.php"
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var id = intent?.getStringExtra("id").toString()
        var day = intent?.getStringExtra("day").toString()
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

        val presentDate = "$year-$month-$date"

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                val jsonArray = JSONArray(response)

                for (i in jsonArray.length() - 1 downTo 0) {
                    val item = jsonArray.getJSONObject(i)
                    id = item.getString("id")
                    val schedule_name = item.getString("schedule_name")
                    val schedule_start = item.getString("schedule_start")
                    val schedule_end = item.getString("schedule_end")
                    val schedule_alarm = item.getString("schedule_alarm")
                    val schedule_date = item.getString("schedule_date")
                    Log.d("alarmamrmrm", "$schedule_name, $schedule_start, $schedule_end, $schedule_alarm, $schedule_date")

                    if(presentDate == schedule_date) {
                        val builder: NotificationCompat.Builder =
                            NotificationCompat.Builder(this, NOTIFICATION_ID)
                                .setContentTitle("[Medi_Nion] 캘린더 일정 알람") //타이틀 TEXT
                                .setContentText("인증할 수 없습니다. 인증을 다시 시도해주세요.\n프로필 메뉴 > 설정") //세부내용 TEXT
                                .setSmallIcon(R.drawable.logo) //필수 (안해주면 에러)
                        notificationManager.notify(0, builder.build())
                    }

                }
            }, { Log.d("login failed", "error......${error(applicationContext)}") },
                hashMapOf(
                "id" to id,
                "schedule_date" to presentDate
                )
            )
        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(request)
    }

}