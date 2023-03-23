package com.example.medi_nion

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class CommentListAdapter(private var itemList : ArrayList<CommentItem>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    var datas = mutableListOf<CommentItem>()

    interface OnItemClickListener {
        fun onItemClick(v: View, data: CommentItem, pos: Int)
        fun onItemHeart(v: View, data: CommentItem, pos: Int)
        fun onItemComment(v: View, data: CommentItem, pos: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: CommentListAdapter.OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.board_comment_item, parent, false)
        return ViewHolder(inflatedView);
    }

    override fun getItemCount(): Int =
        itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemComment: TextView = itemView.findViewById(R.id.comment_content)
        private val itemCommentTime: TextView = itemView.findViewById(R.id.comment_time)
        private val itemCommentNum: TextView = itemView.findViewById(R.id.comment_num)
        private val itemCommentHeart: CheckBox = itemView.findViewById(R.id.imageView_comment_like)
        private val itemCommentHeartCnt: TextView = itemView.findViewById(R.id.comment_heart_count)
        private val itemCommentComment: ImageView =
            itemView.findViewById(R.id.imageView_comment_comment)
        private val itemCommentDetail: RecyclerView =
            itemView.findViewById(R.id.CommentRecyclerView2)

        fun bind(item: CommentItem, pos: Int) {
            itemComment.text = item.comment
            itemCommentTime.text = item.comment_time
            itemCommentNum.text = item.writerNum.toString()
            itemCommentHeartCnt.text = item.commentHeart.toString()

            if (item.isHeart == true) itemCommentHeart.isChecked = true
            else itemCommentHeart.isChecked = false


            itemCommentDetail.adapter = item.commentDetailAdapter

//            Log.d("itiitt", itemView.toString())
//            itemCommentDetail.adapter = item.commentDetailAdapter[item.comment_num]

//            for(i in 0 until item.commentDetailAdapter.size){
//                Log.d("-=1-23123", item.commentDetailAdapter[i].comment2)
//                Log.d("-=1-23123", "${item.comment_num} // $i")
//            }

//            itemCommentDetail.apply{
//                adapter = CommentDetailListAdapter(item.commentDetailAdapter)
//                layoutManager = LinearLayoutManager(itemCommentDetail.context, LinearLayoutManager.HORIZONTAL, false)
//                setHasFixedSize(true)
//            }


                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                        Log.d("ItemClick1", "Event1")
                    }
                    itemComment.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }

                    itemCommentHeart.setOnClickListener {
                        listener?.onItemHeart(itemView, item, pos)
                    }

                    itemCommentComment.setOnClickListener {
                        listener?.onItemComment(itemView, item, pos)
                    }

                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setData(newData: ArrayList<CommentItem>) {
            itemList = newData
            notifyDataSetChanged()
        }
    }
