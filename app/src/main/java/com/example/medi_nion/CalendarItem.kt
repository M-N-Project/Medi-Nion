package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class CalendarItem(
    val id : String,
    val schedule_name : String,
    val schedule_start : String,
    val schedule_end : String,
    val color : String,
    val alarm : String,
    val memo : String,
    val isDone : Boolean
    ) {

}
