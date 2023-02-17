package com.example.medi_nion

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.Button
    import androidx.fragment.app.Fragment

class MenuFragment : Fragment(R.layout.bottom_menu) { //menu 창으로 이동하는 프레그먼트

    private lateinit var qnaBtn: Button
    private lateinit var basicBoard: Button
    private lateinit var academyBtn: Button
    private lateinit var employeeInfoBtn: Button
    private lateinit var hospitalHomePageBtn: Button
    private lateinit var medicalNewsBtn: Button
    private lateinit var manageBusinessBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basicBoard = view.findViewById(R.id.menu1)
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
                startActivity(intent)
            }
        }

        manageBusinessBtn.setOnClickListener { //비즈니스 관리(프로필수정, 글쓰기 화면으로 이동)
            activity?.let{
                val intent = Intent(context, BusinessManageActivity::class.java)
                startActivity(intent)
            }
        }

    }
}