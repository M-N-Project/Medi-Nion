package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class CommentListAdapter(private var itemList : ArrayList<CommentItem>) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    var datas = mutableListOf<CommentItem>()

    interface OnItemClickListener {
        fun onItemClick(v: View, data: CommentItem, pos: Int)
        fun onItemHeart(v: View, data: CommentItem, pos: Int)
        fun onItemComment(v: View, data: CommentItem, pos: Int)
        fun onItemDelete(v: View, data:CommentItem, pos:Int)
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
        private val itemCommentNum: TextView = itemView.findViewById(R.id.comment_num) //익명 1, 익명 2
        private val itemCommentHeart: CheckBox = itemView.findViewById(R.id.imageView_comment_like)
        private val itemCommentHeartCnt: TextView = itemView.findViewById(R.id.comment_heart_count)
        private val itemCommentMedal: ImageView = itemView.findViewById(R.id.comment_medal)
        private val itemCommentComment: ImageView =
            itemView.findViewById(R.id.imageView_comment_comment)
        private val itemCommentDetail: RecyclerView =
            itemView.findViewById(R.id.CommentRecyclerView2)
        private val itemCommentMore : Button = itemView.findViewById(R.id.commentMoreBtn)

        fun bind(item: CommentItem, pos: Int) {
            itemComment.text = item.comment
            itemCommentTime.text = item.comment_time
            itemCommentNum.text = item.writerNum.toString()
            itemCommentHeartCnt.text = item.commentHeart.toString()

            when (item.comment_medal) {
                "king" -> {
                    itemCommentMedal.setImageResource(R.drawable.grade_diamond2)
                }
                "gold" -> {
                    itemCommentMedal.setImageResource(R.drawable.grade_gold2)
                }
                "silver" -> {
                    itemCommentMedal.setImageResource(R.drawable.grade_silver2)
                }
                else -> {
                    itemCommentMedal.setImageResource(R.drawable.grade_bronze2)
                }
            }

            Log.d("-=1342", "${item.id} .. ${item.writerId}")
            if(item.id == item.writerId) {
                itemCommentMore.visibility = View.VISIBLE

                itemCommentMore.setOnClickListener{
//                    val comment_more = itemView.findViewById<RadioGroup>(R.id.optionRadioGroup)
//                    if(comment_more.visibility == 8) comment_more.visibility = View.VISIBLE
//                    else comment_more.visibility = View.GONE
//
//                    val comment_delete = itemView.findViewById<RadioButton>(R.id.commDelete_RadioBtn)

                    //댓글 삭제 이벤트
//                    comment_delete.setOnClickListener{
                        listener?.onItemDelete(itemView, item, pos)

//                    }
                }

            }
            else{
                itemCommentMore.visibility = View.INVISIBLE //디테일 안보이는 원인후보1,,,
            }

            if (item.isHeart == true) itemCommentHeart.isChecked = true
            else itemCommentHeart.isChecked = false

            itemCommentDetail.adapter = item.commentDetailAdapter

                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }
                    itemComment.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }

                    //
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
