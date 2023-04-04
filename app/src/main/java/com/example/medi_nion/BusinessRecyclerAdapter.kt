package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessRecyclerAdapter(private val items: ArrayList<BusinessBoardItem>) :
    RecyclerView.Adapter<BusinessRecyclerAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onProfileClick(v:View, data: BusinessBoardItem, pos : Int)
        fun onItemHeart(v:View, data: BusinessBoardItem, pos: Int)
        fun onItemBook(v:View, data: BusinessBoardItem, pos: Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessRecyclerAdapter.ViewHolder, position: Int) {
//        val item = items[position]
//        val listener = View.OnClickListener { it ->
//            Toast.makeText(it.context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
//        }
//        holder.apply {
//            bind(listener, item)
//            itemView.tag = item
//        }
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var profileImg = v.findViewById<ImageView>(R.id.profileImg2)

        private var bookmark = v.findViewById<CheckBox>(R.id.checkBox)
        private var heart = v.findViewById<CheckBox>(R.id.checkBox2)


        fun bind(item: BusinessBoardItem) {
             //뒤는 item class 변수명을 입력하면 된다,,,
            setViewMore(view.content, view.viewMore)

            view.titleName.text = item.channel_name
            view.time.text = item.time
//            view.profileImg2.setImageDrawable(item.profileImg)
            view.content.text = item.content
            bookmark.isChecked = item.isBookm
            heart.isChecked = item.isHeart


            if(item.image1 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image1)
                view.businessMG_Img.visibility
                view.businessMG_postImg1.visibility = View.VISIBLE
                view.businessMG_postImg1.setImageBitmap(bitmap)
            }
            if(item.image2 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image2)
                view.businessMG_Img.visibility
                view.businessMG_postImg2.visibility = View.VISIBLE
                view.businessMG_postImg2.setImageBitmap(bitmap)
            }
            if(item.image3 != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image3)
                view.businessMG_Img.visibility
                view.businessMG_postImg3.visibility = View.VISIBLE
                view.businessMG_postImg3.setImageBitmap(bitmap)
            }


            val pos = absoluteAdapterPosition
            profileImg.setOnClickListener {
                listener?.onProfileClick(itemView,item,pos)
            }

            if(pos!= RecyclerView.NO_POSITION) {
                bookmark.setOnClickListener {
                    listener?.onItemBook(itemView,item,pos)
                }
                heart.setOnClickListener{
                    listener?.onItemHeart(itemView,item,pos)
                }
            }


//            view.scrap_btn.text = item.scrap.toString()
//            view.scrap_btn2.text = item.heart.toString()

        }

        private fun setViewMore(contentTextView: TextView, viewMoreTextView: TextView){
            // getEllipsisCount()을 통한 더보기 표시 및 구현
            contentTextView.post{
                val lineCount = contentTextView.layout.lineCount
                if (lineCount > 0) {
                    if (contentTextView.layout.getEllipsisCount(lineCount - 1) > 0) {
                        // 더보기 표시
                        viewMoreTextView.visibility = View.VISIBLE

                        // 더보기 클릭 이벤트
                        viewMoreTextView.setOnClickListener {
                            if(viewMoreTextView.text == "더보기"){
                                contentTextView.maxLines = Int.MAX_VALUE
                                viewMoreTextView.text = "간략히 보기"
                            }
                            else{
                                contentTextView.maxLines = Int.MAX_VALUE
                                viewMoreTextView.text = "더보기"
                            }

                        }

                    }
                }
            }
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