package com.example.medi_nion

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class SearchListAdapter(private val itemList : ArrayList<BoardItem>) : RecyclerView.Adapter<SearchListAdapter.ViewHolder>()  {

    interface OnItemClickListener{
        fun onItemClick(v:View, data: BoardItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    private var filteredList = ArrayList<BoardItem>()

    init {
        filteredList
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String?) {
        val filteredList = ArrayList<BoardItem>()

        if (query == null || query.isEmpty()) {
            filteredList.addAll(itemList)
        } else {
            for (item in itemList) {
                val title = item.title.lowercase(Locale.getDefault())
                val content = item.contents.lowercase(Locale.getDefault())
                if (title.contains(query.lowercase(Locale.getDefault())) || content.contains(query.lowercase(Locale.getDefault()))) {
                    filteredList.add(item)
                }
            }
        }

        itemList.clear()
        itemList.addAll(filteredList)
        notifyDataSetChanged()
    }



    fun setOnItemClickListener(listener: SearchListAdapter.OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<BoardItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.board_home_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = itemList.size //라이브데이터 사용할때는 datas사용, 지금은 더미 데이터라서 매개변수로 넘긴 itemList로 대체


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(itemList[safePosition])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemTitle: TextView = itemView.findViewById(R.id.boardTitle)
        private val itemContents: TextView = itemView.findViewById(R.id.boardContent)
        private val itemTime : TextView = itemView.findViewById(R.id.textView_BoardTime)
        private val itemImg : ImageView = itemView.findViewById(R.id.imageView_image)
        private val itemHeart : TextView = itemView.findViewById(R.id.textView_likecount)
        private val itemComment : TextView = itemView.findViewById(R.id.textView_commentcount)
        private val itemBookmark : TextView = itemView.findViewById(R.id.textView_bookmarkcount)


        fun bind(item: BoardItem) {
            itemTitle.text = item.title
            itemContents.text = item.contents
            itemTime.text = item.time
            itemHeart.text = item.heart.toString()
            itemComment.text = item.comment.toString()
            itemBookmark.text = item.bookmark.toString()

            if(item.image != "null"){
                val bitmap: Bitmap? = StringToBitmaps(item.image)
                itemImg.setImageBitmap(bitmap)
            }

            val pos = absoluteAdapterPosition
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

    // String -> Bitmap 변환
    fun StringToBitmaps(image: String?): Bitmap? {
        try {
            val encodeByte = Base64.decode(image, Base64.DEFAULT)
            val bitmap : Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            return bitmap
        } catch (e: Exception) {
            e.message
            return null
        }
    }

}