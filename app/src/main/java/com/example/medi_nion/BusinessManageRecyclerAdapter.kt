package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessManageRecyclerAdapter(private val items: ArrayList<BusinessBoardItem>) :
    RecyclerView.Adapter<BusinessManageRecyclerAdapter.ViewHolder>() {

    private var listener : OnItemClickListener? = null
    interface OnItemClickListener{
        fun onUpdateClick(v:View, data: BusinessBoardItem, pos:Int)
        fun onDeleteClick(v:View, data: BusinessBoardItem, pos:Int)
    }

    fun setOnItemClickListener(listener: BusinessManageRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }
    companion object bitmap

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessManageRecyclerAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            bind(item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_item_manage, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v


        fun bind(item: BusinessBoardItem) {
             //뒤는 item class 변수명을 입력하면 된다,,,


            view.titleName.text = item.title
            view.deadline.text = item.time
            view.content.text = item.content


//            roundAll(view.profileImg2, 100.0f)

            var imgItems = ArrayList<BusinessPostImgItem>()
            var BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)


            if (item.image1 != "") {
                Log.d("imgtiem", item.image1)
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image1}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)

                imgItems.add(imgItem)
                view.BusinessBoardImgRecyclerView.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }
            if (item.image2 != "") {
                Log.d("imgtiem", item.image2)
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image2}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)
                imgItems.add(imgItem)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }
            if (item.image3 != "") {
                Log.d("imgtiem", item.image3)
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image3}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)
                imgItems.add(imgItem)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }

            BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
            view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter


//            view.scrap_btn.text = item.scrap.toString()
//            view.scrap_btn2.text = item.heart.toString()

            if(item.isWriter) {
                view.busi_moreBtn.visibility = View.VISIBLE

                view.busi_moreBtn.setOnClickListener {
                    if (view.busi_optionRadioGroup.visibility == View.GONE){
                        view.busi_optionRadioGroup.visibility = View.VISIBLE
                        view.busi_optionRadioGroup.bringToFront()
                    }
                    else view.busi_optionRadioGroup.visibility = View.GONE
                }

                val pos = absoluteAdapterPosition
                view.busi_postDelete_RadioBtn.setOnClickListener{
                    listener?.onDeleteClick(itemView,item,pos)
                }
                view.busi_postUpdate_RadioBtn.setOnClickListener{
                    listener?.onUpdateClick(itemView,item,pos)
                }
            }

        }

        fun roundAll(iv: ImageView, curveRadius: Float): ImageView {

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
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
                return bitmap
            } catch (e: Exception) {
                e.message
                return null
            }
        }
    }


}