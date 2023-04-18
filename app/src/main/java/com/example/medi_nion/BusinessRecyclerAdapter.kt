package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.media.Image
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_board_item.view.*

class BusinessRecyclerAdapter(private val items: ArrayList<BusinessBoardItem>) :
    RecyclerView.Adapter<BusinessRecyclerAdapter.ViewHolder>() {

    var BusinessImgAdapterMap = HashMap<Int,BusinessPostImgRecyclerAdapter>()

    interface OnItemClickListener{
        fun onProfileClick(v:View, data: BusinessBoardItem, pos : Int)
        fun onItemHeart(v:View, data: BusinessBoardItem, pos: Int)
        fun onItemBook(v:View, data: BusinessBoardItem, pos: Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessRecyclerAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var profileImg = v.findViewById<ImageView>(R.id.profileImg2)

        private var bookmark = v.findViewById<CheckBox>(R.id.checkBox)
        private var heart = v.findViewById<CheckBox>(R.id.checkBox2)

        private val imgRecyclerView = v.findViewById<RecyclerView>(R.id.BusinessBoardImgRecyclerView)


        fun bind(item: BusinessBoardItem) {
             //뒤는 item class 변수명을 입력하면 된다,,,
            setViewMore(view.content, view.viewMore)

            view.titleName.text = item.channel_name
            view.time.text = item.time
            view.content.text = item.content
            bookmark.isChecked = item.isBookm
            heart.isChecked = item.isHeart

            profileImg.setImageResource(R.drawable.logo)
            view.findViewById<HorizontalScrollView>(R.id.businessMG_Img).visibility  = View.GONE

            if(item.profileImg.length >= 5){
                if(item.profileImg.substring((item.profileImg).length-4, (item.profileImg).length) == ".jpg"){
                    val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${item.profileImg}"
                    val task = ImageLoadTask(imgUrl, profileImg)
                    task.execute()
                }
            }

            roundAll(view.profileImg2, 100.0f)

            var imgItems = ArrayList<BusinessPostImgItem>()
            var BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
            BusinessImgAdapterMap[item.post_num] = BusinessImgAdapter

            if(item.image1 != ""){
                Log.d("imgtiem", item.image1)
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image1}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)

                imgItems.add(imgItem)
                view.BusinessBoardImgRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL,false)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }
            if(item.image2 != ""){
                Log.d("imgtiem", item.image2)
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image2}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)
                imgItems.add(imgItem)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }
            if(item.image3 != ""){
                val imgUrl = "http://seonho.dothome.co.kr/images/businessPost/${item.image3}"
                view.businessMG_Img.visibility = View.VISIBLE
                val imgItem = BusinessPostImgItem(item.post_num, item.id, imgUrl)
                imgItems.add(imgItem)

                BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
                view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter
            }

            BusinessImgAdapter = BusinessPostImgRecyclerAdapter(imgItems)
            view.BusinessBoardImgRecyclerView.adapter = BusinessImgAdapter


            val pos = absoluteAdapterPosition
            profileImg.setOnClickListener {
                listener?.onProfileClick(itemView,item,pos)
            }


            if(pos!= RecyclerView.NO_POSITION) {
                bookmark.setOnClickListener {
                    listener?.onItemBook(itemView,item,pos)
                }
                heart.setOnClickListener{
                    listener?.onItemHeart(itemView,item,pos)
                }
            }


        }

        private fun setViewMore(contentTextView: TextView, viewMoreTextView: TextView){
            // getEllipsisCount()을 통한 더보기 표시 및 구현
            contentTextView.post{
                val lineCount = contentTextView.layout.lineCount
                if (lineCount > 0) {
                    if (contentTextView.layout.getEllipsisCount(lineCount - 1) > 0) {
                        // 더보기 표시
                        viewMoreTextView.visibility = View.VISIBLE

                        // 더보기 클릭 이벤트
                        viewMoreTextView.setOnClickListener {
                            if(viewMoreTextView.text == "더보기"){
                                contentTextView.maxLines = Int.MAX_VALUE
                                viewMoreTextView.text = "간략히 보기"
                            }
                            else{
                                contentTextView.maxLines = Int.MIN_VALUE
                                viewMoreTextView.text = "더보기"
                            }

                        }

                    }
                }
            }
        }

        fun roundAll(iv: ImageView, curveRadius : Float)  : ImageView {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                iv.outlineProvider = object : ViewOutlineProvider() {

                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun getOutline(view: View?, outline: Outline?) {
                        outline?.setRoundRect(0, 0, view!!.width, view.height, curveRadius)
                    }
                }

                iv.clipToOutline = true
            }
            return iv
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


}