package com.example.medi_nion

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class CalendarRecyclerAdapter(private val items: ArrayList<CalendarItem>) :
    RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEventClick(v:View, data: CalendarItem, pos:Int)
    }

    private var listener: OnItemClickListener? = null


    fun setOnItemClickListener(listener: CalendarRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CalendarRecyclerAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val calendarLinear = v.findViewById<LinearLayout>(R.id.linearLayout_calendar)
        val color = v.findViewById<ImageView>(R.id.color)
        val schedule_name = v.findViewById<TextView>(R.id.titleName)
        val schedule_time = v.findViewById<TextView>(R.id.time)
        val isDone = v.findViewById<CheckBox>(R.id.calendarCheckBox)
        fun bind(item: CalendarItem) {
            //뒤는 item class 변수명을 입력하면 된다,,,

            schedule_name.text = item.schedule_name

            val startTime = item.schedule_start.substring(0,5)
            val endTime = item.schedule_end.substring(0,5)
            schedule_time.text = "$startTime ~ $endTime"

            color.setColorFilter(Color.parseColor(item.color))

            isDone.isChecked = item.isDone

            calendarLinear.setOnClickListener{
//                listener?.onEventClick(itemView,item,pos)
            }

        }
    }
}


