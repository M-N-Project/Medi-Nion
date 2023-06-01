package com.example.medi_nion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class BusinessMainFragment : Fragment() { //bussiness 체널 보여주는 프레그먼트

    private lateinit var BusinessBoardRecyclerView : RecyclerView
    private lateinit var BusinessBoardHomeRecyclerView : RecyclerView
    private lateinit var BusinessHotRecycler : RecyclerView
    private lateinit var BusinessSubRecycler : RecyclerView
    private lateinit var activity : Activity

    private lateinit var noChan : TextView
    var detail_items = HashMap<String, ArrayList<BusinessBoardItem>>()
    var info_items = HashMap<String, String>()
    var items = ArrayList<String>()
//    var items = ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    var new_items = ArrayList<BusinessBoardItem>()
//    var adapter = BusinessRecyclerAdapter(items)
    lateinit var home_adapter : BusinessHomeRecyclerAdapter

    private var hotListItems = ArrayList<BusinessHotListItem>()
    var hotAdapter = BusinessHotListAdapter(hotListItems)

    private var subListItems = ArrayList<BusinessHotListItem>()
    var subAdapter = BusinessSubListAdapter(hotListItems)

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var myFragment: Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.business_home, container, false)
        myFragment = this

        var appUser = arguments?.getString("id").toString()

//        swipeRefreshLayout = view.findViewById(R.id.business_refresh_layout)
//        swipeRefreshLayout.setOnRefreshListener {
//            fragmentManager?.beginTransaction()?.detach(myFragment)?.attach(fragment)?.commit()
//            swipeRefreshLayout.isRefreshing = false
//        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout?.setOnRefreshListener {
            swipeRefreshLayout?.setColorSchemeResources(R.color.color5)
            refreshFragment()
            swipeRefreshLayout?.isRefreshing = false
        }


        home_adapter = BusinessHomeRecyclerAdapter(appUser, items,info_items, detail_items)
//        BusinessBoardRecyclerView = view.findViewById<RecyclerView>(R.id.BusinessBoardRecyclerView)
        BusinessBoardHomeRecyclerView = view.findViewById<RecyclerView>(R.id.BusinessBoardHomeRecyclerView)
        BusinessHotRecycler = view.findViewById<RecyclerView>(R.id.BusinessHotRecycler)
//        BusinessSubRecycler = view.findViewById<RecyclerView>(R.id.BusinessSubRecycler)

//        BusinessBoardRecyclerView.adapter = adapter
        BusinessBoardHomeRecyclerView.adapter = home_adapter
        BusinessHotRecycler.adapter = hotAdapter
//        BusinessSubRecycler.adapter = subAdapter

        return view
    }

    private fun refreshFragment() {
        swipeRefreshLayout?.let {
            it.isRefreshing = true

            // 프래그먼트를 재갱신하는 작업을 수행합니다.
//            val fragmentTransaction = parentFragmentManager.beginTransaction()
//            fragmentTransaction.detach(this)
//            fragmentTransaction.attach(this)
//            fragmentTransaction.commitAllowingStateLoss()
            fetchHotProfile()
            fetchData()

            it.isRefreshing = false
        }
    }


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
    var scrollFlag = false

    override fun onResume() {
        super.onResume()

        items.clear()
        all_items.clear()
        subListItems.clear()
        hotListItems.clear()

        fetchHotProfile()
//        fetchSubProfile()
        fetchData()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        items.clear()
//        all_items.clear()
//        subListItems.clear()
//        hotListItems.clear()
//
//        fetchHotProfile()
////        fetchSubProfile()
//        fetchData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        items.clear()
        all_items.clear()
        subListItems.clear()
        hotListItems.clear()

        if(context is Activity){
            activity = context as Activity
        }
    }

    //비즈니스 프래그먼트 새로고침하기
