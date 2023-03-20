package com.example.medi_nion

    import android.annotation.SuppressLint
    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.view.animation.Animation
    import android.view.animation.AnimationUtils
    import android.widget.Button
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.navigation.fragment.findNavController
    import com.example.medi_nion.databinding.BottomMenuBinding
    import com.example.medi_nion.databinding.ProfileBinding
    import kotlinx.android.synthetic.main.bottom_menu.*

class MenuFragment : Fragment(R.layout.bottom_menu) { //menu 창으로 이동하는 프레그먼트

    private lateinit var allBoard: Button
    private lateinit var basicBoard: Button
    private lateinit var jobBoard: Button
    private lateinit var secBoard: Button
    private lateinit var hosBoard: Button
    private lateinit var dealBoard: Button
    private lateinit var qnaBtn: Button
    private lateinit var academyBtn: Button
    private lateinit var employeeInfoBtn: Button
    private lateinit var hospitalHomePageBtn: Button
    private lateinit var medicalNewsBtn: Button

    private lateinit var manageBusinessBtn: Button
    private lateinit var allBoardDetail: LinearLayout

    private lateinit var basicBoardTextView: TextView
    private lateinit var marketBoardTextView: TextView
    private lateinit var QnABoardTextView: TextView
    private lateinit var jobBoardTextView: TextView
    private lateinit var deptBoardTextView: TextView


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bundle 에서 id, userType, userDept 값 가져오기
        val id = arguments?.getString("id")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

        allBoard = view.findViewById(R.id.menu_All) // 전체 게시판
        basicBoard = view.findViewById(R.id.menu_basic) // 자유 게시판
        jobBoard = view.findViewById(R.id.menu_job) //직종별 게시판
        secBoard = view.findViewById(R.id.menu_dept) //진료과별 게시판
        dealBoard = view.findViewById(R.id.menu_market) // 장터 게시판
        allBoardDetail = view.findViewById(R.id.Allboard_Detail)


        qnaBtn = view.findViewById(R.id.menu_qna)

        academyBtn = view.findViewById(R.id.menu7)
        employeeInfoBtn = view.findViewById(R.id.menu8)
        hospitalHomePageBtn = view.findViewById(R.id.menu9)
        medicalNewsBtn = view.findViewById(R.id.menu_news)
        manageBusinessBtn = view.findViewById(R.id.menu_buss)

        basicBoardTextView = view.findViewById(R.id.basicboard_TextView)
        marketBoardTextView = view.findViewById(R.id.marketboard_TextView)
        QnABoardTextView = view.findViewById(R.id.QnAboard_TextView)
        jobBoardTextView = view.findViewById(R.id.jobboard_TextView)
        deptBoardTextView = view.findViewById(R.id.deptboard_TextView)

        allBoard.setOnClickListener { //전체 게시판으로 이동함
            activity?.let{
                allBoardDetail.visibility = View.VISIBLE
                basicBoardTextView.setOnClickListener {
                    var intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("board", "자유 게시판")
                    startActivity(intent)
                }
                marketBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("board", "장터 게시판")
                    startActivity(intent)
                }
                QnABoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("board", "QnA 게시판")
                    startActivity(intent)
                }
                jobBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("userType", userType)
                    intent.putExtra("userDept", userDept)
                    intent.putExtra("board", "직종별 게시판")
                    startActivity(intent)
                }
                deptBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    Log.d("userSearch", "$userType  $userDept")
                    intent.putExtra("id", id)
                    intent.putExtra("userType", userType)
                    intent.putExtra("userDept", userDept)
                    intent.putExtra("board", "진료과별 게시판")
                    startActivity(intent)
                }
            }
        }


        employeeInfoBtn.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let{
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        basicBoard.setOnClickListener { //자유 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "자유 게시판")
                startActivity(intent)
            }
        }
        jobBoard.setOnClickListener { //직종별 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            }
        }
        secBoard.setOnClickListener { //진료과별 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            }
        }
        dealBoard.setOnClickListener { //장터 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "장터 게시판")
                startActivity(intent)
            }
        }

        qnaBtn.setOnClickListener { //게시판으로 이동함 -> 추후 상의 (어떤 형식의 게시판으로 할지)
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }


        manageBusinessBtn.setOnClickListener { //비즈니스 관리(프로필수정, 글쓰기 화면으로 이동)
            activity?.let{
                val intent = Intent(context, BusinessManageFirstActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        }
    }
}