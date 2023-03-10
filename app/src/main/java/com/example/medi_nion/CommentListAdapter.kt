package com.example.medi_nion

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentListAdapter(private var itemList : ArrayList<CommentItem>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>()  {

    var datas = mutableListOf<CommentItem>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: CommentItem, pos: Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : CommentListAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.board_comment_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemComment: TextView = itemView.findViewById(R.id.comment_content)
        private val itemCommentTime: TextView = itemView.findViewById(R.id.comment_time)
        private val itemCommentNum: TextView = itemView.findViewById(R.id.comment_num)

        fun bind(item: CommentItem) {
            itemComment.text = item.comment
            itemCommentTime.text = item.comment_time
            itemCommentNum.text = item.comment_num.toString()

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                    Log.d("ItemClick1", "Event1")
                }
                itemComment.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData:ArrayList<CommentItem>){
        itemList = newData
        notifyDataSetChanged()
    }
}