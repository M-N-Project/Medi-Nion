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
            }

        }


    }


}