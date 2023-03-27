package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.home_qna.*
import org.json.JSONArray

class qnaNewItem(val num:Int, val title:String, val content:String) {
}
var qnaItems = java.util.ArrayList<qnaNewItem>()

class HomeFragment : Fragment(R.layout.home) { //피드 보여주는 홈화면 프레그먼트(게시판 구분은 추후에)

    class PagerRecyclerAdapter(private val qnaItem: java.util.ArrayList<qnaNewItem>) : RecyclerView.Adapter<PagerRecyclerAdapter.PagerViewHolder>() {

        inner class PagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val qna_title: TextView = itemView.findViewById(R.id.home_qna_title)
            private val qna_content: TextView = itemView.findViewById(R.id.home_qna_detail)
            private val qna_box = itemView.findViewById<Button>(R.id.detail_box_hot)
            fun bind(item: qnaNewItem) {
                qna_title.text = item.title
                qna_content.text = item.content

                val pos = absoluteAdapterPosition
                if(pos!= RecyclerView.NO_POSITION)
                {
                    qna_box.setOnClickListener {
                        listener?.onItemClick(itemView,item,pos)
                    }
                    qna_title.setOnClickListener {
                        listener?.onItemClick(itemView,item,pos)
                    }
                    qna_content.setOnClickListener {
                        listener?.onItemClick(itemView,item,pos)
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
            holder.bind(qnaItem[4-safePosition])
        }

        override fun getItemCount(): Int = 5

        interface OnItemClickListener{
            fun onItemClick(v:View, data: qnaNewItem, pos : Int)
        }
        private var listener : OnItemClickListener? = null
        fun setOnItemClickListener(listener: PagerRecyclerAdapter.OnItemClickListener) {
            this.listener = listener
        }
    }

    override fun onStart() {
        super.onStart()
        fetchNewQna()
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

        // bundle 에서 id, userType, userDept 값 가져오기
        val id = arguments?.getString("id")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

///////////////////  즐겨찾는 게시판 클릭 이벤트 ////////////////////////////////////////////
        basicBoard.setOnClickListener {
            Log.d("aaaa", "aabb")
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "자유 게시판")
                startActivity(intent)
            }
        }

        jobBoard.setOnClickListener {
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            }
        }

        secBoard.setOnClickListener {
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            }
        }

        marketBoard.setOnClickListener {
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "장터 게시판")
                startActivity(intent)
            }
        }

        qnaBoard.setOnClickListener {
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }

        acadamy_info.setOnClickListener {
            //학회 및 세미나 정보로 이동
        }

        employee_info.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let{
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        medi_news.setOnClickListener {
            //의료뉴스 이동
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
                for (i in jsonArray.length()-1  downTo  0) {
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
                viewPager.adapter = adapter2
                // ViewPager의 Paging 방향은 Horizontal
                viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

                var detailId : String = ""
                var detailTitle : String = ""
                var detailContent : String = ""
                var detailTime : String = ""
                var detailImg : String = ""


                //게시판 상세
                adapter2.setOnItemClickListener(object : PagerRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, data: qnaNewItem, pos: Int) {
                        Log.d("onItemClick", "override fun 실행")
                        val request = Login_Request(
                            Request.Method.POST,
                            urlDetail,
                            { response ->
                                if(response!="Detail Info Error"){
                                    val jsonArray = JSONArray(response)
                                    qnaItems.clear()
                                    for (i in jsonArray.length()-1  downTo  0) {
                                        val item = jsonArray.getJSONObject(i)

                                        detailId = item.getString("id")
                                        detailTitle = item.getString("title")
                                        detailContent = item.getString("content")
                                        detailTime = item.getString("time")
                                        detailImg = item.getString("image")

                                        val intent = Intent(activity?.applicationContext, BoardDetail::class.java)
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

                            }, { Log.d("login failed", "error......${activity?.applicationContext?.let { it1 ->
                                error(
                                    it1
                                )
                            }}") },
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
}