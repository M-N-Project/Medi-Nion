package com.example.medi_nion

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class CommentDetailListAdapter(private var itemList : ArrayList<CommentDetailItem>) : RecyclerView.Adapter<CommentDetailListAdapter.ViewHolder>()  {

    var datas = mutableListOf<CommentItem>()

    interface OnItemClickListener{
        fun onItemHeart(v:View, data: CommentDetailItem, pos: Int)
        fun onItemDelete(v: View, data: CommentDetailItem, pos : Int)
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
        holder.bind(itemList[position], position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemComment: TextView = itemView.findViewById(R.id.comment2_content)
        private val itemCommentTime: TextView = itemView.findViewById(R.id.comment2_time)
        private val itemCommentNum: TextView = itemView.findViewById(R.id.comment2_num)
        private val itemCommentHeart : CheckBox =  itemView.findViewById(R.id.imageView_comment2_like)
        private val itemCommentHeartCnt : TextView = itemView.findViewById(R.id.comment2_heartCnt)
        private val itemCommentMore : Button = itemView.findViewById(R.id.commentMoreBtn2)
        private val itemCommentMedal: ImageView = itemView.findViewById(R.id.comment_comment_medal)


        fun bind(item: CommentDetailItem, pos : Int) {
            itemComment.text = item.comment2
            itemCommentTime.text = item.comment2_time
            itemCommentNum.text = item.writerNum.toString()
            itemCommentHeartCnt.text = item.heart.toString()

            when (item.comment2_medal) {
                "king" -> {
                    itemCommentMedal.setImageResource(R.drawable.king_medal)
                }
                "gold" -> {
                    itemCommentMedal.setImageResource(R.drawable.gold_medal)
                }
                "silver" -> {
                    itemCommentMedal.setImageResource(R.drawable.silver_medal)
                }
                else -> {
                    itemCommentMedal.setImageResource(R.drawable.bronze_medal)
                }
            }

            if(item.id == item.writerId) {
                itemCommentMore.setOnClickListener{
                    itemCommentMore.visibility = View.VISIBLE

                    val comment_more = itemView.findViewById<RadioGroup>(R.id.optionRadioGroup2)
                    if(comment_more.visibility == 8) comment_more.visibility = View.VISIBLE
                    else comment_more.visibility = View.GONE

                    val comment_delete = itemView.findViewById<RadioButton>(R.id.commDelete_RadioBtn2)

                    //대댓글 삭제 이벤트
                    comment_delete.setOnClickListener{
                        listener?.onItemDelete(itemView, item, pos)
                    }
                }

            }
            else{
                itemCommentMore.visibility = View.INVISIBLE

            }

            if(item.isHeart == true) itemCommentHeart.isChecked = true
            else itemCommentHeart.isChecked = false

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemCommentHeart.setOnClickListener {
                    listener?.onItemHeart(itemView,item,pos)
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