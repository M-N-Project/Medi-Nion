package com.example.medi_nion

import android.graphics.Outline
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessSubListAdapter(private val itemList: ArrayList<BusinessHotListItem>) :
    RecyclerView.Adapter<BusinessSubListAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onProfileClick(v:View, data: BusinessHotListItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: BusinessSubListAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusinessSubListAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_hot_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: BusinessSubListAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var chanImg = itemView.findViewById<ImageView>(R.id.hot_chanImg)
        private var chanName = itemView.findViewById<TextView>(R.id.hot_chanName)
        private var chanView = itemView.findViewById<LinearLayout>(R.id.business_hot_layout)

        fun bind(item: BusinessHotListItem) {
            //chanImg.setImageDrawable(item.channel_Profile_Img)
            chanName.text = item.chanName

            chanImg.setImageResource(R.drawable.logo)

            if(item.channel_Profile_Img.length >= 5){
                if(item.channel_Profile_Img.substring((item.channel_Profile_Img).length-4, (item.channel_Profile_Img).length) == ".jpg"){
                    val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.channel_Profile_Img}"
                    val task = ImageLoadTask(imgUrl, chanImg)
                    task.execute()
                }
            }

            roundAll(chanImg, 100.0f)

//            if(item.channel_Profile_Img.get(item.chanName) == null){
//                chanImg.setImageResource(R.drawable.basic_profile)
//            }
//            else{
//                Log.d("channnn", item.chanName)
//                chanImg.setImageBitmap(item.channel_Profile_Img.get(item.chanName))
//                Log.d("90123", item.channel_Profile_Img.get(item.chanName).toString())
//                roundAll(chanImg, 100.0f)
//            }

            val pos = absoluteAdapterPosition

            if(pos!= RecyclerView.NO_POSITION){
                chanView.setOnClickListener{ listener?.onProfileClick(itemView, item, pos)}
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
}