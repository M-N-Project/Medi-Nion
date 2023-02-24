package com.example.medi_nion

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

class BoardListAdapter(private val itemList : ArrayList<BoardItem>) : RecyclerView.Adapter<BoardListAdapter.ViewHolder>()  {

    var datas = mutableListOf<BoardItem>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: BoardItem, pos : Int)
    }
    private var listener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        Log.d("---", "---")
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
        private val itemTime : TextView = itemView.findViewById(R.id.textView_BoardTime)
        private val itemImg : ImageView = itemView.findViewById(R.id.imageView_image)

        fun bind(item: BoardItem) {
            itemTitle.text = item.title
            itemContents.text = item.contents
            itemTime.text = item.time

            val bitmap: Bitmap? = stringToBitmaps(item.image)
            itemImg.setImageBitmap(bitmap)

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

    // String -> Bitmap 변환
    fun stringToBitmaps(image: String?): Bitmap {
        val encodeByte: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }

}