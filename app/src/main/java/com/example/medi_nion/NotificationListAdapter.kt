package com.example.medi_nion

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationListAdapter(private val itemList : ArrayList<NotificationItem>)
    : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>()  {

    interface OnItemClickListener{
        fun onItemClick(v:View, data: NotificationItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: NotificationListAdapter.OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<NotificationItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemTitle: TextView = itemView.findViewById(R.id.notification_title)
        private val itemContents: TextView = itemView.findViewById(R.id.notification_content)
        private val itemTime : TextView = itemView.findViewById(R.id.notification_time)

        fun bind(item: NotificationItem) {
            itemTitle.text = item.title
            itemContents.text = item.content
            itemTime.text = item.time

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
                itemTitle.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
                itemContents.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
    }
}