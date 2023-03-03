package com.example.medi_nion

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Button
    import androidx.fragment.app.Fragment
    import kotlinx.android.synthetic.main.login.*

class MenuFragment : Fragment(R.layout.bottom_menu) { //menu 창으로 이동하는 프레그먼트

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("id")

        basicBoard = view.findViewById(R.id.menu1) // 전체 게시판
        jobBoard = view.findViewById(R.id.menu2) //직종별 게시판
        secBoard = view.findViewById(R.id.menu3) //진료과별 게시판
        hosBoard = view.findViewById(R.id.menu4) //우리 병원 게시판
        dealBoard = view.findViewById(R.id.menu5) // 장터 게시판

        qnaBtn = view.findViewById(R.id.menu6)

        academyBtn = view.findViewById(R.id.menu7)
        employeeInfoBtn = view.findViewById(R.id.menu8)
        hospitalHomePageBtn = view.findViewById(R.id.menu9)
        medicalNewsBtn = view.findViewById(R.id.menu10)
        manageBusinessBtn = view.findViewById(R.id.menu11)

        employeeInfoBtn.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let{
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        basicBoard.setOnClickListener { //게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "전체 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }
        jobBoard.setOnClickListener { //게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "직종별 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }
        secBoard.setOnClickListener { //게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "진료과별 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }
        hosBoard.setOnClickListener { //게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "우리 병원 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }
        dealBoard.setOnClickListener { //게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "장터 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }

        qnaBtn.setOnClickListener { //게시판으로 이동함 -> 추후 상의 (어떤 형식의 게시판으로 할지)
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("board", "QnA 게시판")
                Log.d("Menu id", "$id")
                startActivity(intent)
            }
        }






        manageBusinessBtn.setOnClickListener { //비즈니스 관리(프로필수정, 글쓰기 화면으로 이동)
            activity?.let{
                val intent = Intent(context, BusinessManageActivity::class.java)
                intent.putExtra("id", id)
                Log.d("business id", "$id")
                startActivity(intent)
            }
        }
    }
}