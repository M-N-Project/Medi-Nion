package com.example.medi_nion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley

class BusinessHomeItemRecyclerAdapter(private val items: ArrayList<BusinessBoardItem>, private val appUser : String) :
    RecyclerView.Adapter<BusinessHomeItemRecyclerAdapter.ViewHolder>() {
    private var flag = false

    interface OnItemClickListener {
        fun onItemClick(v: View, data: BusinessBoardItem, pos: Int, flag: Boolean)
    }

    private var listener: OnItemClickListener? = null


    fun setOnItemClickListener(listener: BusinessHomeItemRecyclerAdapter.OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: BusinessHomeItemRecyclerAdapter.ViewHolder,
        position: Int
    ) {
        val safePosition = holder.absoluteAdapterPosition
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.business_board_detail_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var title = v.findViewById<TextView>(R.id.business_home_title)
        private var time = v.findViewById<TextView>(R.id.business_home_time)
        private var linearLayout = v.findViewById<LinearLayout>(R.id.linearLayout12)

        private var businessDetailRecyclerView =
            v.findViewById<RecyclerView>(R.id.BusinessBoardDetailRecyclerView)


        fun bind(item: BusinessBoardItem) {

            title.text = item.title
            time.text = item.time


            val pos = absoluteAdapterPosition
            linearLayout.setOnClickListener {
                flag = !flag

                var tempArray = ArrayList<BusinessBoardItem>()
                if (flag == true) {
                    businessDetailRecyclerView.visibility = View.VISIBLE
                    tempArray.add(item)
                    var BusinessDetailAdapter = BusinessDetailRecyclerAdapter(tempArray)
                    businessDetailRecyclerView.adapter = BusinessDetailAdapter

                    BusinessDetailAdapter.setOnItemClickListener(
                        object :
                            BusinessDetailRecyclerAdapter.OnItemClickListener {
                            override fun onProfileClick(
                                v: View,
                                data: BusinessBoardItem,
                                pos: Int
                            ) {

                            }

                            override fun onItemHeart(
                                v: View,
                                data: BusinessBoardItem,
                                pos: Int
                            ) {
                                val heartrequest = Board_Request(
                                    Request.Method.POST,
                                    "http://seonho.dothome.co.kr/BusinessLike.php",
                                    { response ->
                                        if (response != "Heart fail") {
                                            data.isHeart = !data.isHeart
                                            Toast.makeText(
                                                v.context,
                                                "좋아요 완료",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        } else Log.d(
                                            "heartrequest fail",
                                            response
                                        )
                                    },
                                    {
                                        Log.d(
                                            "b-heart failed",
                                            "error......${
                                                v.context?.let { it1 ->
                                                    error(
                                                        it1
                                                    )
                                                }
                                            }"
                                        )
                                    },
                                    hashMapOf(
                                        "id" to appUser,
                                        "post_num" to item.post_num.toString(),
                                        "flag" to (!data.isHeart).toString()
                                    )
                                )


                                val queue = Volley.newRequestQueue(v.context)
                                queue.add(heartrequest)
                            }

                            override fun onItemBook(
                                v: View,
                                data: BusinessBoardItem,
                                pos: Int
                            ) {
                                val bookrequest = Board_Request(
                                    Request.Method.POST,
                                    "http://seonho.dothome.co.kr/BusinessBookmark.php",
                                    { response ->
                                        if (response != "Bookmark fail") {
                                            data.isBookm = !data.isBookm
                                            Toast.makeText(
                                                v.context,
                                                "북마크 완료",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        } else {
                                            Log.d("bookrequest fail", response)
                                        }
                                    },
                                    {
                                        Log.d(
                                            "b-bookmark failed",
                                            "error......${
                                                v.context?.let { it1 ->
                                                    error(
                                                        it1
                                                    )
                                                }
                                            }"
                                        )
                                    },
                                    hashMapOf(
                                        "id" to appUser,
                                        "post_num" to data.post_num.toString(),
                                        "flag" to (!data.isBookm).toString()
                                    )
                                )

                                val queue = Volley.newRequestQueue(v.context)
                                queue.add(bookrequest)

                            }
                        })
                } else {
                    businessDetailRecyclerView.visibility = View.GONE
                    tempArray.clear()
                    var BusinessDetailAdapter = BusinessDetailRecyclerAdapter(tempArray)
                    businessDetailRecyclerView.adapter = BusinessDetailAdapter
                }


            }


        }


    }
}

