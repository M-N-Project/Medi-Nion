package com.example.medi_nion

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView


class CalendarHistoryAdapter(private val items: ArrayList<CalendarItem>) :
    RecyclerView.Adapter<CalendarHistoryAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onHistoryClick(v:View, data: CalendarItem, pos:Int)
    }

    private var listener: OnItemClickListener? = null


    fun setOnItemClickListener(listener: CalendarHistoryAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CalendarHistoryAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_history_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val historyName = v.findViewById<CheckBox>(R.id.history_name)
        val linearLayout = v.findViewById<LinearLayout>(R.id.linearLayout_history)
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: CalendarItem, pos : Int) {

            historyName.setText(item.schedule_name)

            linearLayout.setOnClickListener{
                listener?.onHistoryClick(itemView,item,pos)
            }

        }
    }
}


