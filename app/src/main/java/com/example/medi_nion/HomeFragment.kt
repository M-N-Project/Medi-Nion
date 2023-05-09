package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_busi_new.*
import kotlinx.android.synthetic.main.home_qna.*
import org.json.JSONArray

class qnaNewItem(val num: Int, val title: String, val content: String) {
}

var qnaItems = java.util.ArrayList<qnaNewItem>()

class HomeFragment : Fragment(R.layout.home) { //피드 보여주는 홈화면 프레그먼트(게시판 구분은 추후에)

    private val random = (1..100000) // 1~100000 범위에서 알람코드 랜덤으로 생성
    private val alarmCode = random.random()
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    lateinit var notificationPermission: ActivityResultLauncher<String>

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val ALARM_REQUEST_CODE = 1000
        const val CHANNEL_ID = "medinion"
        const val CHANNEL_NAME = "schedule alarm"
    }

    private lateinit var activity : Activity
    private lateinit var id: String
    private lateinit var device_id: String
    private lateinit var nickname: String
    private lateinit var userType: String
    private lateinit var userDept: String
    private lateinit var userMedal: String
    private lateinit var ad_viewPager2: ViewPager2
    private lateinit var big_ad_viewPager2: ViewPager2

    class PagerRecyclerAdapter(private val qnaItem: java.util.ArrayList<qnaNewItem>) :
        RecyclerView.Adapter<PagerRecyclerAdapter.PagerViewHolder>() {

        inner class PagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val qna_title: TextView = itemView.findViewById(R.id.home_qna_title)
            private val qna_content: TextView = itemView.findViewById(R.id.home_qna_detail)
            private val qna_box = itemView.findViewById<Button>(R.id.detail_box)

            fun bind(item: qnaNewItem) {
                qna_title.text = item.title
                qna_content.text = item.content

                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    qna_box.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }
                    qna_title.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }
                    qna_content.setOnClickListener {
                        listener?.onItemClick(itemView, item, pos)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.home_qna_detail,
                parent,
                false
            )
            return PagerViewHolder(view)
        }


        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            val safePosition = holder.bindingAdapterPosition
            holder.bind(qnaItem[4 - safePosition])
        }

        override fun getItemCount(): Int = 5

        interface OnItemClickListener {
            fun onItemClick(v: View, data: qnaNewItem, pos: Int)
        }

        private var listener: OnItemClickListener? = null
        fun setOnItemClickListener(listener: PagerRecyclerAdapter.OnItemClickListener) {
            this.listener = listener
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        id = arguments?.getString("id").toString()
        Log.d("idididiidid   Home", id)
        super.onStart()
        fetchNewQna()
        fetchHotPost()
        fetchNewBusi()
        fetchHotProfile()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is Activity){
            activity = context as Activity
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home, container, false)

        val basicBoard = view.findViewById<TextView>(R.id.home_boardList1)
        val jobBoard = view.findViewById<TextView>(R.id.home_boardList2)
        val secBoard = view.findViewById<TextView>(R.id.home_boardList3)
        val marketBoard = view.findViewById<TextView>(R.id.home_boardList4)
        val qnaBoard = view.findViewById<TextView>(R.id.home_boardList5)
        val acadamy_info = view.findViewById<TextView>(R.id.home_boardList6)
        val employee_info = view.findViewById<TextView>(R.id.home_boardList7)
        val medi_news = view.findViewById<TextView>(R.id.home_boardList8)

        val business_nickname = view.findViewById<TextView>(R.id.home_business_nickname)

        var currentPosition = 0
        var big_currentPosition = 0



        // bundle 에서 id, userType, userDept, userMedal 값 가져오기
        id = arguments?.getString("id").toString()
        device_id = arguments?.getString("device_id").toString()
        nickname = arguments?.getString("nickname").toString()
        userType = arguments?.getString("userType").toString()
        userDept = arguments?.getString("userDept").toString()
        userMedal = arguments?.getInt("userMedal").toString()

        business_nickname.text = nickname

