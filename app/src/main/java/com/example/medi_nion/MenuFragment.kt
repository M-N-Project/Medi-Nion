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
    import androidx.fragment.app.Fragment

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
    private lateinit var scheduleBtn: Button

    private lateinit var manageBusinessBtn: Button
    private lateinit var allBoardDetail: LinearLayout

    private lateinit var basicBoardTextView: TextView
    private lateinit var marketBoardTextView: TextView
    private lateinit var QnABoardTextView: TextView
    private lateinit var jobBoardTextView: TextView
    private lateinit var deptBoardTextView: TextView

    private lateinit var doctorTextView: TextView
    private lateinit var nurseTextView: TextView
    private lateinit var engineerTextView: TextView
    private lateinit var officeTextView: TextView
    private lateinit var studentTextView: TextView

    private lateinit var internalTextView: TextView
    private lateinit var sugcialTextView: TextView
    private lateinit var machuiTextView: TextView
    private lateinit var pathologyTextView: TextView
    private lateinit var urologyTextView: TextView
    private lateinit var pregnancyTextView: TextView
    private lateinit var cosmeticTextView: TextView
    private lateinit var childTextView: TextView
    private lateinit var neuroTextView: TextView
    private lateinit var eyeTextView: TextView
    private lateinit var videoTextView: TextView
    private lateinit var urgentTextView: TextView
    private lateinit var earTextView: TextView
    private lateinit var psyTextView: TextView
    private lateinit var boneTextView: TextView
    private lateinit var toothTextView: TextView
    private lateinit var skinTextView: TextView
    private lateinit var orientalTextView: TextView
    private lateinit var etcDeptTextView: TextView


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bundle 에서 id, userType, userDept 값 가져오기
        val id = arguments?.getString("id")
        val nickname = arguments?.getString("nickname")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")
        val userMedal = arguments?.getInt("userMedal")

        allBoard = view.findViewById(R.id.menu_All) // 전체 게시판
        basicBoard = view.findViewById(R.id.menu_basic) // 자유 게시판
        jobBoard = view.findViewById(R.id.menu_job) //직종별 게시판
        secBoard = view.findViewById(R.id.menu_dept) //진료과별 게시판
        dealBoard = view.findViewById(R.id.menu_market) // 장터 게시판
        allBoardDetail = view.findViewById(R.id.Allboard_Detail)


        qnaBtn = view.findViewById(R.id.menu_qna)

        academyBtn = view.findViewById(R.id.menu7)
        employeeInfoBtn = view.findViewById(R.id.menu8)
        medicalNewsBtn = view.findViewById(R.id.menu_news)
        manageBusinessBtn = view.findViewById(R.id.menu_buss)
        scheduleBtn = view.findViewById(R.id.menu_schedule)

        basicBoardTextView = view.findViewById(R.id.basicboard_TextView)
        marketBoardTextView = view.findViewById(R.id.marketboard_TextView)
        QnABoardTextView = view.findViewById(R.id.QnAboard_TextView)
        jobBoardTextView = view.findViewById(R.id.jobboard_TextView)
        deptBoardTextView = view.findViewById(R.id.deptboard_TextView)

        //직종 텍스트 뷰 >> 클릭이벤트 연결 (접근제한)
        doctorTextView = view.findViewById<TextView>(R.id.doctor_TextView)
        nurseTextView = view.findViewById<TextView>(R.id.nurse_TextView)
        engineerTextView = view.findViewById<TextView>(R.id.engineer_TextView)
        officeTextView = view.findViewById<TextView>(R.id.office_TextView)
        studentTextView = view.findViewById<TextView>(R.id.student_TextView)

        // 진료과 텍스트뷰 >> 클릭이벤트 연결 (접근제한)
        internalTextView = view.findViewById<TextView>(R.id.internal_TextView)
        sugcialTextView = view.findViewById<TextView>(R.id.surgical_TextView)
        machuiTextView = view.findViewById<TextView>(R.id.machui_TextView)
        pathologyTextView = view.findViewById<TextView>(R.id.pathology_TextView)
        urologyTextView = view.findViewById<TextView>(R.id.urology_TextView)
        pregnancyTextView = view.findViewById<TextView>(R.id.pregnancy_TextView)
        cosmeticTextView = view.findViewById<TextView>(R.id.cosmetic_TextView)
        childTextView = view.findViewById<TextView>(R.id.child_TextView)
        neuroTextView = view.findViewById<TextView>(R.id.neuro_TextView)
        eyeTextView = view.findViewById<TextView>(R.id.eye_TextView)
        videoTextView = view.findViewById<TextView>(R.id.video_TextView)
        urgentTextView = view.findViewById<TextView>(R.id.urgent_TextView)
        earTextView = view.findViewById<TextView>(R.id.ear_TextView)
        psyTextView = view.findViewById<TextView>(R.id.psy_TextView)
        boneTextView = view.findViewById<TextView>(R.id.bone_TextView)
        toothTextView = view.findViewById<TextView>(R.id.tooth_TextView)
        skinTextView = view.findViewById<TextView>(R.id.skin_TextView)
        orientalTextView = view.findViewById<TextView>(R.id.oriental_TextView)
        etcDeptTextView = view.findViewById<TextView>(R.id.etcDept_TextView)

        allBoard.setOnClickListener { //전체 게시판으로 이동함
            activity?.let{

                if(allBoardDetail.visibility == View.GONE) allBoardDetail.visibility = View.VISIBLE
                else allBoardDetail.visibility = View.GONE

///////////////// 전체 게시판 하위 버튼 (텍스트뷰) ////////////////////////////////
                basicBoardTextView.setOnClickListener {
                    var intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("nickname", nickname)
                    intent.putExtra("userMedal", userMedal)
                    intent.putExtra("board", "자유 게시판")
                    startActivity(intent)
                }
                marketBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("nickname", nickname)
                    intent.putExtra("userMedal", userMedal)
                    intent.putExtra("board", "장터 게시판")
                    startActivity(intent)
                }
                QnABoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("nickname", nickname)
                    intent.putExtra("userMedal", userMedal)
                    intent.putExtra("board", "QnA 게시판")
                    startActivity(intent)
                }
                jobBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("nickname", nickname)
                    intent.putExtra("userType", userType)
                    intent.putExtra("userDept", userDept)
                    intent.putExtra("userMedal", userMedal)
                    intent.putExtra("board", "직종별 게시판")
                    startActivity(intent)
                }
                deptBoardTextView.setOnClickListener {
                    val intent = Intent(context, Board::class.java)
                    //Log.d("userSearch", "$userType  $userDept")
                    intent.putExtra("id", id)
                    intent.putExtra("nickname", nickname)
                    intent.putExtra("userMedal", userMedal)
                    intent.putExtra("userType", userType)
                    intent.putExtra("userDept", userDept)
                    intent.putExtra("board", "진료과별 게시판")
                    startActivity(intent)
                }

