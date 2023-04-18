package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class BusinessBoardItem(
    val post_num : Int,
    val id : String,
    val profileImg : String,
    val channel_name : String,
    val title: String,
    val content : String,
    val time: String,
    val image1 : String,
    val image2 : String,
    val image3 : String,
    var isHeart : Boolean,
    var isBookm : Boolean,
//    val profileImg: Drawable,
//    val content: String,
//    val scrap: Int,
//    val heart: Int
    var isWriter : Boolean
    ) {

}