//        arguments?.let {
//            id = it.getString("id", "")
//            userType = it.getString("userType", "").toString()
//            userDept = it.getString("userDept", "").toString()
//            userMedal = it.getInt("userMedal", 0).toString()
//            nickname = it.getString("nickname", "").toString()
//        }

        val notificationPermissionCheck = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
        if (notificationPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10000
            )
        } else { //권한이 있는 경우
            Log.d("0-09123","hot notinoti")
        }

        notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("ontintno", "hot notinoti")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notification()
                }
            } else {
                Toast.makeText(context, "권한을 승인해야 HOT 게시물 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            Log.d("create", "Channel")
        }

        val detector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(p0: MotionEvent) {
            }

            override fun onSingleTapUp(p0: MotionEvent): Boolean {
                Log.d("onSingleTapUp", "onSingleTapUp")
                val address = "https://github.com/M-N-Project/Medi-Nion"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
                startActivity(intent)
                return false
            }

            override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
                return false
            }

            override fun onLongPress(p0: MotionEvent) {
            }

            override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
                return false
            }

        })
        ad_viewPager2 = view.findViewById(R.id.ad_viewPager)
        ad_viewPager2.adapter = ViewPager2Adapter_Ad(getAdImage())
        ad_viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        ad_viewPager2.getChildAt(0).setOnTouchListener { view, event ->
            detector.onTouchEvent(event)
            false
        }

        fun setPage() {
            if (currentPosition == 3)
                currentPosition = 0
            ad_viewPager2.setCurrentItem(currentPosition, true)
            currentPosition += 1
        }

        val handler = Handler(Looper.getMainLooper()) {
            setPage()
            true
        }


        class PagerRunnable : Runnable {
            override fun run() {
                while (true) {
                    try {
                        Thread.sleep(5000)
                        handler.sendEmptyMessage(0)
                    } catch (e: InterruptedException) {
                        Log.d("interrupt", "interrupt")
                    }
                }
            }
        }

        val thread = Thread(PagerRunnable())
        thread.start()


        big_ad_viewPager2 = view.findViewById(R.id.big_ad_viewPager2)
        big_ad_viewPager2.adapter = ViewPagerAdapter_BigAd(getBigAdImage())
        big_ad_viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        big_ad_viewPager2.getChildAt(0).setOnTouchListener { v, event ->
            detector.onTouchEvent(event)
            false
        }

        fun setBigPage() {
            if (big_currentPosition == 4)
                big_currentPosition = 0
            big_ad_viewPager2.setCurrentItem(big_currentPosition, true)
            big_currentPosition += 1
        }

        val handler1 = Handler(Looper.getMainLooper()) {
            setBigPage()
            true
        }


        class PagerRunnable1 : Runnable {
            override fun run() {
                while (true) {
                    try {
                        Thread.sleep(5000)
                        handler1.sendEmptyMessage(0)
                    } catch (e: InterruptedException) {
                        Log.d("interrupt", "interrupt")
                    }
                }
            }
        }

        val thread1 = Thread(PagerRunnable1())
        thread1.start()