///////////////////// 각 직종별 게시판 클릭이벤트 (접근제한 검사) //////////////////////////
                doctorTextView.setOnClickListener{
                    if (userTypeDeptCheck(true, "의사")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "직종별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                nurseTextView.setOnClickListener{
                    if (userTypeDeptCheck(true, "간호사")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "직종별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                engineerTextView.setOnClickListener{
                    if (userTypeDeptCheck(true, "의료기사")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "직종별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                officeTextView.setOnClickListener{
                    if (userTypeDeptCheck(true, "사무직")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "직종별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                studentTextView.setOnClickListener{
                    if (userTypeDeptCheck(true, "학생")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "직종별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }

///////////////////// 각 진료과별 게시판 클릭이벤트 (접근 제한) ///////////////////////////////
                internalTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "내과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                sugcialTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "외과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                machuiTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "마취통증의학과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                pathologyTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "병리과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                urologyTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "비뇨의학과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                pregnancyTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "산부인과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                cosmeticTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "성형외과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                childTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "소아청소년과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                neuroTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "신경과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                eyeTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "안과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                videoTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "영상의학과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                urgentTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "응급의학과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                earTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "이비인후과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                psyTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "정신건강의학과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                boneTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "정형외과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                toothTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "치과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                skinTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "피부과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                orientalTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "한의과")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    }else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                etcDeptTextView.setOnClickListener{
                    if (userTypeDeptCheck(false, "기타")) {
                        val intent = Intent(context, Board::class.java)
                        Log.d("userSearch", "$userType  $userDept")
                        intent.putExtra("id", id)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("userMedal", userMedal)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("board", "진료과별 게시판")
                        startActivity(intent)
                    } else Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
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
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("board", "QnA 게시판")
                startActivity(intent)
            }
        }

        scheduleBtn.setOnClickListener {
            val scheduleFragment = ScheduleFragment()
            fragmentManager?.beginTransaction()?.replace(R.id.linearLayout, scheduleFragment)?.commit()
        }


        manageBusinessBtn.setOnClickListener { //비즈니스 관리(프로필수정, 글쓰기 화면으로 이동)
            val businessFragment = BusinessMainFragment()
            var bundle = Bundle()
            bundle.putString("id",id)
            businessFragment.arguments = bundle //fragment의 arguments에 데이터를 담은 bundle을 넘겨줌
            fragmentManager?.beginTransaction()?.replace(R.id.linearLayout, businessFragment)?.commit()
        }
    }

    // 접근 제한 검사
    fun userTypeDeptCheck(isType:Boolean, boardType:String) : Boolean{
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

        return if(isType) {
            // 직종별 게시판. userType 검사
            boardType == userType
        } else {
            // 진료과별 게시판. userDept 검사
            boardType == userDept
        }
    }
}