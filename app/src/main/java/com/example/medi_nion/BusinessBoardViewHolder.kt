package com.example.medi_nion

import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessBoardViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    var view : View = v

    fun bind(item: BusinessBoardItem) {
         itemView.titleName.text = item.title
        itemView.time.text = item.time
        // 이미지 삽입은 Glide 라이브러리?
        //itemView.profileImg2 = item.profileImg
        itemView.content.text = item.content
        itemView.scrap_btn.text = item.scrap.toString()
        itemView.scrap_btn2.text = item.heart.toString()
    }

}