///////////////////  즐겨찾는 게시판 클릭 이벤트 ////////////////////////////////////////////
        basicBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "자유 게시판")
                startActivity(intent)
            }
        }

        jobBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            }
        }

        secBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            }
        }

        marketBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "장터 게시판")
                startActivity(intent)
            }
        }

        qnaBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }

        acadamy_info.setOnClickListener {
            //학회 및 세미나 정보로 이동
        }

        acadamy_info.setOnClickListener{ // 학회 및 세미나 정보로 이동
            activity?.let {
                val intent = Intent(context, MedicalSeminar::class.java)
                startActivity(intent)
            }
        }

        employee_info.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let {
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        medi_news.setOnClickListener {
            //의료뉴스 이동
            activity?.let {
                val intent = Intent(context, MedicalNews::class.java)
                startActivity(intent)
            }
        }

        ////////////////////// hot 게시물 클릭 이벤트 ///////////////////////////////

        ////// 더보기 클릭했을 때 hot 게시물 보이게 하기 ////////
        val hotBasicMore = view.findViewById<TextView>(R.id.btn_hot_basic_more)
        val basic_more = view.findViewById<LinearLayout>(R.id.hot_detail_basic_more)
        val hotJobMore = view.findViewById<TextView>(R.id.btn_hot_job_more)
        val job_more = view.findViewById<LinearLayout>(R.id.hot_detail_job_more)
        val hotDeptMore = view.findViewById<TextView>(R.id.btn_hot_dept_more)
        val dept_more = view.findViewById<LinearLayout>(R.id.hot_detail_dept_more)

        hotBasicMore.setOnClickListener {
            activity?.let {
                if (basic_more.visibility == View.GONE) basic_more.visibility = View.VISIBLE
                else basic_more.visibility = View.GONE
            }
        }
        hotJobMore.setOnClickListener {
            activity?.let {
                if (job_more.visibility == View.GONE) job_more.visibility = View.VISIBLE
                else job_more.visibility = View.GONE
            }
        }
        hotDeptMore.setOnClickListener {
            activity?.let {
                if (dept_more.visibility == View.GONE) dept_more.visibility = View.VISIBLE
                else dept_more.visibility = View.GONE
            }
        }

        /////// 각 home_hot_detail 별로 상세 게시판으로 클릭 이벤트 넣기

        // 자유 게시판

        val hotDetailBasic1: View = view.findViewById(R.id.hot_detail_basic1)
        hotDetailBasic1.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailBasic1.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "자유 게시판"
                )
            }
        }
        val hotDetailBasic2: View = view.findViewById(R.id.hot_detail_basic2)
        hotDetailBasic2.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailBasic2.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "자유 게시판"
                )
            }
        }
        val hotDetailBasic3: View = view.findViewById(R.id.hot_detail_basic3)
        hotDetailBasic3.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailBasic3.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "자유 게시판"
                )
            }
        }

        // 직종별 게시판

        val hotDetailJob1: View = view.findViewById(R.id.hot_detail_job1)
        hotDetailJob1.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailJob1.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "직종별 게시판"
                )
            }
        }
        val hotDetailJob2: View = view.findViewById(R.id.hot_detail_job2)
        hotDetailJob2.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailJob2.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "직종별 게시판"
                )
            }
        }
        val hotDetailJob3: View = view.findViewById(R.id.hot_detail_job3)
        hotDetailJob3.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailJob3.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "직종별 게시판"
                )
            }
        }

        // 진료과별 게시판

        val hotDetailDept1: View = view.findViewById(R.id.hot_detail_dept1)
        hotDetailDept1.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailDept1.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "진료과별 게시판"
                )
            }
        }
        val hotDetailDept2: View = view.findViewById(R.id.hot_detail_dept2)
        hotDetailDept2.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailDept2.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "진료과별 게시판"
                )
            }
        }
        val hotDetailDept3: View = view.findViewById(R.id.hot_detail_dept3)
        hotDetailDept3.setOnClickListener {
            activity?.let {
                gotoBoardDetail(
                    hotDetailDept3.findViewById<TextView>(R.id.home_hot_num).text.toString(),
                    "진료과별 게시판"
                )
            }
        }
