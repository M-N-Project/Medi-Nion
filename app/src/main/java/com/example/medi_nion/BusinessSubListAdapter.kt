package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medi_nion.databinding.BusinessSubListItemBinding

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

//            if(item.image != "null"){
//                val bitmap: Bitmap? = StringToBitmaps(item.image)
//                itemImg.setImageBitmap(bitmap)
//            }

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                channel_profile.setOnClickListener{
                    listener?.onItemProfile(itemView,item,pos)
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