package com.example.medi_nion

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentDetailListAdapter(private var itemList : ArrayList<CommentDetailItem>) : RecyclerView.Adapter<CommentDetailListAdapter.ViewHolder>()  {

    var datas = mutableListOf<CommentItem>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: CommentDetailItem, pos: Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : CommentDetailListAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.comment_comment_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemComment: TextView = itemView.findViewById(R.id.comment2_content)
        private val itemCommentTime: TextView = itemView.findViewById(R.id.comment2_time)
        private val itemCommentNum: TextView = itemView.findViewById(R.id.comment2_num)

        fun bind(item: CommentDetailItem) {
            itemComment.text = item.comment2
            itemCommentTime.text = item.comment2_time
            itemCommentNum.text = item.comment2_num.toString()

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
                itemComment.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData:ArrayList<CommentDetailItem>){
        itemList = newData
        notifyDataSetChanged()
    }
}