package com.example.medi_nion

import android.graphics.Bitmap
import android.widget.Adapter
import java.sql.Timestamp

class CommentItem(val id: String, val comment: String, val comment_num: Int, val comment_time: String, val commentDetailAdapterMap : HashMap<Int,CommentDetailListAdapter>) {

}