package com.example.medi_nion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.home_qna.*
import org.json.JSONArray

class qnaNewItem(val num: Int, val title: String, val content: String) {
}

var qnaItems = java.util.ArrayList<qnaNewItem>()

class HomeFragment : Fragment(R.layout.home) { //피드 보여주는 홈화면 프레그먼트(게시판 구분은 추후에)

    private lateinit var id: String
    private lateinit var userType: String
    private lateinit var userDept: String
    private lateinit var userGrade: String

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

    override fun onStart() {
        super.onStart()
        fetchNewQna()
        fetchHotPost()
    }

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

        val imageView_ad = view.findViewById<ImageView>(R.id.imageView_ad)
        val imageView_ad2 = view.findViewById<ImageView>(R.id.imageView_ad2)

        // bundle 에서 id, userType, userDept 값 가져오기
        id = arguments?.getString("id").toString()
        userType = arguments?.getString("userType").toString()
        userDept = arguments?.getString("userDept").toString()
        userGrade = arguments?.getString("userGrade").toString()


        imageView_ad.setOnClickListener {
            val address = "https://www.tripstore.kr/travels/b1653a2c-57ad-4f45-998b-7d79296dd444"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
            startActivity(intent)
        }

        imageView_ad2.setOnClickListener {
            val address = "https://www.tripstore.kr/travels/720f181c-42db-46b9-a94d-060ca6691fcd?regionSub=%EB%8C%80%EB%A7%8C&path=detail"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
            startActivity(intent)
        }

///////////////////  즐겨찾는 게시판 클릭 이벤트 ////////////////////////////////////////////
        basicBoard.setOnClickListener {
            Log.d("aaaa", "aabb")
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userGrade", userGrade)
                intent.putExtra("board", "자유 게시판")
                startActivity(intent)
            }
        }

        jobBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("userGrade", userGrade)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            }
        }

        secBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("userGrade", userGrade)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            }
        }

        marketBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userGrade", userGrade)
                intent.putExtra("board", "장터 게시판")
                startActivity(intent)
            }
        }

        qnaBoard.setOnClickListener {
            activity?.let {
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userGrade", userGrade)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }

        acadamy_info.setOnClickListener {
            //학회 및 세미나 정보로 이동
        }

        employee_info.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let {
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        medi_news.setOnClickListener {
            //의료뉴스 이동
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

        return view
    }
/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchNewQna()

    }
*/
    ///////////////////////// viewPager에 넣을 QnA 게시판 최신글 가져오는 fetch 함수 //////////////////////////////////////

    fun fetchNewQna() {
        val urlQnaNew = "http://seonho.dothome.co.kr/QnaNew_list.php"
        val urlDetail = "http://seonho.dothome.co.kr/postInfoDetail.php"

        val id = arguments?.getString("id")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

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
                                        intent.putExtra("writerId", detailId)
                                        intent.putExtra("title", detailTitle)
                                        intent.putExtra("content", detailContent)
                                        intent.putExtra("time", detailTime)
                                        intent.putExtra("image", detailImg)
                                        intent.putExtra("userType", userType)
                                        intent.putExtra("userDept", userDept)
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

        val id = arguments?.getString("id").toString()
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
                    includeView.findViewById<TextView>(R.id.home_hot).text = title
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
                            includeView.findViewById<TextView>(R.id.home_hot).text = title
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
                                    includeView.findViewById<TextView>(R.id.home_hot).text = title
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
                        val queue = Volley.newRequestQueue(context)
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
        val queue = Volley.newRequestQueue(context)
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
                        intent.putExtra("writerId", detailId)
                        intent.putExtra("title", detailTitle)
                        intent.putExtra("content", detailContent)
                        intent.putExtra("time", detailTime)
                        intent.putExtra("image", detailImg)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
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
}