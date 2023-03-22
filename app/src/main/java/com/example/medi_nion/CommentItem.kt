package com.example.medi_nion

import android.graphics.Bitmap
import android.widget.Adapter
import java.sql.Timestamp

class CommentItem(val writerId: String, val writerNum : Int, val comment: String, val comment_num: Int, val comment_time: String, val commentHeart : Int, val isHeart : Boolean?,  val commentDetailAdapter : CommentDetailListAdapter?) {

}