package com.example.medi_nion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusinessHotListAdapter(private val itemList: ArrayList<BusinessHotListItem>) :
    RecyclerView.Adapter<BusinessHotListAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onProfileClick(v:View, data: BusinessHotListItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: BusinessHotListAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusinessHotListAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_hot_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: BusinessHotListAdapter.ViewHolder, position: Int) {
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
            chanImg.setImageResource(R.drawable.basic_profile)

            val pos = absoluteAdapterPosition

            if(pos!= RecyclerView.NO_POSITION){
                chanView.setOnClickListener{ listener?.onProfileClick(itemView, item, pos)}
            }
        }
    }
}