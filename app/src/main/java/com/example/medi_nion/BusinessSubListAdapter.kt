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
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.medi_nion.databinding.BusinessSubListItemBinding
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessSubListAdapter(private val itemList : ArrayList<BusinessChanListItem>) : RecyclerView.Adapter<BusinessSubListAdapter.ViewHolder>()  {

    interface OnItemClickListener{
        fun onItemProfile(v:View, data: BusinessChanListItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessSubListAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.business_sub_list_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val channal_name: TextView = itemView.findViewById(R.id.channel_name)
        private val channel_desc: TextView = itemView.findViewById(R.id.channel_desc)
        private val channel_profile : ImageView = itemView.findViewById(R.id.channel_profile_img)


        fun bind(item: BusinessChanListItem) {
            channal_name.text = item.chan_name
            channel_desc.text = item.chan_desc

            channel_profile.setImageResource(R.drawable.logo)

            if(item.profileImg.length >= 5){
                if(item.profileImg.substring((item.profileImg).length-4, (item.profileImg).length) == ".jpg"){
                    val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.profileImg}"
                    val task = ImageLoadTask(imgUrl, channel_profile)
                    task.execute()
                }
            }

            roundAll(channel_profile, 100.0f)

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                channel_profile.setOnClickListener{
                    listener?.onItemProfile(itemView,item,pos)
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