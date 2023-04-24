package com.example.medi_nion

import android.annotation.SuppressLint
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
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.calendar_item.view.*


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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CalendarRecyclerAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position], position)
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
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: CalendarItem, pos : Int) {
            //뒤는 item class 변수명을 입력하면 된다,,,

            schedule_name.text = item.schedule_name

            val startTime = item.schedule_start.substring(0,5)
            val endTime = item.schedule_end.substring(0,5)
            schedule_time.text = "$startTime ~ $endTime"

            val drawable = ContextCompat.getDrawable(this.itemView.context, R.drawable.calendar_color_oval)
//            drawable?.setTint(Color.parseColor(item.color))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable!!.colorFilter = BlendModeColorFilter(Color.parseColor(item.schedule_color), BlendMode.SRC_ATOP)
            } else {
                drawable!!.setColorFilter(Color.parseColor(item.schedule_color), PorterDuff.Mode.SRC_ATOP)
            }
            color.background = drawable

            Log.d("91027312", "${isDone.isChecked}// ${item.schedule_isDone}")
            isDone.isChecked = item.schedule_isDone

            isDone.setOnCheckedChangeListener{ _ , isChecked ->
                val updateScheduleDoneUrl = "http://seonho.dothome.co.kr/updateCalendarDone.php"

                val request = Upload_Request(
                    Request.Method.POST,
                    updateScheduleDoneUrl,
                    { response ->
                        Log.d("CDCD", response.toString())
                        if (!response.equals("schedule update fail")) {


                        } else {

                        }
                    },
                    { Log.d("failed", "error......${error(this.itemView.context)}") },
                    mutableMapOf(
                        "id" to item.id,
                        "schedule_name" to item.schedule_name,
                        "schedule_date" to item.schedule_date,
                        "schedule_start" to item.schedule_start,
                        "isDone" to isDone.isChecked.toString()
                    )
                )


                val queue = Volley.newRequestQueue(this.itemView.context)
                queue.add(request)
            }


            calendarLinear.setOnClickListener{
                listener?.onEventClick(itemView,item,pos)
            }

        }
    }
}