//    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
//        var ft: FragmentTransaction = fragmentManager.beginTransaction()
//        ft.detach(fragment).attach(fragment).commit()
//    }

    ////////////////// 신규 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
    fun fetchHotProfile() {
        var appUser = arguments?.getString("id").toString()
        var nickname = arguments?.getString("nickname").toString()
        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
        val urlNewProfile = "http://seonho.dothome.co.kr/Business_profileNew_list.php"
        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileRand_list.php"

        business_nickname_hot?.text = nickname
        business_nickname_sub?.text = nickname
        val request = Board_Request(
            Request.Method.POST,
            urlNewProfile,
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
                    hotAdapter = BusinessHotListAdapter(hotListItems)
                    BusinessHotRecycler.adapter = hotAdapter
                    hotAdapter.notifyDataSetChanged()

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
                            var appUser = arguments?.getString("id").toString()
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
                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
                    val request = Board_Request(
                        Request.Method.POST,
                        urlRandProfile,
                        { responseRand ->
                            if (responseRand != "no BusinessProfile"){
                                hotListItems.clear()
                                val jsonArray = JSONArray(responseRand)

                                for (i in jsonArray.length() - 1 downTo 0) {
                                    val item = jsonArray.getJSONObject(i)

                                    val chanName = item.getString("channel_name")
                                    val chanProfile = item.getString("channel_profile_img")

                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
                                    hotListItems.add(HotListItem)

                                }
                                hotListItems.reverse()
                                var hotAdapter = BusinessHotListAdapter(hotListItems)
                                BusinessHotRecycler.adapter = hotAdapter
                                hotAdapter.notifyDataSetChanged()

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

                                        var appUser = arguments?.getString("id").toString()
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
                    val queue = Volley.newRequestQueue(activity?.applicationContext)
                    queue.add(request)
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
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)
    }

//    ////////////////// 인기 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
//    fun fetchHotProfile() {
//        var appUser = arguments?.getString("id").toString()
//        var nickname = arguments?.getString("nickname").toString()
//        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
//        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
//        val urlHotProfile = "http://seonho.dothome.co.kr/Business_profileHot_list.php"
//        val urlSubProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"
//        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileNew_list.php"
//
//        business_nickname_hot?.text = nickname
//        business_nickname_sub?.text = nickname
//
//        val request = Board_Request(
//            Request.Method.POST,
//            urlHotProfile,
//            { response ->
//                if (response != "no BusinessProfile"){
//                    hotListItems.clear()
//                    val jsonArray = JSONArray(response)
//
//                    for (i in jsonArray.length() - 1 downTo 0) {
//                        val item = jsonArray.getJSONObject(i)
//
//                        val chanName = item.getString("channel_name")
//                        var writerId = item.getString("id")
//                        val chanProfile = item.getString("channel_profile_img")
//
//                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                        hotListItems.add(HotListItem)
//
//                    }
//                    hotListItems.reverse()
//                    hotAdapter = BusinessHotListAdapter(hotListItems)
//                    hotAdapter.notifyDataSetChanged()
//                    BusinessHotRecycler.adapter = hotAdapter
//
//                    hotAdapter.setOnItemClickListener(object :
//                        BusinessHotListAdapter.OnItemClickListener {
//                        override fun onProfileClick(
//                            v: View,
//                            data: BusinessHotListItem,
//                            pos: Int
//                        ){
//                            val intent =
//                                Intent(
//                                    context,
//                                    BusinessProfileActivity::class.java
//                                )
//                            var appUser = arguments?.getString("id").toString()
//                            intent.putExtra("appUser", appUser)
//                            intent.putExtra(
//                                "channel_name",
//                                data.chanName
//                            )
//                            startActivity(intent)
//                        }
//                    })
//                }
//                else {
//                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
//                    val request = Board_Request(
//                        Request.Method.POST,
//                        urlRandProfile,
//                        { responseRand ->
//                            Log.d("0712312", responseRand)
//                            if (responseRand != "no BusinessProfile"){
//                                hotListItems.clear()
//                                val jsonArray = JSONArray(responseRand)
//
//                                for (i in jsonArray.length() - 1 downTo 0) {
//                                    val item = jsonArray.getJSONObject(i)
//
//                                    val chanName = item.getString("channel_name")
//                                    val chanProfile = item.getString("channel_profile_img")
//
//                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                                    hotListItems.add(HotListItem)
//
//                                }
//                                hotListItems.reverse()
//                                var hotAdapter = BusinessHotListAdapter(hotListItems)
//                                hotAdapter.notifyDataSetChanged()
//                                BusinessHotRecycler.adapter = hotAdapter
//
//                                hotAdapter.setOnItemClickListener(object :
//                                    BusinessHotListAdapter.OnItemClickListener {
//                                    override fun onProfileClick(
//                                        v: View,
//                                        data: BusinessHotListItem,
//                                        pos: Int
//                                    ){
//                                        val intent =
//                                            Intent(
//                                                context,
//                                                BusinessProfileActivity::class.java
//                                            )
//
//                                        var appUser = arguments?.getString("id").toString()
//                                        intent.putExtra("appUser", appUser)
//                                        intent.putExtra(
//                                            data.chanName
//                                        )
//                                        startActivity(intent)
//                                    }
//                                })
//                            }
//                            else {
//                                Toast.makeText(context, "프로필가져오기 실패", Toast.LENGTH_SHORT)
//                            }
//                        },
//                        {
//                            Log.d(
//                                "login failed",
//                                "error......${context?.let { it1 -> error(it1) }}"
//                            )
//                        },
//                        hashMapOf()
//                    )
//                    val queue = Volley.newRequestQueue(activity?.applicationContext)
//                    queue.add(request)
//                }
//            },
//            {
//                Log.d(
//                    "login failed",
//                    "error......${context?.let { it1 -> error(it1) }}"
//                )
//            },
//            hashMapOf()
//        )
//        val queue = Volley.newRequestQueue(activity?.applicationContext)
//        queue.add(request)
//    }

    ////////////////// 구독 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
    fun fetchSubProfile() {
        var appUser = arguments?.getString("id").toString()
        var nickname = arguments?.getString("nickname").toString()
        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
        val urlSubProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"
        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileRand_list.php"

        business_nickname_hot?.text = nickname
        business_nickname_sub?.text = nickname
        subListItems.clear()
        val request = Board_Request(
            Request.Method.POST,
            urlSubProfile,
            { response ->
                if (response != "business sub list no Item"){
                    val jsonArray = JSONArray(response)

                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)

                        val chanName = item.getString("Channel_name")
                        var writerId = item.getString("id")
                        val chanProfile = item.getString("Channel_profile_img")

                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
                        subListItems.add(HotListItem)

                    }
                    subListItems.reverse()
                    subAdapter = BusinessSubListAdapter(subListItems)
                    BusinessSubRecycler.adapter = subAdapter
                    subAdapter.notifyDataSetChanged()

                    subAdapter.setOnItemClickListener(object :
                        BusinessSubListAdapter.OnItemClickListener {
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
                            var appUser = arguments?.getString("id").toString()
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
                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
                    val request = Board_Request(
                        Request.Method.POST,
                        urlRandProfile,
                        { responseRand ->
                            if (responseRand != "no BusinessProfile"){
                                subListItems.clear()
                                val jsonArray = JSONArray(responseRand)

                                for (i in jsonArray.length() - 1 downTo 0) {
                                    val item = jsonArray.getJSONObject(i)

                                    val chanName = item.getString("channel_name")
                                    val chanProfile = item.getString("channel_profile_img")

                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
                                    hotListItems.add(HotListItem)

                                }
                                subListItems.reverse()
                                var subAdapter = BusinessSubListAdapter(subListItems)
                                BusinessSubRecycler.adapter = subAdapter
                                subAdapter.notifyDataSetChanged()

                                subAdapter.setOnItemClickListener(object :
                                    BusinessSubListAdapter.OnItemClickListener {
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

                                        var appUser = arguments?.getString("id").toString()
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
                        hashMapOf(
                            "id" to appUser
                        )
                    )
                    val queue = Volley.newRequestQueue(activity?.applicationContext)
                    queue.add(request)
                }
            },
            {
                Log.d(
                    "login failed",
                    "error......${context?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf(
                "id" to appUser
            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)
    }

    ////////////////// 구독 중인 채널의 게시물 가져오는 fetch 함수 + 좋아요&북마크 //////////////////////////////////////////////////////
    fun fetchData() {
        // url to post our data
        var appUser = arguments?.getString("id").toString()
        val urlBoard = "http://seonho.dothome.co.kr/BusinessBoardHomeSub_list.php"
        val urlBookmark = "http://seonho.dothome.co.kr/BusinessBookmark_list.php"
        val urlLike = "http://seonho.dothome.co.kr/BusinessLike_list.php"
        val urlIsSub = "http://seonho.dothome.co.kr/ChannelSubList.php"
        val urlProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"

        items.clear()
        all_items.clear()
        subListItems.clear()
        detail_items.clear()

        val request1 = Board_Request(
            Request.Method.POST,
            urlProfile,
            { response ->
                if (response != "business sub list no Item") {
                    val jsonArray = JSONArray(response)

                    for (f in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(f)

                        val id = item.getString("id")
                        val channel_name = item.getString("Channel_name")
                        val profileImg = item.getString("Channel_profile_img")

                        val request2 = Board_Request(
                            Request.Method.POST,
                            urlBoard,
                            { response ->
                                if(response != "business sub list fail") {
                                    if (response != "business board no Item") {

                                        items.add(channel_name)
                                        info_items[channel_name] = id
                                        detail_items[channel_name]?.clear()

                                        val jsonArray = JSONArray(response)


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
                                                                profileImg,
                                                                channel_name,
                                                                title,
                                                                content,
                                                                time,
                                                                image1,
                                                                image2,
                                                                image3,
                                                                isHeart,
                                                                isBookmark,
                                                                false
                                                            )

                                                            if (detail_items[channel_name] == null) {
                                                                detail_items[channel_name] =
                                                                    ArrayList<BusinessBoardItem>()
                                                            }
                                                            detail_items[channel_name]!!.add(
                                                                BusinessItem
                                                            )

                                                            if(i==0) {
                                                                home_adapter =
                                                                    BusinessHomeRecyclerAdapter(
                                                                        appUser,
                                                                        items,
                                                                        info_items,
                                                                        detail_items
                                                                    )
                                                                BusinessBoardHomeRecyclerView.adapter =
                                                                    home_adapter
                                                                home_adapter.notifyDataSetChanged()
                                                                home_adapter.setOnItemClickListener(
                                                                    object :
                                                                        BusinessHomeRecyclerAdapter.OnItemClickListener {
                                                                        override fun onProfileClick(
                                                                            v: View,
                                                                            data: String,
                                                                            pos: Int
                                                                        ) {
                                                                            val intent =
                                                                                Intent(
                                                                                    context,
                                                                                    BusinessProfileActivity::class.java
                                                                                )
                                                                            intent.putExtra(
                                                                                "appUser",
                                                                                appUser
                                                                            )
                                                                            intent.putExtra(
                                                                                "channel_name",
                                                                                data
                                                                            )
                                                                            startActivity(intent)
                                                                        }

                                                                    })
                                                            }
                                                        },
                                                        {
                                                            Log.d(
                                                                "login failed",
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
                                                            "post_num" to num.toString()
                                                        )
                                                    )
                                                    val queue =
                                                        Volley.newRequestQueue(activity?.applicationContext)
                                                    queue.add(likerequest)
                                                },
                                                {
                                                    Log.d(
                                                        "login failed",
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
                                                    "post_num" to num.toString()
                                                )
                                            )
                                            val queue =
                                                Volley.newRequestQueue(activity?.applicationContext)
                                            queue.add(bookfetchrequest)

                                        }

                                    }
                                    else{
                                        home_adapter =
                                            BusinessHomeRecyclerAdapter(
                                                appUser,
                                                items,
                                                info_items,
                                                detail_items
                                            )
                                        BusinessBoardHomeRecyclerView.adapter =
                                            home_adapter
                                        home_adapter.notifyDataSetChanged()
                                        home_adapter.setOnItemClickListener(
                                            object :
                                                BusinessHomeRecyclerAdapter.OnItemClickListener {
                                                override fun onProfileClick(
                                                    v: View,
                                                    data: String,
                                                    pos: Int
                                                ) {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            BusinessProfileActivity::class.java
                                                        )
                                                    intent.putExtra(
                                                        "appUser",
                                                        appUser
                                                    )
                                                    intent.putExtra(
                                                        "channel_name",
                                                        data
                                                    )
                                                    startActivity(intent)
                                                }

                                            })
                                    }
                                }

                            }, {
                                Log.d(
                                    "login failed",
                                    "error......${context?.let { it1 -> error(it1) }}"
                                )
                            },
                            hashMapOf(
                                "id" to appUser,
                                "channel_name" to channel_name
                            )
                        )
                        val queue = Volley.newRequestQueue(activity?.applicationContext)
                        queue.add(request2)
                    }
                }



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

        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request1)

    }
}






//package com.example.medi_nion
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentTransaction
//import androidx.recyclerview.widget.RecyclerView
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.android.volley.DefaultRetryPolicy
//import com.android.volley.Request
//import com.android.volley.toolbox.Volley
//import org.json.JSONArray
//
//
//class BusinessMainFragment : Fragment() { //bussiness 체널 보여주는 프레그먼트
//
//    private lateinit var BusinessBoardRecyclerView : RecyclerView
//    private lateinit var BusinessBoardHomeRecyclerView : RecyclerView
//    private lateinit var BusinessHotRecycler : RecyclerView
//    private lateinit var BusinessSubRecycler : RecyclerView
//    private lateinit var activity : Activity
//
//    private lateinit var noChan : TextView
//    var detail_items = HashMap<String, ArrayList<BusinessBoardItem>>()
//    var info_items = HashMap<String, String>()
//    var items = ArrayList<String>()
//    //    var items = ArrayList<BusinessBoardItem>()
//    var all_items = ArrayList<BusinessBoardItem>()
//    var new_items = ArrayList<BusinessBoardItem>()
//    //    var adapter = BusinessRecyclerAdapter(items)
//    lateinit var home_adapter : BusinessHomeRecyclerAdapter
//
//    private var hotListItems = ArrayList<BusinessHotListItem>()
//    var hotAdapter = BusinessHotListAdapter(hotListItems)
//
//    private var subListItems = ArrayList<BusinessHotListItem>()
//    var subAdapter = BusinessSubListAdapter(hotListItems)
//
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
//    private lateinit var myFragment: Fragment
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view =  inflater.inflate(R.layout.business_home, container, false)
//        myFragment = this
//
//        var appUser = arguments?.getString("id").toString()
//
////        swipeRefreshLayout = view.findViewById(R.id.business_refresh_layout)
////        swipeRefreshLayout.setOnRefreshListener {
////            fragmentManager?.beginTransaction()?.detach(myFragment)?.attach(fragment)?.commit()
////            swipeRefreshLayout.isRefreshing = false
////        }
//
//        home_adapter = BusinessHomeRecyclerAdapter(appUser, items,info_items, detail_items)
////        BusinessBoardRecyclerView = view.findViewById<RecyclerView>(R.id.BusinessBoardRecyclerView)
//        BusinessBoardHomeRecyclerView = view.findViewById<RecyclerView>(R.id.BusinessBoardHomeRecyclerView)
//        BusinessHotRecycler = view.findViewById<RecyclerView>(R.id.BusinessHotRecycler)
////        BusinessSubRecycler = view.findViewById<RecyclerView>(R.id.BusinessSubRecycler)
//
////        BusinessBoardRecyclerView.adapter = adapter
//        BusinessBoardHomeRecyclerView.adapter = home_adapter
//        BusinessHotRecycler.adapter = hotAdapter
////        BusinessSubRecycler.adapter = subAdapter
//
//        return view
//    }
//
//    var num = 0 //비즈니스 채널 번호
//    var writerId = ""
//    var channel_name = ""
//    var title = "" //비즈니스 채널명
//    var content = "" //비즈니스 채널 내용
//    var time = "" //비즈니스 채널 게시글 업로드 시간
//    var image1 = "" //비즈니스 채널 사진 1
//    var image2 = "" //비즈니스 채널 사진 2
//    var image3 = "" //비즈니스 채널 사진 3
//    var isHeart = false // 좋아요 정보
//    var isBookmark = false // 북마크 정보
//    var isSub = false
//    var scrollFlag = false
//
//    override fun onResume() {
//        super.onResume()
//
//        items.clear()
//        all_items.clear()
//        subListItems.clear()
//        hotListItems.clear()
//
//        fetchHotProfile()
////        fetchSubProfile()
//        fetchData()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////
////        items.clear()
////        all_items.clear()
////        subListItems.clear()
////        hotListItems.clear()
////
////        fetchHotProfile()
//////        fetchSubProfile()
////        fetchData()
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        items.clear()
//        all_items.clear()
//        subListItems.clear()
//        hotListItems.clear()
//
//        if(context is Activity){
//            activity = context as Activity
//        }
//    }
//
//    //비즈니스 프래그먼트 새로고침하기
////    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
////        var ft: FragmentTransaction = fragmentManager.beginTransaction()
////        ft.detach(fragment).attach(fragment).commit()
////    }
//
//    ////////////////// 신규 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
//    fun fetchHotProfile() {
//        var appUser = arguments?.getString("id").toString()
//        var nickname = arguments?.getString("nickname").toString()
//        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
//        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
//        val urlNewProfile = "http://seonho.dothome.co.kr/Business_profileNew_list.php"
//        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileRand_list.php"
//
//        business_nickname_hot?.text = nickname
//        business_nickname_sub?.text = nickname
//        val request = Board_Request(
//            Request.Method.POST,
//            urlNewProfile,
//            { response ->
//                if (response != "no BusinessProfile"){
//                    hotListItems.clear()
//                    val jsonArray = JSONArray(response)
//
//                    for (i in jsonArray.length() - 1 downTo 0) {
//                        val item = jsonArray.getJSONObject(i)
//
//                        val chanName = item.getString("channel_name")
//                        val chanProfile = item.getString("channel_profile_img")
//
//                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                        hotListItems.add(HotListItem)
//
//                    }
//                    hotListItems.reverse()
//                    hotAdapter = BusinessHotListAdapter(hotListItems)
//                    hotAdapter.notifyDataSetChanged()
//                    BusinessHotRecycler.adapter = hotAdapter
//
//                    hotAdapter.setOnItemClickListener(object :
//                        BusinessHotListAdapter.OnItemClickListener {
//                        override fun onProfileClick(
//                            v: View,
//                            data: BusinessHotListItem,
//                            pos: Int
//                        ){
//                            val intent =
//                                Intent(
//                                    context,
//                                    BusinessProfileActivity::class.java
//                                )
//                            var appUser = arguments?.getString("id").toString()
//                            intent.putExtra("appUser", appUser)
//                            intent.putExtra(
//                                "channel_name",
//                                data.chanName
//                            )
//                            startActivity(intent)
//                        }
//                    })
//                }
//                else {
//                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
//                    val request = Board_Request(
//                        Request.Method.POST,
//                        urlRandProfile,
//                        { responseRand ->
//                            if (responseRand != "no BusinessProfile"){
//                                hotListItems.clear()
//                                val jsonArray = JSONArray(responseRand)
//
//                                for (i in jsonArray.length() - 1 downTo 0) {
//                                    val item = jsonArray.getJSONObject(i)
//
//                                    val chanName = item.getString("channel_name")
//                                    val chanProfile = item.getString("channel_profile_img")
//
//                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                                    hotListItems.add(HotListItem)
//
//                                }
//                                hotListItems.reverse()
//                                var hotAdapter = BusinessHotListAdapter(hotListItems)
//                                hotAdapter.notifyDataSetChanged()
//                                BusinessHotRecycler.adapter = hotAdapter
//
//                                hotAdapter.setOnItemClickListener(object :
//                                    BusinessHotListAdapter.OnItemClickListener {
//                                    override fun onProfileClick(
//                                        v: View,
//                                        data: BusinessHotListItem,
//                                        pos: Int
//                                    ){
//                                        val intent =
//                                            Intent(
//                                                context,
//                                                BusinessProfileActivity::class.java
//                                            )
//
//                                        var appUser = arguments?.getString("id").toString()
//                                        intent.putExtra("appUser", appUser)
//                                        intent.putExtra(
//                                            "channel_name",
//                                            data.chanName
//                                        )
//                                        startActivity(intent)
//                                    }
//                                })
//                            }
//                            else {
//                                Toast.makeText(context, "프로필가져오기 실패", Toast.LENGTH_SHORT)
//                            }
//                        },
//                        {
//                            Log.d(
//                                "login failed",
//                                "error......${context?.let { it1 -> error(it1) }}"
//                            )
//                        },
//                        hashMapOf()
//                    )
//                    val queue = Volley.newRequestQueue(activity?.applicationContext)
//                    queue.add(request)
//                }
//            },
//            {
//                Log.d(
//                    "login failed",
//                    "error......${context?.let { it1 -> error(it1) }}"
//                )
//            },
//            hashMapOf()
//        )
//        val queue = Volley.newRequestQueue(activity?.applicationContext)
//        queue.add(request)
//    }
//
////    ////////////////// 인기 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
////    fun fetchHotProfile() {
////        var appUser = arguments?.getString("id").toString()
////        var nickname = arguments?.getString("nickname").toString()
////        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
////        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
////        val urlHotProfile = "http://seonho.dothome.co.kr/Business_profileHot_list.php"
////        val urlSubProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"
////        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileNew_list.php"
////
////        business_nickname_hot?.text = nickname
////        business_nickname_sub?.text = nickname
////
////        val request = Board_Request(
////            Request.Method.POST,
////            urlHotProfile,
////            { response ->
////                if (response != "no BusinessProfile"){
////                    hotListItems.clear()
////                    val jsonArray = JSONArray(response)
////
////                    for (i in jsonArray.length() - 1 downTo 0) {
////                        val item = jsonArray.getJSONObject(i)
////
////                        val chanName = item.getString("channel_name")
////                        var writerId = item.getString("id")
////                        val chanProfile = item.getString("channel_profile_img")
////
////                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
////                        hotListItems.add(HotListItem)
////
////                    }
////                    hotListItems.reverse()
////                    hotAdapter = BusinessHotListAdapter(hotListItems)
////                    hotAdapter.notifyDataSetChanged()
////                    BusinessHotRecycler.adapter = hotAdapter
////
////                    hotAdapter.setOnItemClickListener(object :
////                        BusinessHotListAdapter.OnItemClickListener {
////                        override fun onProfileClick(
////                            v: View,
////                            data: BusinessHotListItem,
////                            pos: Int
////                        ){
////                            val intent =
////                                Intent(
////                                    context,
////                                    BusinessProfileActivity::class.java
////                                )
////                            var appUser = arguments?.getString("id").toString()
////                            intent.putExtra("appUser", appUser)
////                            intent.putExtra(
////                                "channel_name",
////                                data.chanName
////                            )
////                            startActivity(intent)
////                        }
////                    })
////                }
////                else {
////                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
////                    val request = Board_Request(
////                        Request.Method.POST,
////                        urlRandProfile,
////                        { responseRand ->
////                            Log.d("0712312", responseRand)
////                            if (responseRand != "no BusinessProfile"){
////                                hotListItems.clear()
////                                val jsonArray = JSONArray(responseRand)
////
////                                for (i in jsonArray.length() - 1 downTo 0) {
////                                    val item = jsonArray.getJSONObject(i)
////
////                                    val chanName = item.getString("channel_name")
////                                    val chanProfile = item.getString("channel_profile_img")
////
////                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
////                                    hotListItems.add(HotListItem)
////
////                                }
////                                hotListItems.reverse()
////                                var hotAdapter = BusinessHotListAdapter(hotListItems)
////                                hotAdapter.notifyDataSetChanged()
////                                BusinessHotRecycler.adapter = hotAdapter
////
////                                hotAdapter.setOnItemClickListener(object :
////                                    BusinessHotListAdapter.OnItemClickListener {
////                                    override fun onProfileClick(
////                                        v: View,
////                                        data: BusinessHotListItem,
////                                        pos: Int
////                                    ){
////                                        val intent =
////                                            Intent(
////                                                context,
////                                                BusinessProfileActivity::class.java
////                                            )
////
////                                        var appUser = arguments?.getString("id").toString()
////                                        intent.putExtra("appUser", appUser)
////                                        intent.putExtra(
////                                            data.chanName
////                                        )
////                                        startActivity(intent)
////                                    }
////                                })
////                            }
////                            else {
////                                Toast.makeText(context, "프로필가져오기 실패", Toast.LENGTH_SHORT)
////                            }
////                        },
////                        {
////                            Log.d(
////                                "login failed",
////                                "error......${context?.let { it1 -> error(it1) }}"
////                            )
////                        },
////                        hashMapOf()
////                    )
////                    val queue = Volley.newRequestQueue(activity?.applicationContext)
////                    queue.add(request)
////                }
////            },
////            {
////                Log.d(
////                    "login failed",
////                    "error......${context?.let { it1 -> error(it1) }}"
////                )
////            },
////            hashMapOf()
////        )
////        val queue = Volley.newRequestQueue(activity?.applicationContext)
////        queue.add(request)
////    }
//
//    ////////////////// 구독 채널 가져오는 fetch 함수 //////////////////////////////////////////////////////
//    fun fetchSubProfile() {
//        var appUser = arguments?.getString("id").toString()
//        var nickname = arguments?.getString("nickname").toString()
//        val business_nickname_hot = view?.findViewById<TextView>(R.id.hotChan_nickname_title)
//        val business_nickname_sub = view?.findViewById<TextView>(R.id.subChan_nickname_title)
//        val urlSubProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"
//        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileRand_list.php"
//
//        business_nickname_hot?.text = nickname
//        business_nickname_sub?.text = nickname
//        subListItems.clear()
//        val request = Board_Request(
//            Request.Method.POST,
//            urlSubProfile,
//            { response ->
//                if (response != "business sub list no Item"){
//                    val jsonArray = JSONArray(response)
//
//                    for (i in jsonArray.length() - 1 downTo 0) {
//                        val item = jsonArray.getJSONObject(i)
//
//                        val chanName = item.getString("Channel_name")
//                        var writerId = item.getString("id")
//                        val chanProfile = item.getString("Channel_profile_img")
//
//                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                        subListItems.add(HotListItem)
//
//                    }
//                    subListItems.reverse()
//                    subAdapter = BusinessSubListAdapter(subListItems)
//                    subAdapter.notifyDataSetChanged()
//                    BusinessSubRecycler.adapter = subAdapter
//
//                    subAdapter.setOnItemClickListener(object :
//                        BusinessSubListAdapter.OnItemClickListener {
//                        override fun onProfileClick(
//                            v: View,
//                            data: BusinessHotListItem,
//                            pos: Int
//                        ){
//                            val intent =
//                                Intent(
//                                    context,
//                                    BusinessProfileActivity::class.java
//                                )
//                            var appUser = arguments?.getString("id").toString()
//                            intent.putExtra("appUser", appUser)
//                            intent.putExtra(
//                                "channel_name",
//                                data.chanName
//                            )
//                            startActivity(intent)
//                        }
//                    })
//                }
//                else {
//                    //인기채널이 없을때 -> 랜덤으로 프로필 가져오기.
//                    val request = Board_Request(
//                        Request.Method.POST,
//                        urlRandProfile,
//                        { responseRand ->
//                            if (responseRand != "no BusinessProfile"){
//                                subListItems.clear()
//                                val jsonArray = JSONArray(responseRand)
//
//                                for (i in jsonArray.length() - 1 downTo 0) {
//                                    val item = jsonArray.getJSONObject(i)
//
//                                    val chanName = item.getString("channel_name")
//                                    val chanProfile = item.getString("channel_profile_img")
//
//                                    val HotListItem = BusinessHotListItem(chanName, chanProfile)
//                                    hotListItems.add(HotListItem)
//
//                                }
//                                subListItems.reverse()
//                                var subAdapter = BusinessSubListAdapter(subListItems)
//                                subAdapter.notifyDataSetChanged()
//                                BusinessSubRecycler.adapter = subAdapter
//
//                                subAdapter.setOnItemClickListener(object :
//                                    BusinessSubListAdapter.OnItemClickListener {
//                                    override fun onProfileClick(
//                                        v: View,
//                                        data: BusinessHotListItem,
//                                        pos: Int
//                                    ){
//                                        val intent =
//                                            Intent(
//                                                context,
//                                                BusinessProfileActivity::class.java
//                                            )
//
//                                        var appUser = arguments?.getString("id").toString()
//                                        intent.putExtra("appUser", appUser)
//                                        intent.putExtra(
//                                            "channel_name",
//                                            data.chanName
//                                        )
//                                        startActivity(intent)
//                                    }
//                                })
//                            }
//                            else {
//                                Toast.makeText(context, "프로필가져오기 실패", Toast.LENGTH_SHORT)
//                            }
//                        },
//                        {
//                            Log.d(
//                                "login failed",
//                                "error......${context?.let { it1 -> error(it1) }}"
//                            )
//                        },
//                        hashMapOf(
//                            "id" to appUser
//                        )
//                    )
//                    val queue = Volley.newRequestQueue(activity?.applicationContext)
//                    queue.add(request)
//                }
//            },
//            {
//                Log.d(
//                    "login failed",
//                    "error......${context?.let { it1 -> error(it1) }}"
//                )
//            },
//            hashMapOf(
//                "id" to appUser
//            )
//        )
//        val queue = Volley.newRequestQueue(activity?.applicationContext)
//        queue.add(request)
//    }
//
//    ////////////////// 구독 중인 채널의 게시물 가져오는 fetch 함수 + 좋아요&북마크 //////////////////////////////////////////////////////
//    fun fetchData() {
//        // url to post our data
//        var appUser = arguments?.getString("id").toString()
//        val urlBoard = "http://seonho.dothome.co.kr/BusinessBoardHomeSub_list.php"
//        val urlBookmark = "http://seonho.dothome.co.kr/BusinessBookmark_list.php"
//        val urlLike = "http://seonho.dothome.co.kr/BusinessLike_list.php"
//        val urlIsSub = "http://seonho.dothome.co.kr/ChannelSubList.php"
//        val urlProfile = "http://seonho.dothome.co.kr/Business_profileSub_list.php"
//
//        items.clear()
//        all_items.clear()
//        subListItems.clear()
//        detail_items.clear()
//
//        val request1 = Board_Request(
//            Request.Method.POST,
//            urlProfile,
//            { response ->
//                if (response != "business sub list no Item") {
//                    val jsonArray = JSONArray(response)
//
//                    for (i in jsonArray.length() - 1 downTo 0) {
//                        val item = jsonArray.getJSONObject(i)
//
//                        val id = item.getString("id")
//                        val channel_name = item.getString("Channel_name")
//                        val profileImg = item.getString("Channel_profile_img")
//
//                        val request2 = Board_Request(
//                            Request.Method.POST,
//                            urlBoard,
//                            { response ->
//                                if(response != "business sub list fail") {
//                                    if (response != "business board no Item") {
//
//                                        items.add(channel_name)
//                                        info_items[channel_name] = id
//                                        detail_items[channel_name]?.clear()
//
//                                        val jsonArray = JSONArray(response)
//
//
//                                        for (i in jsonArray.length() - 1 downTo 0) {
//                                            val item = jsonArray.getJSONObject(i)
//
//                                            val num = item.getInt("num")
//                                            val writerId = item.getString("id")
//                                            val channel_name = item.getString("channel_name")
//                                            val title = item.getString("title")
//                                            val content = item.getString("content")
//                                            val time = item.getString("time")
//                                            val image1 = item.getString("image1")
//                                            val image2 = item.getString("image2")
//                                            val image3 = item.getString("image3")
//
//                                            val bookfetchrequest = Login_Request(
//                                                Request.Method.POST,
//                                                urlBookmark,
//                                                { responseBookmark ->
//                                                    if (responseBookmark.equals("Success Bookmark")) {
//                                                        isBookmark = true
//                                                    } else if (responseBookmark.equals("No Bookmark")) {
//                                                        isBookmark = false
//                                                    }
//                                                    val likerequest = Login_Request(
//                                                        Request.Method.POST,
//                                                        urlLike,
//                                                        { responseLike ->
//                                                            if (responseLike.equals("Success Heart")) {
//                                                                isHeart = true
//                                                            } else if (responseLike.equals("No Heart")) {
//                                                                isHeart = false
//                                                            }
//
//                                                            val BusinessItem = BusinessBoardItem(
//                                                                num,
//                                                                writerId,
//                                                                profileImg,
//                                                                channel_name,
//                                                                title,
//                                                                content,
//                                                                time,
//                                                                image1,
//                                                                image2,
//                                                                image3,
//                                                                isHeart,
//                                                                isBookmark,
//                                                                false
//                                                            )
//
//                                                            if (detail_items[channel_name] == null) {
//                                                                detail_items[channel_name] =
//                                                                    ArrayList<BusinessBoardItem>()
//                                                            }
//                                                            detail_items[channel_name]!!.add(
//                                                                BusinessItem
//                                                            )
//
//                                                            home_adapter =
//                                                                BusinessHomeRecyclerAdapter(
//                                                                    appUser,
//                                                                    items,
//                                                                    info_items,
//                                                                    detail_items
//                                                                )
//                                                            BusinessBoardHomeRecyclerView.adapter =
//                                                                home_adapter
//                                                            home_adapter.setOnItemClickListener(
//                                                                object :
//                                                                    BusinessHomeRecyclerAdapter.OnItemClickListener {
//                                                                    override fun onProfileClick(
//                                                                        v: View,
//                                                                        data: String,
//                                                                        pos: Int
//                                                                    ) {
//                                                                        val intent =
//                                                                            Intent(
//                                                                                context,
//                                                                                BusinessProfileActivity::class.java
//                                                                            )
//                                                                        intent.putExtra(
//                                                                            "appUser",
//                                                                            appUser
//                                                                        )
//                                                                        intent.putExtra(
//                                                                            "channel_name",
//                                                                            data
//                                                                        )
//                                                                        startActivity(intent)
//                                                                    }
//
//                                                                })
//
//                                                        },
//                                                        {
//                                                            Log.d(
//                                                                "login failed",
//                                                                "error......${
//                                                                    context?.let { it1 ->
//                                                                        error(
//                                                                            it1
//                                                                        )
//                                                                    }
//                                                                }"
//                                                            )
//                                                        },
//                                                        hashMapOf(
//                                                            "id" to appUser,
//                                                            "post_num" to num.toString()
//                                                        )
//                                                    )
//                                                    val queue =
//                                                        Volley.newRequestQueue(activity?.applicationContext)
//                                                    queue.add(likerequest)
//                                                },
//                                                {
//                                                    Log.d(
//                                                        "login failed",
//                                                        "error......${
//                                                            context?.let { it1 ->
//                                                                error(
//                                                                    it1
//                                                                )
//                                                            }
//                                                        }"
//                                                    )
//                                                },
//                                                hashMapOf(
//                                                    "id" to appUser,
//                                                    "post_num" to num.toString()
//                                                )
//                                            )
//                                            val queue =
//                                                Volley.newRequestQueue(activity?.applicationContext)
//                                            queue.add(bookfetchrequest)
//
//                                        }
//
//                                    }
//                                    else{
//                                        home_adapter =
//                                            BusinessHomeRecyclerAdapter(
//                                                appUser,
//                                                items,
//                                                info_items,
//                                                detail_items
//                                            )
//                                        BusinessBoardHomeRecyclerView.adapter =
//                                            home_adapter
//
//                                        home_adapter.setOnItemClickListener(
//                                            object :
//                                                BusinessHomeRecyclerAdapter.OnItemClickListener {
//                                                override fun onProfileClick(
//                                                    v: View,
//                                                    data: String,
//                                                    pos: Int
//                                                ) {
//                                                    val intent =
//                                                        Intent(
//                                                            context,
//                                                            BusinessProfileActivity::class.java
//                                                        )
//                                                    intent.putExtra(
//                                                        "appUser",
//                                                        appUser
//                                                    )
//                                                    intent.putExtra(
//                                                        "channel_name",
//                                                        data
//                                                    )
//                                                    startActivity(intent)
//                                                }
//
//                                            })
//                                    }
//                                }
//
//                            }, {
//                                Log.d(
//                                    "login failed",
//                                    "error......${context?.let { it1 -> error(it1) }}"
//                                )
//                            },
//                            hashMapOf(
//                                "id" to appUser,
//                                "channel_name" to channel_name
//                            )
//                        )
//                        val queue = Volley.newRequestQueue(activity?.applicationContext)
//                        queue.add(request2)
//                    }
//                }
//
//
//
//            }, {
//                Log.d(
//                    "login failed",
//                    "error......${context?.let { it1 -> error(it1) }}"
//                )
//            },
//            hashMapOf(
//                "id" to appUser
//            )
//        )
//
//        val queue = Volley.newRequestQueue(activity?.applicationContext)
//        queue.add(request1)
//
//    }
//}