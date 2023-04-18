package com.example.medi_nion

import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class HomeNewRecyclerItem(val chanName:String, val writerId:String, val title:String, val content:String)

class HomeNewRecyclerAdapter(private val newItem: ArrayList<HomeNewRecyclerItem>) : RecyclerView.Adapter<HomeNewRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val subsc_box = itemView.findViewById<View>(R.id.home_subsc_box)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView6)
        val nameView = itemView.findViewById<TextView>(R.id.home_business_chanName)
        val titleView = itemView.findViewById<TextView>(R.id.home_business_title)
        val detailView = itemView.findViewById<TextView>(R.id.home_business_detail)

        fun bind(item: HomeNewRecyclerItem) {
            titleView.text = item.title
            nameView.text = item.chanName
            detailView.text = item.content
            imageView.setImageResource(R.drawable.logo)

            val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.writerId}BusinessProfile.jpg"
            val task = ImageLoadTask(imgUrl, imageView)
            task.execute()
            roundAll(imageView, 100.0f)

            val pos = absoluteAdapterPosition
            if(pos != RecyclerView.NO_POSITION) {
                subsc_box.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: HomeNewRecyclerItem, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: HomeNewRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.home_busi_new_detail,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(newItem[safePosition])
    }

    override fun getItemCount(): Int = newItem.size

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
}