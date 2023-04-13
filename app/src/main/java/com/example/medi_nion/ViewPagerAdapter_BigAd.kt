package com.example.medi_nion

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.medi_nion.R

class ViewPagerAdapter_BigAd(var big_ad_Images: ArrayList<Int>) :
    RecyclerView.Adapter<ViewPagerAdapter_BigAd.PagerViewHolder>() {

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.big_ad_image, parent, false)) {
        val big_ad_Image: ImageView = itemView.findViewById(R.id.imageView_big_ad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = big_ad_Images.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.big_ad_Image.setImageResource(big_ad_Images[position])
    }
}