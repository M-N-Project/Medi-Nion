package com.example.medi_nion

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ViewPager2Adapter_Ad(var ad_Images: ArrayList<Int>) :
    RecyclerView.Adapter<ViewPager2Adapter_Ad.PagerViewHolder>() {

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.ad_image, parent, false)) {
        val ad_Image = itemView.findViewById<ImageView>(R.id.imageView_ad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = ad_Images.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.ad_Image.setImageResource(ad_Images[position])
    }
}