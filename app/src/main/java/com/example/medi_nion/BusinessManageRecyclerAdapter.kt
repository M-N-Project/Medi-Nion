package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessManageRecyclerAdapter(private val items: ArrayList<BusinessManageBoardItem>) :
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


        fun bind(listener: View.OnClickListener, item: BusinessManageBoardItem) {
             //뒤는 item class 변수명을 입력하면 된다,,,

            setViewMore(view.content, view.viewMore)

            view.titleName.text = item.channel_name
            view.time.text = item.time
            view.content.text = item.content


            view.profileImg2.setImageResource(R.drawable.logo)

            if(item.profileImg.length >= 5){
                if(item.profileImg.substring((item.profileImg).length-4, (item.profileImg).length) == ".jpg"){
                    val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.profileImg}"
                    val task = ImageLoadTask(imgUrl, view.profileImg2)
                    task.execute()
                }
            }

            roundAll(view.profileImg2, 100.0f)

//            view.profileImg2.setImageBitmap(item.profileImg)
//            roundAll(view.profileImg2, 100.0f)

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

        fun roundAll(iv: ImageView, curveRadius : Float)  : ImageView {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                iv.outlineProvider = object : ViewOutlineProvider() {

                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun getOutline(view: View?, outline: Outline?) {
                        outline?.setRoundRect(0, 0, view!!.width, view.height, curveRadius)
                    }
                }

                iv.clipToOutline = true
            }
            return iv
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