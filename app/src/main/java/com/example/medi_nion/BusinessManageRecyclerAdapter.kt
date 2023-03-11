package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessManageRecyclerAdapter(private val items: ArrayList<BusinessBoardItem>) :
    RecyclerView.Adapter<BusinessManageRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessManageRecyclerAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener { it ->
            Toast.makeText(it.context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_item, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v


        fun bind(listener: View.OnClickListener, item: BusinessBoardItem) {
             //뒤는 item class 변수명을 입력하면 된다,,,
            view.titleName.text = item.id
            view.time.text = item.time
//            view.profileImg2.setImageDrawable(item.profileImg)
            view.content.text = item.content

            if(item.image1 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image1)
                view.businessMG_postImg1.visibility = View.VISIBLE
                view.businessMG_postImg1.setImageBitmap(bitmap)
            }
            if(item.image2 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image2)
                view.businessMG_postImg2.visibility = View.VISIBLE
                view.businessMG_postImg2.setImageBitmap(bitmap)
            }
            if(item.image3 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image3)
                view.businessMG_postImg3.visibility = View.VISIBLE
                view.businessMG_postImg3.setImageBitmap(bitmap)
            }
//            view.scrap_btn.text = item.scrap.toString()
//            view.scrap_btn2.text = item.heart.toString()

        }
        // String -> Bitmap 변환
        fun StringToBitmaps(image: String?): Bitmap? {
            try {
                val encodeByte = Base64.decode(image, Base64.DEFAULT)
                val bitmap : Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
                return bitmap
            } catch (e: Exception) {
                e.message
                return null
            }
        }
    }


}