package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MenuFragment : Fragment(R.layout.bottom_menu) { //menu 창으로 이동하는 프레그먼트

    private lateinit var allBoard: Button
    private lateinit var basicBoard: Button
    private lateinit var jobBoard: Button
    private lateinit var secBoard: Button
    private lateinit var dealBoard: Button
    private lateinit var qnaBtn: Button
    private lateinit var academyBtn: Button
    private lateinit var employeeInfoBtn: Button
    private lateinit var medicalNewsBtn: Button
    private lateinit var scheduleBtn: Button
    private lateinit var manageBusinessBtn: Button

    @SuppressLint("ResourceType", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bundle 에서 id, userType, userDept 값 가져오기]
        val id = arguments?.getString("id")
        val nickname = arguments?.getString("nickname")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")
        val userMedal = arguments?.getInt("userMedal")
        val device_id = arguments?.getString("device_id").toString()

        Log.d("menuFragment", "id: $id")
        Log.d("menuFragment", "nickname: $nickname")
        Log.d("menuFragment", "userType: $userType")
        Log.d("menuFragment", "userDept: $userDept")
        Log.d("menuFragment", "userMedal: $userMedal")


        allBoard = view.findViewById(R.id.menu_All) // 전체 게시판
        basicBoard = view.findViewById(R.id.menu_basic) // 자유 게시판
        jobBoard = view.findViewById(R.id.menu_job) //직종별 게시판
        secBoard = view.findViewById(R.id.menu_dept) //진료과별 게시판
        dealBoard = view.findViewById(R.id.menu_market) // 장터 게시판


        qnaBtn = view.findViewById(R.id.menu_qna)

        academyBtn = view.findViewById(R.id.menu7)
        employeeInfoBtn = view.findViewById(R.id.menu8)
        medicalNewsBtn = view.findViewById(R.id.medi_news)
        manageBusinessBtn = view.findViewById(R.id.menu_buss)
        scheduleBtn = view.findViewById(R.id.menu_schedule)


        allBoard.setOnClickListener { //전체 게시판으로 이동함
            activity?.let {
                val intent = Intent(context, Menu_All::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                startActivity(intent)
            }
        }

        academyBtn.setOnClickListener { //학회 및 세미나로 이동
            activity?.let {
                val intent = Intent(context, MedicalSeminar::class.java)
                startActivity(intent)
            }
        }

        employeeInfoBtn.setOnClickListener { //병원 프로필 및 채용 정보로 이동함
            activity?.let{
                val intent = Intent(context, HospitalProfile::class.java)
                startActivity(intent)
            }
        }

        medicalNewsBtn.setOnClickListener {  //의료뉴스로 이동
            activity?.let {
                val intent = Intent(context, MedicalNews::class.java)
                startActivity(intent)
            }
        }

        basicBoard.setOnClickListener { //자유 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "자유 게시판")
                startActivity(intent)
            }
        }
        jobBoard.setOnClickListener { //직종별 게시판으로 이동함
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
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
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
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
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "장터 게시판")
                startActivity(intent)
            }
        }

        qnaBtn.setOnClickListener { //게시판으로 이동함 -> 추후 상의 (어떤 형식의 게시판으로 할지)
            activity?.let{
                val intent = Intent(context, Board::class.java)
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }

        scheduleBtn.setOnClickListener {
            val scheduleFragment = CalendarFragment()
            val activity = requireActivity()
            val viewPager = activity.findViewById<ViewPager2>(R.id.linearLayout)
            val currentPosition = viewPager.currentItem
            val newPosition = currentPosition - 1
            viewPager.setCurrentItem(newPosition, false)
            viewPager.adapter?.notifyDataSetChanged()
        }

        manageBusinessBtn.setOnClickListener {
            val businessFragment = BusinessMainFragment()
            val bundle = Bundle().apply {
                putString("id", id)
                putString("nickname", nickname)
            }
            businessFragment.arguments = bundle
            val activity = requireActivity()
            val viewPager = activity.findViewById<ViewPager2>(R.id.linearLayout)
            val currentPosition = viewPager.currentItem
            val newPosition = currentPosition + 1
            viewPager.setCurrentItem(newPosition, false)
            viewPager.adapter?.notifyDataSetChanged()
        }

    }
}