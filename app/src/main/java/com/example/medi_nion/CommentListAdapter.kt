package com.example.medi_nion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class CommentListAdapter(private val itemList : ArrayList<CommentItem>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>()  {

    var datas = mutableListOf<CommentItem>()

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

        fun bind(item: CommentItem) {
            itemComment.text = item.comment
            itemCommentTime.text = item.comment_time

        }
    }
}