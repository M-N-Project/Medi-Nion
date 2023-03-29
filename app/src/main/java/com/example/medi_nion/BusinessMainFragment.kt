package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.business_home.*
import org.json.JSONArray
import org.json.JSONObject


class BusinessMainFragment : Fragment() { //bussiness 체널 보여주는 프레그먼트
    var items =ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    var scroll_count = 1
    var adapter = BusinessRecyclerAdapter(items)
    var scrollFlag = false
    var itemIndex = ArrayList<Int>()
    // RecyclerView.adapter에 지정할 Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.business_home, container, false)
    }

    var appUser = ""

    var num = 0 //비즈니스 채널 번호
    var writerId = ""
    var channel_name = ""
    var title = "" //비즈니스 채널명
    var content = "" //비즈니스 채널 내용
    var time = "" //비즈니스 채널 게시글 업로드 시간
    var image1 = "" //비즈니스 채널 사진 1
    var image2 = "" //비즈니스 채널 사진 2
    var image3 = "" //비즈니스 채널 사진 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items.clear()
        all_items.clear()

        fetchData()
    }

    fun fetchData() {
        // url to post our data
        var id = arguments?.getString("id")
        val urlBusiness = "http://seonho.dothome.co.kr/Business.php"
        val jsonArray : JSONArray

        val request = Board_Request(
            Request.Method.POST,
            urlBusiness,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                all_items.clear()
                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    num = item.getInt("num")
                    writerId = item.getString("id")
                    channel_name = item.getString("channel_name")
                    title = item.getString("title")
                    content = item.getString("content")
                    time = item.getString("time")
                    image1 = item.getString("image1")
                    image2 = item.getString("image2")
                    image3 = item.getString("image3")
                    val BusinessItem = BusinessBoardItem(writerId, channel_name, title, content, time, image1, image2, image3)

//                    if(i >= jsonArray.length() - item_count*scroll_count){
//                        items.add(BusinessItem)
//                        itemIndex.add(num) //앞에다가 추가.
//                    }
                    items.add(BusinessItem)
                    all_items.add(BusinessItem)
                }
                var recyclerViewState = BusinessBoardRecyclerView.layoutManager?.onSaveInstanceState()
                var new_items = ArrayList<BusinessBoardItem>()
                new_items.addAll(items)
                adapter = BusinessRecyclerAdapter(new_items)
                BusinessBoardRecyclerView.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                BusinessBoardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);

                adapter.setOnItemClickListener(object : BusinessRecyclerAdapter.OnItemClickListener {
                    override fun onProfileClick(v: View, data: BusinessBoardItem, pos: Int) {
                        val intent = Intent(context, BusinessProfileActivity::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("channel_name", data.channel_name)
                        startActivity(intent)
                    }
                })

            }, { Log.d("login failed", "error......${context?.let { it1 -> error(it1) }}") },
            hashMapOf(
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)

    }
}