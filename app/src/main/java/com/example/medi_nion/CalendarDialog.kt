package com.example.medi_nion

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.schedule_add.*

class CalendarDialog : Dialog {
    var schedule_name : String = ""

    constructor(
        context: Context,
        schedule_name : String
    ) : super(context) {
        this.schedule_name = schedule_name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_dialog)

        val editText_scheduleName = findViewById<EditText>(R.id.editText_scheduleName)
        val start_day_night = findViewById<TextView>(R.id.start_day_night)
        val start_time = findViewById<TextView>(R.id.start_time)
        val end_day_night = findViewById<TextView>(R.id.start_day_night)
        val end_time = findViewById<TextView>(R.id.end_time)
        val schedule_color_view = findViewById<ImageView>(R.id.schedule_color_view)
        val schedule_alarm = findViewById<TextView>(R.id.schedule_alarm)
        val schedule_memo = findViewById<EditText>(R.id.schedule_memo)

        editText_scheduleName.setText(this.schedule_name)

    }
}