//package com.example.medi_nion
//
//import android.content.Context
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.example.medi_nion.databinding.BoardHomeItemBinding
//
//class BoardListAdapter(private val context: Context, private val itemList : List<BoardItem>) :RecyclerView.Adapter<BoardListAdapter.BoardViewHolder>()  {
//    private val TAG = this.javaClass.simpleName
//    private lateinit var binding: BoardHomeItemBinding
//    private var onItemClickListener: OnItemClickListener? = null
//    private var selectedPosition = 0
//
//    interface OnItemClickListener {
//        fun onItemClick(item: BoardItem, position: Int)
//    }
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.onItemClickListener = listener
//    }
//
//    inner class BoardViewHolder(
//        private val binding: BoardHomeItemBinding
//    ): RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: BoardItem) {
//            //binding.model = item
//
//            if (onItemClickListener != null) {
//                binding.boardTitle.setOnClickListener {
//                    onItemClickListener?.onItemClick(item, selectedPosition)
//                    if (selectedPosition != selectedPosition) {
//                        binding.setChecked()
//                        notifyItemChanged(selectedPosition)
//                        selectedPosition = selectedPosition
//                    }
//                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
//        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.board_home_item, parent, false)
//        return BoardViewHolder(binding);
//    }
//
//    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
//        val item = itemList[position]
//        holder.apply {
//            bind(item)
//        }
//    }
//
//    private fun BoardHomeItemBinding.setChecked() = boardTitle.setTextColor(Color.parseColor("#FFFFFF"));
//
//    private fun BoardHomeItemBinding.setUnchecked() = boardTitle.setTextColor(Color.parseColor("#000000"))
//
//
//
//}

package com.example.medi_nion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoardListAdapter(private val context: Context, private val itemList : List<BoardItem>) : RecyclerView.Adapter<BoardListAdapter.ViewHolder>()  {

//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
//        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.board_home_item, parent, false)
//        return BoardViewHolder(inflatedView);
//    }
//
//    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
//        val item = itemList[position]
//        holder.apply {
//            bind(item)
//        }
//    }

    var datas = mutableListOf<BoardItem>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: BoardItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.board_home_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemTitle: TextView = itemView.findViewById(R.id.boardTitle)
        private val itemContents: TextView = itemView.findViewById(R.id.boardContent)

        fun bind(item: BoardItem) {
            itemTitle.text = item.title
            itemContents.text = item.contents

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
                itemTitle.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
                itemContents.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }


        }
    }




}