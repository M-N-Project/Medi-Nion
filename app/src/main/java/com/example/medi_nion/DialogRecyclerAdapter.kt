package com.example.medi_nion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.internal.concurrent.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DialogRecyclerAdapter(private val itemList : ArrayList<String>, private var lastSelected : String) : RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder>() {

    private var selectCheck = ArrayList<Int>()

    init {
        for(i in itemList){
            selectCheck.add(0)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, data: String)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: DialogRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.normal_dialog_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val v : View = view
        private val layout = view.findViewById<LinearLayout>(R.id.itemLayout)
        private val radioItem = view.findViewById<RadioButton>(R.id.item)

        fun bind(item: String) {
            if(item == lastSelected) {
                selectCheck[absoluteAdapterPosition] = 1
                lastSelected = ""
            }

            if (selectCheck[absoluteAdapterPosition] == 0) {
                layout.setBackgroundColor(Color.WHITE)
            } else {
                layout.setBackgroundColor(Color.parseColor("#70DDDDDD"))
            }

            radioItem.text = item

            //select 여부를 확인하고 상태를 변경
            radioItem.isChecked = selectCheck[absoluteAdapterPosition] == 1

            radioItem.setOnClickListener {
                for (k in selectCheck.indices) {
                    if (k == absoluteAdapterPosition) {
                        selectCheck[k] = 1
                    } else {
                        selectCheck[k] = 0
                    }
                }
                notifyDataSetChanged()

                listener?.onItemClick(itemView,item)
            }

        }
    }
}