package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_home.*
import kotlinx.android.synthetic.main.business_hot.*
import org.json.JSONArray


class BusinessMainFragment : Fragment() { //bussiness 체널 보여주는 프레그먼트
    var items = ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    var adapter = BusinessRecyclerAdapter(items)

    private var hotListItems = ArrayList<BusinessHotListItem>()


    val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    var scroll_count = 1


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
    var isHeart = false // 좋아요 정보
    var isBookmark = false // 북마크 정보
    var isSub = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items.clear()
        all_items.clear()

        fetchHotProfile()
        fetchData()
    }

    ////////////////// 인기 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
    fun fetchHotProfile() {
        var appUser = arguments?.getString("id").toString()
        val urlHotProfile = "http://seonho.dothome.co.kr/Business_profileHot_list.php"
        val request = Board_Request(
            Request.Method.POST,
            urlHotProfile,
            { response ->
                if (response != "no BusinessProfile"){
                    hotListItems.clear()
                    val jsonArray = JSONArray(response)

                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)

                        val chanName = item.getString("channel_name")
                        val chanProfile = item.getString("channel_profile_img")

                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
                        hotListItems.add(HotListItem)
                    }

                    hotListItems.reverse()
                    var hotAdapter = BusinessHotListAdapter(hotListItems)
                    hotAdapter.notifyDataSetChanged()
                    BusinessSubRecycler.adapter = hotAdapter

                    hotAdapter.setOnItemClickListener(object :
                        BusinessHotListAdapter.OnItemClickListener {
                        override fun onProfileClick(
                            v: View,
                            data: BusinessHotListItem,
                            pos: Int
                        ){
                            val intent =
                                Intent(
                                    context,
                                    BusinessProfileActivity::class.java
                                )
                            intent.putExtra("appUser", appUser)
                            intent.putExtra(
                                "channel_name",
                                data.chanName
                            )
                            startActivity(intent)
                        }
                    })
                }
                else {
                    Toast.makeText(context, "프로필가져오기 실패", Toast.LENGTH_SHORT)
                }
            },
            {
                Log.d(
                    "login failed",
                    "error......${context?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf()
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    ////////////////// 구독 중인 채널의 게시물 가져오는 fetch 함수 + 좋아요&북마크 //////////////////////////////////////////////////////
    fun fetchData() {
        // url to post our data
        var appUser = arguments?.getString("id").toString()
        val urlBoard = "http://seonho.dothome.co.kr/BusinessBoardSub_list.php"
        val urlBookmark = "http://seonho.dothome.co.kr/BusinessBookmark_list.php"
        val urlLike = "http://seonho.dothome.co.kr/BusinessLike_list.php"
        val jsonArray: JSONArray
        val urlIsSub = "http://seonho.dothome.co.kr/ChannelSubList.php"
//        val urlBoard = "http://seonho.dothome.co.kr/Business.php"

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                if (response != "business board list fail") {
                    if (response != "business board no Item") {
                        val jsonArray = JSONArray(response)
                        items.clear()
                        all_items.clear()

                        for (i in jsonArray.length() - 1 downTo 0) {
                            val item = jsonArray.getJSONObject(i)

                            val num = item.getInt("num")
                            val writerId = item.getString("id")
                            val channel_name = item.getString("channel_name")
                            val title = item.getString("title")
                            val content = item.getString("content")
                            val time = item.getString("time")
                            val image1 = item.getString("image1")
                            val image2 = item.getString("image2")
                            val image3 = item.getString("image3")

                            val bookfetchrequest = Login_Request(
                                Request.Method.POST,
                                urlBookmark,
                                { responseBookmark ->
                                    if (responseBookmark.equals("Success Bookmark")) {
                                        isBookmark = true
                                    } else if (responseBookmark.equals("No Bookmark")) {
                                        isBookmark = false
                                    }
                                    val likerequest = Login_Request(
                                        Request.Method.POST,
                                        urlLike,
                                        { responseLike ->
                                            if (responseLike.equals("Success Heart")) {
                                                isHeart = true
                                            } else if (responseLike.equals("No Heart")) {
                                                isHeart = false
                                            }

                                            val BusinessItem = BusinessBoardItem(
                                                num,
                                                writerId,
                                                channel_name,
                                                title,
                                                content,
                                                time,
                                                image1,
                                                image2,
                                                image3,
                                                isHeart,
                                                isBookmark
                                            )
                                            items.add(BusinessItem)
                                            all_items.add(BusinessItem)

                                            var recyclerViewState =
                                                BusinessBoardRecyclerView.layoutManager?.onSaveInstanceState()
                                            var new_items = ArrayList<BusinessBoardItem>()
                                            new_items.addAll(items)
                                            adapter = BusinessRecyclerAdapter(new_items)
                                            BusinessBoardRecyclerView.adapter = adapter
                                            adapter.stateRestorationPolicy =
                                                RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                                            BusinessBoardRecyclerView.layoutManager?.onRestoreInstanceState(
                                                recyclerViewState
                                            );

                                            adapter.setOnItemClickListener(object :
                                                BusinessRecyclerAdapter.OnItemClickListener {
                                                override fun onProfileClick(
                                                    v: View,
                                                    data: BusinessBoardItem,
                                                    pos: Int
                                                ) {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            BusinessProfileActivity::class.java
                                                        )
                                                    intent.putExtra("appUser", appUser)
                                                    intent.putExtra(
                                                        "channel_name",
                                                        data.channel_name
                                                    )
                                                    startActivity(intent)
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
                                                                    context,
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
                                                                    context?.let { it1 ->
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
                                                            "flag" to (!data.isHeart).toString()
                                                        )
                                                    )

                                                    val queue = Volley.newRequestQueue(context)
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
                                                                    context,
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
                                                                    context?.let { it1 ->
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

                                                    val queue = Volley.newRequestQueue(context)
                                                    queue.add(bookrequest)

                                                }
                                            })
                                        },
                                        {
                                            Log.d(
                                                "login failed",
                                                "error......${context?.let { it1 -> error(it1) }}"
                                            )
                                        },
                                        hashMapOf(
                                            "id" to appUser,
                                            "post_num" to num.toString()
                                        )
                                    )
                                    val queue = Volley.newRequestQueue(context)
                                    queue.add(likerequest)
                                },
                                {
                                    Log.d(
                                        "login failed",
                                        "error......${context?.let { it1 -> error(it1) }}"
                                    )
                                },
                                hashMapOf(
                                    "id" to appUser,
                                    "post_num" to num.toString()
                                )
                            )

                            val queue = Volley.newRequestQueue(context)
                            queue.add(bookfetchrequest)


//                    if(i >= jsonArray.length() - item_count*scroll_count){
//                        items.add(BusinessItem)
//                        itemIndex.add(num) //앞에다가 추가.
//                    }

                        }
                    } else Log.d("fffffffail", "business board no Item")
                } else Log.d("fffffffffffffail", "business board list fail")
            }, {
                Log.d(
                    "login failed",
                    "error......${context?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf(
                "id" to appUser
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)

    }
}