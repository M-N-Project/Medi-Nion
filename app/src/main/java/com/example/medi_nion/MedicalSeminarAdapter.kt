package com.example.medi_nion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.medical_seminar_item.view.*

class MedicalSeminarAdapter(val items: ArrayList<MedicalSeminar.MediSeminar>, context: Context) : RecyclerView.Adapter<MedicalSeminarAdapter.ViewHolder>() {

    // ViewHolder
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.medical_seminar_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.ms_title?.text = items[position].seminar_title // Title
        holder.view.ms_time.text = items[position].seminar_time_date // Time date
        holder.view.ms_loca.text = items[position].seminar_location // Location

        if(items[position].seminar_time_date.equals("")) {
            holder.view.ms_time.visibility = View.GONE
        }
        if(items[position].seminar_location.equals("")){
            holder.view.ms_loca.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val openUrl = Intent(Intent.ACTION_VIEW)
            openUrl.data = Uri.parse(items[position].saminar_siteUrl)
            startActivity(holder.view.context, openUrl, null)
        }
    }
}
