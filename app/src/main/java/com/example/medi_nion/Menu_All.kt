package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.menu_all.*

class Menu_All: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_all)

        val basicBoardLayout = findViewById<LinearLayout>(R.id.basicboard_layout)
        val marketBoardLayout = findViewById<LinearLayout>(R.id.marketboard_layout)
        val qnaBoardLayout = findViewById<LinearLayout>(R.id.QnAboard_layout)

        val basicboardTextView = findViewById<TextView>(R.id.basicboard_TextView)
        val marketboardTextView = findViewById<TextView>(R.id.marketboard_TextView)
        val qnaboardTextView = findViewById<TextView>(R.id.QnAboard_TextView)

//        직종 텍스트 뷰 >> 클릭이벤트 연결 (접근제한)
        val doctorTextView = findViewById<TextView>(R.id.doctor_TextView)
        val nurseTextView = findViewById<TextView>(R.id.nurse_TextView)
        val engineerTextView = findViewById<TextView>(R.id.engineer_TextView)
        val officeTextView = findViewById<TextView>(R.id.office_TextView)
        val studentTextView = findViewById<TextView>(R.id.student_TextView)

//         진료과 텍스트뷰 >> 클릭이벤트 연결 (접근제한)
        val internalTextView = findViewById<TextView>(R.id.internal_TextView)
        val sugcialTextView = findViewById<TextView>(R.id.surgical_TextView)
        val machuiTextView = findViewById<TextView>(R.id.machui_TextView)
        val pathologyTextView = findViewById<TextView>(R.id.pathology_TextView)
        val urologyTextView = findViewById<TextView>(R.id.urology_TextView)
        val pregnancyTextView = findViewById<TextView>(R.id.pregnancy_TextView)
        val cosmeticTextView = findViewById<TextView>(R.id.cosmetic_TextView)
        val childTextView = findViewById<TextView>(R.id.child_TextView)
        val neuroTextView = findViewById<TextView>(R.id.neuro_TextView)
        val eyeTextView = findViewById<TextView>(R.id.eye_TextView)
        val videoTextView = findViewById<TextView>(R.id.video_TextView)
        val urgentTextView = findViewById<TextView>(R.id.urgent_TextView)
        val earTextView = findViewById<TextView>(R.id.ear_TextView)
        val psyTextView = findViewById<TextView>(R.id.psy_TextView)
        val boneTextView = findViewById<TextView>(R.id.bone_TextView)
        val toothTextView = findViewById<TextView>(R.id.tooth_TextView)
        val skinTextView = findViewById<TextView>(R.id.skin_TextView)
        val orientalTextView = findViewById<TextView>(R.id.oriental_TextView)
        val etcDeptTextView = findViewById<TextView>(R.id.etcDept_TextView)

        val id = intent.getStringExtra("id")
        val device_id = intent.getStringExtra("device_id")
        val nickname = intent.getStringExtra("nickname")
        val userMedal = intent.getStringExtra("userMedal")
        val userType = intent.getStringExtra("userType")
        val userDept = intent.getStringExtra("userDept")

        basicBoardLayout.setOnClickListener {
            var intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        basicboardTextView.setOnClickListener {
            var intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        marketBoardLayout.setOnClickListener {
            val intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "장터 게시판")
            startActivity(intent)
        }

        marketboardTextView.setOnClickListener {
            val intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "장터 게시판")
            startActivity(intent)
        }

        qnaBoardLayout.setOnClickListener {
            val intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "QnA 게시판")
            startActivity(intent)
        }

        qnaboardTextView.setOnClickListener {
            val intent = Intent(applicationContext, Board::class.java)
            intent.putExtra("id", id)
            intent.putExtra("device_id", device_id)
            intent.putExtra("nickname", nickname)
            intent.putExtra("userMedal", userMedal)
            intent.putExtra("board", "QnA 게시판")
            startActivity(intent)
        }

///////////////////// 각 직종별 게시판 클릭이벤트 (접근제한 검사) //////////////////////////
        doctorTextView.setOnClickListener {
            if (userTypeDeptCheck(true, "의사")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        nurseTextView.setOnClickListener {
            if (userTypeDeptCheck(true, "간호사")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        engineerTextView.setOnClickListener {
            if (userTypeDeptCheck(true, "의료기사")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        officeTextView.setOnClickListener {
            if (userTypeDeptCheck(true, "사무직")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        studentTextView.setOnClickListener {
            if (userTypeDeptCheck(true, "학생")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "직종별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }

///////////////////// 각 진료과별 게시판 클릭이벤트 (접근 제한) ///////////////////////////////
        internalTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "내과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        sugcialTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "외과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        machuiTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "마취통증의학과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        pathologyTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "병리과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        urologyTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "비뇨의학과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        pregnancyTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "산부인과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        cosmeticTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "성형외과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        childTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "소아청소년과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        neuroTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "신경과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        eyeTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "안과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        videoTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "영상의학과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        urgentTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "응급의학과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        earTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "이비인후과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        psyTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "정신건강의학과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        boneTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "정형외과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        toothTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "치과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        skinTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "피부과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        orientalTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "한의과")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
        etcDeptTextView.setOnClickListener {
            if (userTypeDeptCheck(false, "기타")) {
                val intent = Intent(applicationContext, Board::class.java)
                Log.d("userSearch", "$userType  $userDept")
                intent.putExtra("id", id)
                intent.putExtra("device_id", device_id)
                intent.putExtra("nickname", nickname)
                intent.putExtra("userMedal", userMedal)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                intent.putExtra("board", "진료과별 게시판")
                startActivity(intent)
            } else Toast.makeText(this, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 접근 제한 검사
    fun userTypeDeptCheck(isType:Boolean, boardType:String) : Boolean{
        val userType = intent.getStringExtra("userType")
        val userDept = intent.getStringExtra("userDept")
        return if(isType) {
            // 직종별 게시판. userType 검사
            boardType == userType
        } else {
            // 진료과별 게시판. userDept 검사
            boardType == userDept
        }
    }
}