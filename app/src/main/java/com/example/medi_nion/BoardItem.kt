package com.example.medi_nion

import android.graphics.Bitmap
import java.sql.Timestamp

class BoardItem(val num: Int, val title: String, val contents: String, val time:String, val image :String) {

    var num:Int = 0

    fun setPostNum(num:Int) {this.num = num}
    fun getPostNum(): Int {return num}
}