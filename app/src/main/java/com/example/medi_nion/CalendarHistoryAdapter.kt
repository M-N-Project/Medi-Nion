package com.example.medi_nion

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class CalendarHistoryAdapter(private val items: ArrayList<CalendarItem>) :
    RecyclerView.Adapter<CalendarHistoryAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onHistoryClick(v:View, data: CalendarItem, pos:Int)
        fun onHistoryLongClick(v: View, data: CalendarItem, pos: Int) : Boolean
        fun onItemDelete(v: View, data:CalendarItem, pos:Int)
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
        Log.d("haha??", "haha~~")
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val historyName = v.findViewById<TextView>(R.id.history_name)
        val linearLayout = v.findViewById<LinearLayout>(R.id.linearLayout_history)
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: CalendarItem, pos : Int) {
            val drawable = ContextCompat.getDrawable(this.itemView.context, R.drawable.history_button_round)
//            drawable?.setTint(Color.parseColor(item.color))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable!!.colorFilter = BlendModeColorFilter(Color.parseColor(item.schedule_color), BlendMode.SRC_ATOP)
            } else {
                drawable!!.setColorFilter(Color.parseColor(item.schedule_color), PorterDuff.Mode.SRC_ATOP)
            }
            linearLayout.background = drawable

            historyName.setText(item.schedule_name)

            linearLayout.setOnClickListener{
                listener?.onHistoryClick(itemView,item,pos)
            }

            linearLayout.setOnLongClickListener(View.OnLongClickListener {
                listener?.onHistoryLongClick(itemView,item,pos)
                Log.d("click", pos.toString() + " : Long click!")
                return@OnLongClickListener true
            })


        }
    }
}