/*
        val BusiNewView = view.findViewById<View>(R.id.home_subsc_box)
        BusiNewView.setOnClickListener {
            //gotoProfile()
        }
*/
        return view
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification() {
        notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun getAdImage(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.ad1,
            R.drawable.ad2,
            R.drawable.ad3
        )
    }

    private fun getBigAdImage(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.big_ad1,
            R.drawable.big_ad2,
            R.drawable.big_ad3,
            R.drawable.big_ad4
        )
    }
    ///////////////////////// viewPager에 넣을 QnA 게시판 최신글 가져오는 fetch 함수 //////////////////////////////////////

    fun fetchNewQna() {
        val urlQnaNew = "http://seonho.dothome.co.kr/QnaNew_list.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        val id = arguments?.getString("id")
        val device_id = arguments?.getString("device_id")
        var nickname = arguments?.getString("nickname")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")
        val userMedal = arguments?.getInt("userMedal").toString()

        val request = Board_Request(
            Request.Method.POST,
            urlQnaNew,
            { response ->
                val jsonArray = JSONArray(response)
                qnaItems.clear()
                for (i in jsonArray.length() - 1 downTo 0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val board_time = item.getString("time")
                    val image = item.getString("image")
                    var heart = item.getInt("heart")
                    var comment = item.getInt("comment")
                    var bookmark = item.getInt("bookmark")

                    val newItem = qnaNewItem(num, title, content)
                    qnaItems.add(newItem)
                }

                // RecyclerView.Adapter<ViewHolder>()
                val adapter2 = PagerRecyclerAdapter(qnaItems)
                createBusinessChan_btn1.adapter = adapter2
                // ViewPager의 Paging 방향은 Horizontal
                createBusinessChan_btn1.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                //중첩 ViewPager이므로 스크롤 가능하게하기
                //createBusinessChan_btn1.isNestedScrollingEnabled = true

                var detailId: String = ""
                var detailTitle: String = ""
                var detailContent: String = ""
                var detailTime: String = ""
                var detailImg: String = ""


                //게시판 상세
                adapter2.setOnItemClickListener(object : PagerRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, data: qnaNewItem, pos: Int) {
                        Log.d("onItemClick", "override fun 실행")
                        val request = Login_Request(
                            Request.Method.POST,
                            urlDetail,
                            { response ->
                                if (response != "Detail Info Error") {
                                    val jsonArray = JSONArray(response)
                                    qnaItems.clear()
                                    for (i in jsonArray.length() - 1 downTo 0) {
                                        val item = jsonArray.getJSONObject(i)

                                        detailId = item.getString("id")
                                        detailTitle = item.getString("title")
                                        detailContent = item.getString("content")
                                        detailTime = item.getString("time")
                                        detailImg = item.getString("image")

                                        val intent = Intent(
                                            activity?.applicationContext,
                                            BoardDetail::class.java
                                        )
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                                        intent.putExtra("board", "QnA 게시판")
                                        intent.putExtra("num", data.num)
                                        intent.putExtra("id", id)
                                        intent.putExtra("device_id", device_id)
                                        intent.putExtra("nickname", nickname)
                                        intent.putExtra("writerId", detailId)

                                        intent.putExtra("title", detailTitle)
                                        intent.putExtra("content", detailContent)
                                        intent.putExtra("time", detailTime)
                                        intent.putExtra("image", detailImg)
                                        intent.putExtra("userType", userType)
                                        intent.putExtra("userDept", userDept)
                                        intent.putExtra("userMedal", userMedal)
                                        startActivity(intent)
                                    }


                                }

                            }, {
                                Log.d(
                                    "login failed", "error......${
                                        activity?.applicationContext?.let { it1 ->
                                            error(
                                                it1
                                            )
                                        }
                                    }"
                                )
                            },
                            hashMapOf(
                                "board" to "QnA 게시판",
                                "post_num" to data.num.toString()
                            )
                        )
                        val queue = Volley.newRequestQueue(activity?.applicationContext)
                        queue.add(request)
                    }

                })


            }, { Log.d("login failed", "error......${activity?.applicationContext}") },
            hashMapOf(

            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)

    }

    ///////////////////////// hot 게시물 가져오는 fetch 함수 ///////////////////////////////
    private fun fetchHotPost() {
        val urlhotpost = "http://seonho.dothome.co.kr/Hot_post_list.php"
        val receiverIntent: Intent = Intent(
            context,
            AlarmReceiver_hot::class.java
        )
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context,
                ALARM_REQUEST_CODE, receiverIntent,
                PendingIntent.FLAG_MUTABLE
            )
        val alarmManager = this.getActivity()?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val id = arguments?.getString("id").toString()
        var nickname = arguments?.getString("nickname").toString()
        val userType = arguments?.getString("userType").toString()
        val userDept = arguments?.getString("userDept").toString()
        //Log.d("user", "$userType $userDept")

        // include 했던 항목들
        val includeBasic = ArrayList<View>()
        view?.findViewById<View>(R.id.hot_detail_basic1)?.let { includeBasic.add(it) }
        view?.findViewById<View>(R.id.hot_detail_basic2)?.let { includeBasic.add(it) }
        view?.findViewById<View>(R.id.hot_detail_basic3)?.let { includeBasic.add(it) }

        val includeJob = ArrayList<View>()
        view?.findViewById<View>(R.id.hot_detail_job1)?.let { includeJob.add(it) }
        view?.findViewById<View>(R.id.hot_detail_job2)?.let { includeJob.add(it) }
        view?.findViewById<View>(R.id.hot_detail_job3)?.let { includeJob.add(it) }

        val includeDept = ArrayList<View>()
        view?.findViewById<View>(R.id.hot_detail_dept1)?.let { includeDept.add(it) }
        view?.findViewById<View>(R.id.hot_detail_dept2)?.let { includeDept.add(it) }
        view?.findViewById<View>(R.id.hot_detail_dept3)?.let { includeDept.add(it) }

        var includeView: View

        val basicrequest = Board_Request(
            Request.Method.POST,
            urlhotpost,
            { response ->
                val jsonArray = JSONArray(response)
                for (i in jsonArray.length() - 1 downTo 0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val board_time = item.getString("time")
                    val image = item.getString("image")
                    var heart = item.getInt("heart")
                    var comment = item.getInt("comment")
                    var bookmark = item.getInt("bookmark")

                    includeView = includeBasic.removeAt(0)
                    includeView.findViewById<TextView>(R.id.home_hot_num).text =
                        num.toString()
                    includeView.findViewById<TextView>(R.id.business_home_title).text = title
                    includeView.findViewById<TextView>(R.id.home_hot_like).text =
                        heart.toString()
                    includeView.findViewById<TextView>(R.id.home_hot_comm).text =
                        comment.toString()
                }
                val jobrequest = Board_Request(
                    Request.Method.POST,
                    urlhotpost,
                    { response ->
                        val jsonArray = JSONArray(response)
                        for (i in jsonArray.length() - 1 downTo 0) {
                            val item = jsonArray.getJSONObject(i)

                            val num = item.getInt("num")
                            val title = item.getString("title")
                            val content = item.getString("content")
                            val board_time = item.getString("time")
                            val image = item.getString("image")
                            var heart = item.getInt("heart")
                            var comment = item.getInt("comment")
                            var bookmark = item.getInt("bookmark")

                            includeView = includeJob.removeAt(0)
                            includeView.findViewById<TextView>(R.id.home_hot_num).text =
                                num.toString()
                            includeView.findViewById<TextView>(R.id.business_home_title).text = title
                            includeView.findViewById<TextView>(R.id.home_hot_like).text =
                                heart.toString()
                            includeView.findViewById<TextView>(R.id.home_hot_comm).text =
                                comment.toString()
                        }
                        val deptrequest = Board_Request(
                            Request.Method.POST,
                            urlhotpost,
                            { response ->
                                val jsonArray = JSONArray(response)
                                for (i in jsonArray.length() - 1 downTo 0) {
                                    val item = jsonArray.getJSONObject(i)

                                    val num = item.getInt("num")
                                    val title = item.getString("title")
                                    val content = item.getString("content")
                                    val board_time = item.getString("time")
                                    val image = item.getString("image")
                                    var heart = item.getInt("heart")
                                    var comment = item.getInt("comment")
                                    var bookmark = item.getInt("bookmark")

                                    // textView.text 수정
                                    includeView = includeDept.removeAt(0)
                                    includeView.findViewById<TextView>(R.id.home_hot_num).text =
                                        num.toString()
                                    includeView.findViewById<TextView>(R.id.business_home_title).text = title
                                    includeView.findViewById<TextView>(R.id.home_hot_like).text =
                                        heart.toString()
                                    includeView.findViewById<TextView>(R.id.home_hot_comm).text =
                                        comment.toString()
                                }
                            },
                            {
                                Log.d(
                                    "hot fetch failed",
                                    "error......${activity?.applicationContext}"
                                )
                            },
                            hashMapOf(
                                "board" to "진료과별 게시판",
                                "userType" to userType,
                                "userDept" to userDept
                            )
                        )
                        val queue = Volley.newRequestQueue(activity?.applicationContext)
                        queue.add(deptrequest)

                    }, { Log.d("hot fetch failed", "error......${activity?.applicationContext}") },
                    hashMapOf(
                        "board" to "직종별 게시판",
                        "userType" to userType,
                        "userDept" to userDept
                    )
                )
                val queue = Volley.newRequestQueue(context)
                queue.add(jobrequest)

            }, { Log.d("hot fetch failed", "error......${activity?.applicationContext}") },
            hashMapOf(
                "board" to "자유 게시판",
                "userType" to userType,
                "userDept" to userDept
            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(basicrequest)

    }

    /////////////////////// hot 게시물 클릭이벤트에 이어지는 함수 //////////////////////////////////////////// /
    ////////////////////// board detail 화면으로 넘어감 /////////////////////////////////////////////
    private fun gotoBoardDetail(post_num: String, board: String) {
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        var detailId: String = ""
        var detailTitle: String = ""
        var detailContent: String = ""
        var detailTime: String = ""
        var detailImg: String = ""

        val request = Login_Request(
            Request.Method.POST,
            urlDetail,
            { response ->
                if (response != "Detail Info Error") {
                    val jsonArray = JSONArray(response)
                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)

                        detailId = item.getString("id")
                        detailTitle = item.getString("title")
                        detailContent = item.getString("content")
                        detailTime = item.getString("time")
                        detailImg = item.getString("image")

                        Log.d("gotoDetail", "$board $post_num")

                        val intent = Intent(activity?.applicationContext, BoardDetail::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //인텐트 플래그 설정
                        intent.putExtra("board", board)
                        intent.putExtra("num", post_num)
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("writerId", detailId)
                        intent.putExtra("title", detailTitle)
                        intent.putExtra("content", detailContent)
                        intent.putExtra("time", detailTime)
                        intent.putExtra("image", detailImg)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("device_id", device_id)
                        startActivity(intent)
                    }
                }
            }, {
                Log.d(
                    "gotoDetail failed", "error......${
                        activity?.applicationContext?.let { it1 ->
                            error(
                                it1
                            )
                        }
                    }"
                )
            },
            hashMapOf(
                "board" to board,
                "post_num" to post_num
            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)
    }

    //////////////////////////////////// 구독채널 새소식 ////////////////////////////////////////////////////////////

    private fun fetchNewBusi() {
        val urlBusiNew = "http://seonho.dothome.co.kr/HomeNewBusi.php"
        val appUser = arguments?.getString("id").toString()

        var newBusiItems = ArrayList<HomeNewRecyclerItem>()

        val request = Board_Request(
            Request.Method.POST,
            urlBusiNew,
            { response ->
                Log.d("비즈니스새소식새소식새소식", response)
                if(response != "No NewBusi"){
                    val jsonArray = JSONArray(response)
                    newBusiItems.clear()
                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)
                        val chanName = item.getString("channel_name")
                        val writerId = item.getString("id")
                        val title = item.getString("title")
                        val content = item.getString("content")

                        val newItem = HomeNewRecyclerItem(chanName, writerId, title, content)
                        newBusiItems.add(newItem)
                    }

                    // RecyclerView.Adapter<ViewHolder>()
                    val adapter2 = HomeNewRecyclerAdapter(newBusiItems)
                    homeBusiNew.adapter = adapter2
                    // ViewPager의 Paging 방향은 Horizontal
                    homeBusiNew.orientation = ViewPager2.ORIENTATION_HORIZONTAL

                    var detailId: String = ""
                    var detailTitle: String = ""
                    var detailContent: String = ""
                    var detailTime: String = ""
                    var detailImg: String = ""


                    //게시판 상세
                    adapter2.setOnItemClickListener(object : HomeNewRecyclerAdapter.OnItemClickListener {
                        override fun onItemClick(v: View, data: HomeNewRecyclerItem, pos: Int) {
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

            }, { Log.d("login failed", "error......${activity?.applicationContext}") },
            hashMapOf(
                "id" to appUser
            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)
    }
    /*
    private fun fetchNewBusi2() {
        val urlBusiNew = "http://seonho.dothome.co.kr/HomeNewBusi.php"
        val id = arguments?.getString("id").toString()

        val chanNameView = view?.findViewById<TextView>(R.id.home_business_title)
        val contentView = view?.findViewById<TextView>(R.id.home_business_detail)
        val chanImg = view?.findViewById<ImageView>(R.id.imageView6)

        val request = Login_Request(
            Request.Method.POST,
            urlBusiNew,
            {
                response->
                if(!response.equals("No NewBusi")){
                    val jsonArray = JSONArray(response)
                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)
                        val chanName = item.getString("channel_name")
                        val writerId = item.getString("id")
                        val imgUrl = "http://seonho.dothome.co.kr/images/businessProfile/${writerId}BusinessProfile.jpg"

                        if (chanNameView != null) {
                            chanNameView.text = chanName
                        }
                        if (contentView != null) {
                            contentView.text = item.getString("content")
                        }

                        if (chanImg != null) {
                            val task = ImageLoadTask(imgUrl, chanImg)
                            task.execute()
                            roundAll(chanImg, 100.0f)
                        }
                    }
                } else{
                    if (chanNameView != null) {
                        chanNameView.text = "비즈니스 채널 구독을 시작해보세요!"
                    }
                    if (contentView != null) {
                        contentView.text = ""
                    }
                    if (chanImg != null) {
                        chanImg.visibility = View.GONE
                    }
                }
            }, {
                Log.d(
                    "failed", "error......${
                        activity?.applicationContext?.let { it1 ->
                            error(
                                it1
                            )
                        }
                    }"
                )
            },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        queue.add(request)
    }

     */

    ////////////////// 비즈니스 새소식 클릭하면 profile로 //////////////////////
    private fun gotoProfile(chanName: String) {
        val intent =
            Intent(
                context,
                BusinessProfileActivity::class.java
            )
        var appUser = arguments?.getString("id").toString()
        intent.putExtra("appUser", appUser)
        intent.putExtra(
            "channel_name",
            chanName
        )
        startActivity(intent)

    }

    private var hotListItems = ArrayList<BusinessHotListItem>()

    fun fetchHotProfile() {
        var appUser = arguments?.getString("id").toString()
        val urlHotProfile = "http://seonho.dothome.co.kr/Business_profileHot_list.php"
        val urlRandProfile = "http://seonho.dothome.co.kr/Business_profileNew_list.php"
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
                        var writerId = item.getString("id")
                        val chanProfile = item.getString("channel_profile_img")

                        val HotListItem = BusinessHotListItem(chanName, chanProfile)
                        hotListItems.add(HotListItem)

                    }
                    hotListItems.reverse()
                    var hotAdapter = BusinessHotListAdapter(hotListItems)
                    hotAdapter.notifyDataSetChanged()
                    BusinessHotRecycler.adapter = hotAdapter

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
                                hotAdapter.notifyDataSetChanged()
                                BusinessHotRecycler.adapter = hotAdapter

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
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    fun roundAll(iv: ImageView, curveRadius: Float): ImageView {

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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(alarm_code: Int, content: String){
        context?.let { AlarmFunctions_hot(it).callAlarm(alarmCode, content) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        manager = this.getActivity()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
        )

    }
}