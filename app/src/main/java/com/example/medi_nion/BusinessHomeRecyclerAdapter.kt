package com.example.medi_nion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.business_board_home_item.view.*
import kotlinx.android.synthetic.main.business_board_item.view.titleName

class BusinessHomeRecyclerAdapter(private val appUser : String, private val items: ArrayList<String>, private val itemInfo : HashMap<String, String>,  private val detailItems : HashMap<String, ArrayList<BusinessBoardItem>>) :
    RecyclerView.Adapter<BusinessHomeRecyclerAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onProfileClick(v:View, data: String, pos : Int)
    }
    private var listener : OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessHomeRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BusinessHomeRecyclerAdapter.ViewHolder, position: Int) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_home_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var titleName = view.findViewById<TextView>(R.id.titleName)
        private var businessHomeItemRecyclerView = view.findViewById<RecyclerView>(R.id.BusinessBoardHomeItemRecyclerView)
        private var businessDetailRecyclerView = businessHomeItemRecyclerView.findViewById<RecyclerView>(R.id.BusinessBoardDetailRecyclerView)
        private var profileImgView = view.findViewById<ImageView>(R.id.profileImg2)
//        private var bookmark = v.findViewById<CheckBox>(R.id.checkBox)
//        private var heart = v.findViewById<CheckBox>(R.id.checkBox2)

//        private val imgRecyclerView = v.findViewById<RecyclerView>(R.id.BusinessBoardImgRecyclerView)


        fun bind(item: String) {
            view.titleName.text = item

            val id = itemInfo[item]

            if(detailItems[item] == null){
                view.visibility = View.GONE
            }
            else{
                val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${id}BusinessProfile.jpg"
                val task = ImageLoadTask(imgUrl, profileImgView)
                task.execute()

            }

            roundAll(view.profileImg2, 100.0f)

            var detailItemArray = detailItems[item]
            if(detailItemArray!= null){
                var BusinessHomeItemAdapter = BusinessHomeItemRecyclerAdapter(detailItemArray, appUser)
                view.BusinessBoardHomeItemRecyclerView.adapter = BusinessHomeItemAdapter

            }


            val pos = absoluteAdapterPosition
            profileImgView.setOnClickListener {
                if(detailItems[item] != null) {
                    listener?.onProfileClick(itemView, item , pos)
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