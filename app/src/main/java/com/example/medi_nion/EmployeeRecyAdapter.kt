package com.example.medi_nion

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EmployeeRecyAdapter(private val itemList : ArrayList<EmployeeRecyItem>) : RecyclerView.Adapter<EmployeeRecyAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(v: View, data: EmployeeRecyItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: EmployeeRecyAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView = itemView.findViewById<TextView>(R.id.company_title)
        private val locaView = itemView.findViewById<TextView>(R.id.location)
        private val experienceView = itemView.findViewById<TextView>(R.id.experience)
        private val schoolView = itemView.findViewById<TextView>(R.id.school)
        private val companyView = itemView.findViewById<TextView>(R.id.company)
        private val deadlineView = itemView.findViewById<TextView>(R.id.deadline)
        //private val activeView = itemView.findViewById<ImageView>(R.id.)
        private val linear = itemView.findViewById<LinearLayout>(R.id.recycler_linear)

        fun bind(item: EmployeeRecyItem) {
            titleView.text = item.title
            locaView.text = item.loca.replace(" &gt; ", ">")
            experienceView.text = item.experience
            schoolView.text = item.school
            companyView.text = item.company
            val date = item.deadline.split("T")[0]
            deadlineView.text = "마감 일시 : "+date.split("-")[1]+"/"+date.split("-")[2]

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION) {
                linear.setOnClickListener{
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }

        fun Millis(postTime : Long) : String {
            // YY-MM-DD HH:MM:SS

            //val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd, hh:mm:ss")
            //val date = LocalDateTime.parse(dateString, formatter)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date1: String = simpleDateFormat.format(Date(postTime))
            return date1
        }
    }
}