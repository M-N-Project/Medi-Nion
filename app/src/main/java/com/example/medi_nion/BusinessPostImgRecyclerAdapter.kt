package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.media.Image
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessPostImgRecyclerAdapter(private val items: ArrayList<BusinessPostImgItem>) :
    RecyclerView.Adapter<BusinessPostImgRecyclerAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onImgClick(v:View, data: BusinessPostImgItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessPostImgRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessPostImgRecyclerAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_image_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var postImg = view.findViewById<ImageView>(R.id.businessMG_postImg)


        fun bind(item: BusinessPostImgItem) {
            if(item.imageUrl != ""){
                val task = ImageLoadTask(item.imageUrl, postImg)
                task.execute()
            }

            val pos = absoluteAdapterPosition
            postImg.setOnClickListener {
                listener?.onImgClick(itemView,item,pos)

                //이미지 띄우기.
                Log.d("hi", item.imageUrl)
                val myCustomDialog = MyCustomDialog(view.context, item.imageUrl)
                myCustomDialog.show()
            }

        }


    }


}











//package com.example.medi_nion
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Outline
//import android.media.Image
//import android.os.Build
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewOutlineProvider
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.RecyclerView.Adapter
//import com.android.volley.Request
//import com.android.volley.toolbox.Volley
//import kotlinx.android.synthetic.main.business_board_item.view.*
//
//class BusinessPostImgRecyclerAdapter(private val items: ArrayList<BusinessPostImgItem>) :
//    RecyclerView.Adapter<BusinessPostImgRecyclerAdapter.ViewHolder>() {
//
//    interface OnItemClickListener{
//        fun onImgClick(v:View, data: BusinessPostImgItem, pos : Int)
//    }
//    private var listener : OnItemClickListener? = null
//
//
//    fun setOnItemClickListener(listener: BusinessPostImgRecyclerAdapter.OnItemClickListener) {
//        this.listener = listener
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: BusinessPostImgRecyclerAdapter.ViewHolder, position: Int) {
//        val safePosition = holder.absoluteAdapterPosition
//        val curveRadius = 10f // Set the desired corner radius value
//        holder.bind(items[position], curveRadius)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
//            ViewHolder {
//        val inflatedView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.business_image_item, parent, false)
//        return ViewHolder(inflatedView)
//    }
//
//    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//
//        private var view: View = v
//        private var postImg = view.findViewById<ImageView>(R.id.businessMG_postImg)
//        private var profileImg = view.findViewById<ImageView>(R.id.profileImg2)
//
//        fun bind(item: BusinessPostImgItem, curveRadius: Float) {
//            if (item.imageUrl != "") {
//                val task = ImageLoadTask(item.imageUrl, postImg)
//                task.execute()
//            }
//
//            if (item.profileImg.length >= 5) {
//                if (item.profileImg.substring(item.profileImg.length - 4, item.profileImg.length) == ".jpg") {
//                    val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.profileImg}"
//                    val task = ImageLoadTask(imgUrl, profileImg)
//                    task.execute()
//                    roundAll(profileImg, curveRadius)
//                }
//            }
//
//            val pos = absoluteAdapterPosition
//            postImg.setOnClickListener {
//                listener?.onImgClick(itemView, item, pos)
//
//                // 이미지 띄우기
//                Log.d("hi", item.imageUrl)
//                val myCustomDialog = MyCustomDialog(view.context, item.imageUrl)
//                myCustomDialog.show()
//            }
//        }
//    }
//
//
//
//    fun roundAll(iv: ImageView, curveRadius : Float)  : ImageView {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            iv.outlineProvider = object : ViewOutlineProvider() {
//
//                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//                override fun getOutline(view: View?, outline: Outline?) {
//                    outline?.setRoundRect(0, 0, view!!.width, view.height, curveRadius)
//                }
//            }
//
//            iv.clipToOutline = true
//        }
//        return iv
//    }
//
//
//}