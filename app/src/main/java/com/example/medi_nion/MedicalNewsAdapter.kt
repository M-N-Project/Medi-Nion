package com.example.medi_nion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medi_nion.MedicalNews
import kotlinx.android.synthetic.main.business_writing_img_item.view.*
import kotlinx.android.synthetic.main.medical_news_item.view.*

class MedicalNewsAdapter(val items: ArrayList<MedicalNews.MediInfo>, context: Context) : RecyclerView.Adapter<MedicalNewsAdapter.ViewHolder>() {

    // ViewHolder
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.medical_news_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.news_title?.text = items[position].title // Title
        holder.view.news_content.text = items[position].content // Content
        holder.view.news_time.text = items[position].time // Time

        Glide.with(holder.view.context)
            .load(items[position].image)
            .fitCenter()
            .into(holder.view.news_img) // Image thumbnail

        holder.itemView.setOnClickListener {
            val openUrl = Intent(Intent.ACTION_VIEW)
            openUrl.data = Uri.parse(items[position].siteUrl)
            startActivity(holder.view.context, openUrl, null)
        }
    }
}
