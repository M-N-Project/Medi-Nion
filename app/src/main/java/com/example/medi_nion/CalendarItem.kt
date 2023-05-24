package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class CalendarItem(
    val id : String,
    var schedule_name : String,
    var start_date : String,
//    var end_date: String,
    var schedule_start : String,
    var schedule_end : String,
    var schedule_color : String,
    var schedule_alarm : String,
    var schedule_repeat : String,
    var schedule_memo : String,
    var schedule_isDone : Boolean
    ) {

}
