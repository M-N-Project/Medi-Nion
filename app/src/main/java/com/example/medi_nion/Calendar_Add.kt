package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import java.util.*


class Calendar_Add : AppCompatActivity() {
    private var selectedColor: Int = ColorSheet.NO_COLOR
    private val random = (1..100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
    private val alarmCode = random.random()
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

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
        setContentView(R.layout.calendar_add)

        val id = intent?.getStringExtra("id")
        val date = intent?.getStringExtra("date")
        Log.d("ID", id.toString())

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            Log.d("create", "Channel")
        }

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

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SimpleDateFormat", "UnspecifiedImmutableFlag")
    private fun CalendarRequest() {
        val receiverIntent: Intent = Intent(
            this@Calendar_Add,
            AlarmReceiver::class.java
        )
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@Calendar_Add, ALARM_REQUEST_CODE, receiverIntent, FLAG_MUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("presentDate", presentDate)
                    startActivity(intent)

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
                    "schedule_date" to presentDate,
                    "schedule_start" to start_result,
                    "schedule_end" to end_result,
                    "schedule_color" to "#BADFD2",
                    "schedule_alarm" to alarm,
                    "schedule_memo" to schedule_memo,
                    "isDone" to "0"
                )
            } else {
                mutableMapOf(
                    "id" to id,
                    "schedule_name" to schedule_title,
                    "schedule_date" to presentDate,
                    "schedule_start" to start_result,
                    "schedule_end" to end_result,
                    "schedule_color" to ColorSheetUtils.colorToHex(selectedColor),
                    "schedule_alarm" to alarm,
                    "schedule_memo" to schedule_memo,
                    "isDone" to "0"
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
}