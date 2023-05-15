package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.databinding.PrivacyPolicyBinding
import com.example.medi_nion.databinding.ProfileBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.profile.*
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ProfileFragment : Fragment(R.layout.profile) {

    private var mBinding: ProfileBinding? = null
    private var isChan: Boolean = false

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = ProfileBinding.inflate(inflater, container, false)
        //이 사이에 변경한거 싹 다 바꾸기


        val id = arguments?.getString("id").toString()
        val userType = arguments?.getString("userType").toString()
        val userDept = arguments?.getString("userDept").toString()
        val userGrade = arguments?.getInt("userGrade").toString()
        val passwd = arguments?.getString("passwd").toString()
        val nickname = arguments?.getString("nickname").toString()

        Log.d("profileFragment", "id: $id")
        Log.d("profileFragment", "passwd: $passwd")
        Log.d("profileFragment", "nickname: $nickname")
        Log.d("profileFragment", "userType: $userType")
        Log.d("profileFragment", "userDept: $userDept")
        Log.d("profileFragment", "userGrade: $userGrade")

        binding.textViewNickname.text = nickname
        binding.textViewDept.text = userDept
        binding.textViewJob.text = userType
        fetchGrade()
        fetchIdentity()

        //내 게시물 -> board에 내 게시물 검색해서 만들기
        binding.myactWriting.setOnClickListener {
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "post")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("userGrade", userGrade)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 좋아요 ->
        binding.myactLike.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "like")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("userGrade", userGrade)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 댓글 ->
        binding.myactComment.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "comment")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("userGrade", userGrade)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }


        //내 스크랩 -> board에 내 스크랩 검색해서 만들기
        binding.myactLikescrap.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "scrap")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("userGrade", userGrade)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 북마크 -> business에서 북마크한 글 찾기
        binding.myactBusinessscrap.setOnClickListener{
            val intent = Intent(context, Business_bookmark__profile::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }


        //내가 구독한 비즈니스 채널
        binding.myactBusinesssub.setOnClickListener{
            val id = arguments?.getString("id")
            val intent = Intent(context, BusinessSubList::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        // 채널 있는지 검사
        fetchifChan(id.toString())

        binding.myactBusinesssubmanage.setOnClickListener{
            //businessChan이 0이면 (비즈니스 채널 없음) -> 신규 생성
            if(!isChan) {
                val intent = Intent(context, BusinessManageFirstActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                startActivity(intent)
            } else { //businessChan이 1이면 (비즈니스 채널 있음) -> 관리
                val intent = Intent(context, BusinessManageActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("isFirst", false)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                startActivity(intent)
            }
        }

        //알림 추가하기
        binding.appSetting.setOnClickListener {

        }

        //다크모드 추가하기
        binding.appDark.setOnClickListener {

        }

        //비밀번호 설정
        binding.appPasswd.setOnClickListener {
            var passwordResetUrl = "http://seonho.dothome.co.kr/UserPasswdSelect.php"
            var userUpdateUrl = "http://seonho.dothome.co.kr/UserPasswdUpdate.php"
            val builder = AlertDialog.Builder(context)
            val dialogView = layoutInflater.inflate(R.layout.profile_password_reset, null)
            var dialogText_pwd_now = dialogView.findViewById<EditText>(R.id.password_now_editText).text.toString()
            var dialogText_pwd_new = dialogView.findViewById<EditText>(R.id.password_new_editText).text.toString()
            var dialogText_pwd_newcheck = dialogView.findViewById<EditText>(R.id.password_check_editText).text.toString()

            builder.setTitle("비밀번호 재설정")
                .setView(dialogView)
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogText_pwd_now = dialogView.findViewById<EditText>(R.id.password_now_editText).text.toString()
                        dialogText_pwd_new = dialogView.findViewById<EditText>(R.id.password_new_editText).text.toString()
                        dialogText_pwd_newcheck = dialogView.findViewById<EditText>(R.id.password_check_editText).text.toString()

                        Log.d("456456", "$dialogText_pwd_now, $dialogText_pwd_new")
                        if (passwd == dialogText_pwd_now) {
                            val request = Login_Request(
                                Request.Method.POST,
                                passwordResetUrl,
                                { response ->
                                    if (!response.equals("no User")) {
                                        val userSearch = Login_Request(
                                            Request.Method.POST,
                                            userUpdateUrl,
                                            { responseUser ->
                                                Log.d("dfdfd", responseUser)
                                                if (dialogText_pwd_new == dialogText_pwd_newcheck) {
                                                    if (!responseUser.equals("updateUser fail")) {
                                                        Log.d(
                                                            "AAAAAA",
                                                            "$id, $dialogText_pwd_new, $dialogText_pwd_now"
                                                        )
                                                        Toast.makeText(
                                                            context,
                                                            String.format("비밀번호를 변경했습니다."),
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            String.format("userUpdatefail"),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        Log.d("userUpdate", "Failed")
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        String.format("변경할 비밀번호가 일치하지 않습니다."),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            {
                                                Log.d(
                                                    "UserUpdate Failed",
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
                                                "id" to id.toString(),
                                                "passwd" to dialogText_pwd_now,
                                                "passwd_new" to dialogText_pwd_new
                                            )
                                        )
                                        val queue = Volley.newRequestQueue(context)
                                        queue.add(userSearch)

                                    } else {
                                        Toast.makeText(
                                            context,
                                            String.format("기존 비밀번호가 틀립니다. 다시 확인해주세요."),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                {
                                    Log.d(
                                        "update failed",
                                        "error......${context?.let { it1 -> error(it1) }}"
                                    )
                                },
                                hashMapOf(
                                    "id" to id.toString()
                                )
                            )
                            val queue = Volley.newRequestQueue(context)
                            queue.add(request)
                        } else {
                            Toast.makeText(
                                context,
                                String.format("기존 비밀번호가 틀립니다. 다시 확인해주세요."),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
            builder.show()
        }

        //로그아웃
        binding.appLogout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("경고")
                .setMessage("로그아웃하시려면 \"확인\"을 눌러주세요.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        val intent: Intent = Intent(context, Login::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
            builder.show()
        }

        //로그아웃
        binding.appLogout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("경고")
                .setMessage("로그아웃하시려면 \"확인\"을 눌러주세요.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        val intent: Intent = Intent(context, Login::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
            builder.show()
        }

        //서비스 탈퇴
        binding.appSec.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("경고")
                .setMessage("탈퇴하시려면 \"확인\"을 눌러주세요.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        UserDeleteRequest()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
            builder.show()
        }

        //공지사항
        binding.profileItem5.setOnClickListener {

        }

        //고객센터
        binding.profileItem6.setOnClickListener {

        }

        //이용약관
        binding.profileItem7.setOnClickListener {

        }

        //개인 정보 처리 방침
        binding.profileItem8.setOnClickListener {
            startActivity(Intent(context, PrivacyPolicy::class.java))
        }

        //서비스 이용 동의
        binding.profileItem9.setOnClickListener {
            startActivity(Intent(context, ServiceAgreement::class.java))
        }

        //오픈 소스 라이선스
        binding.profileItem10.setOnClickListener {
            val intent = Intent(context, LicenseActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun UserDeleteRequest() {
        val id = arguments?.getString("id")
        var userDeleteUrl = "http://seonho.dothome.co.kr/UserDelete.php"
        val intent: Intent = Intent(context, Login::class.java)

        val request = Login_Request(
            Request.Method.POST,
            userDeleteUrl,
            { response ->
                Log.d("456", id.toString())
                if (!response.equals("Delete Fail")) {
                    Toast.makeText(
                        context,
                        String.format("탈퇴했습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        context,
                        String.format("탈퇴 실패했습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { Log.d("login failed", "error......${context?.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id.toString()
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    fun fetchifChan(id:String) {
        val chanNamerequest = Login_Request(
            Request.Method.POST,
            "http://seonho.dothome.co.kr/BusinessChanName.php",
            { response ->
                Log.d("fetchifChan", response)
                if (!response.equals("Channel Name Fail")) {
                    isChan = !response.equals("no Channel Name")
                }
            },
            { Log.d("failed", "error......${activity?.applicationContext?.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id
            )
        )

        val queue = Volley.newRequestQueue(context)
        queue.add(chanNamerequest)
    }

    fun fetchGrade() {
        val gradeurl = "http://seonho.dothome.co.kr/Grade_Select.php"
        var nickname = arguments?.getString("nickname").toString()
        val gradeImage = binding.grade

        val request = Login_Request(
            Request.Method.POST,
            gradeurl,
            { response ->
                Log.d("gradedkd", response)

                when (response) {
                    "berry" -> {
                        gradeImage.setImageResource(R.drawable.berry)
                    }
                    "flower" -> {
                        gradeImage.setImageResource(R.drawable.flower)
                    }
                    "branch" -> {
                        gradeImage.setImageResource(R.drawable.branch)
                    }
                    else -> {
                        gradeImage.setImageResource(R.drawable.sprout)
                    }
                }

            }, { Log.d("grade Failed", "error......${error(this)}") },
            hashMapOf(
                "nickname" to nickname
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    fun fetchIdentity() {
        val id = arguments?.getString("id").toString()
        val userType = arguments?.getString("userType").toString()
        val userDept = arguments?.getString("userDept").toString()
        val userGrade = arguments?.getInt("userGrade").toString()
        val nickname = arguments?.getString("nickname").toString()
        val url = "http://seonho.dothome.co.kr/profile_identity.php"
        val identity_warning_img = binding.identityWarning

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                val jsonArray = JSONArray(response)

                for (i in 0 until jsonArray.length()) {

                    val item = jsonArray.getJSONObject(i)

                    val id = item.getString("id")
                    val identity_check = item.getString("identity_check")
                    Log.d("identititi", "$id, $identity_check")

                    when(identity_check) {
                        "false" -> {
                            identity_warning_img.setOnClickListener {
                                var intent = Intent(context, Profile_opencv::class.java)
                                intent.putExtra("id", id)
                                intent.putExtra("userType", userType)
                                intent.putExtra("userDept", userDept)
                                intent.putExtra("userGrade", userGrade)
                                intent.putExtra("nickname", nickname)
                                startActivity(intent)
                            }
                        }
                        else -> {
                            identity_warning_img.setImageResource(R.drawable.identity_check)
                            identity_warning_img.setOnClickListener {
                                Toast.makeText(context, "신분증이 인증되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            Log.d("identity", "true")
                        }
                    }

                }
            }, { Log.d("identity Failed", "error......${error(this)}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }


}













//ProfileFragment.kt
//
//package com.example.medi_nion
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.ImageDecoder
//import android.media.Image
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.android.volley.Request
//import com.android.volley.toolbox.Volley
//import com.example.medi_nion.databinding.PrivacyPolicyBinding
//import com.example.medi_nion.databinding.ProfileBinding
//import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
//import org.json.JSONArray
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.IOException
//
//class ProfileFragment : Fragment(R.layout.profile) {
//    private var mBinding: ProfileBinding? = null
//    private var isChan: Boolean = false
//
//    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
//    private val binding get() = mBinding!!
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        mBinding = ProfileBinding.inflate(inflater, container, false)
//
//        val id = arguments?.getString("id").toString()
//        val userType = arguments?.getString("userType").toString()
//        val userDept = arguments?.getString("userDept").toString()
//        val userGrade = arguments?.getInt("userGrade").toString()
//        val passwd = arguments?.getString("passwd").toString()
//        val nickname = arguments?.getString("nickname").toString()
//
//        Log.d("profileFragment", "id: $id")
//        Log.d("profileFragment", "passwd: $passwd")
//        Log.d("profileFragment", "nickname: $nickname")
//        Log.d("profileFragment", "userType: $userType")
//        Log.d("profileFragment", "userDept: $userDept")
//        Log.d("profileFragment", "userGrade: $userGrade")
//
//        binding.textViewNickname.text = nickname
//        binding.textViewDept.text = userDept
//        binding.textViewJob.text = userType
//        fetchGrade()
//        fetchIdentity()
//
//        //나의 활동
//        binding.item1.setOnClickListener{
//            val item1ListLayout = binding.item1ListLayout
//            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
//            else item1ListLayout.visibility = View.GONE
//        }
////        binding.item1.setOnClickListener{
////            val item1ListLayout = binding.item1ListLayout
////            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
////            else item1ListLayout.visibility = View.GONE
////        }
//
//
//        //내 게시물 -> board에 내 게시물 검색해서 만들기
//        binding.item1List1Text.setOnClickListener{
//            val intent = Intent(context, Board_profile::class.java)
//            intent.putExtra("id", id)
//            intent.putExtra("profileMenuType", "post")
//            intent.putExtra("userType", userType)
//            intent.putExtra("userDept", userDept)
//            intent.putExtra("userGrade", userGrade)
//            intent.putExtra("board", "자유 게시판")
//            startActivity(intent)
//        }
//
//        //내 좋아요 ->
//        binding.item1List3Text.setOnClickListener{
//            val intent = Intent(context, Board_profile::class.java)
//            intent.putExtra("id", id)
//            intent.putExtra("profileMenuType", "like")
//            intent.putExtra("userType", userType)
//            intent.putExtra("userDept", userDept)
//            intent.putExtra("userGrade", userGrade)
//            intent.putExtra("board", "자유 게시판")
//            startActivity(intent)
//        }
//
//        //내 댓글 ->
//        binding.item1List4Text.setOnClickListener{
//            val intent = Intent(context, Board_profile::class.java)
//            intent.putExtra("id", id)
//            intent.putExtra("profileMenuType", "comment")
//            intent.putExtra("userType", userType)
//            intent.putExtra("userDept", userDept)
//            intent.putExtra("userGrade", userGrade)
//            intent.putExtra("board", "자유 게시판")
//            startActivity(intent)
//        }
//
//        //내 스크랩 -> board에 내 스크랩 검색해서 만들기
//        binding.item1List2Text.setOnClickListener{
//            val intent = Intent(context, Board_profile::class.java)
//            intent.putExtra("id", id)
//            intent.putExtra("profileMenuType", "scrap")
//            intent.putExtra("userType", userType)
//            intent.putExtra("userDept", userDept)
//            intent.putExtra("userGrade", userGrade)
//            intent.putExtra("board", "자유 게시판")
//            startActivity(intent)
//        }
//
//        //내 북마크 -> business에서 북마크한 글 찾기
//        binding.item1List5Text.setOnClickListener{
//            val intent = Intent(context, Business_bookmark__profile::class.java)
//            intent.putExtra("id", id)
//            startActivity(intent)
//        }
//
//        //어플 설정 -> 리스트업
//        binding.item2.setOnClickListener {
//            val item2ListLayout = binding.item2ListLayout
//            if(item2ListLayout.visibility == View.GONE) item2ListLayout.visibility = View.VISIBLE
//            else item2ListLayout.visibility = View.GONE
//        }
//
//        //계정 설정 -> 리스트업
//        binding.item3.setOnClickListener {
//            val item3ListLayout = binding.item3ListLayout
//            if(item3ListLayout.visibility == View.GONE) item3ListLayout.visibility = View.VISIBLE
//            else item3ListLayout.visibility = View.GONE
//        }
//
//        //비밀번호 설정
//        binding.item3List1Text.setOnClickListener {
//            var passwordResetUrl = "http://seonho.dothome.co.kr/UserPasswdSelect.php"
//            var userUpdateUrl = "http://seonho.dothome.co.kr/UserPasswdUpdate.php"
//            val builder = AlertDialog.Builder(context)
//            val dialogView = layoutInflater.inflate(R.layout.profile_password_reset, null)
//            var dialogText_pwd_now = dialogView.findViewById<EditText>(R.id.password_now_editText).text.toString()
//            var dialogText_pwd_new = dialogView.findViewById<EditText>(R.id.password_new_editText).text.toString()
//            var dialogText_pwd_newcheck = dialogView.findViewById<EditText>(R.id.password_check_editText).text.toString()
//
//            builder.setTitle("비밀번호 재설정")
//                .setView(dialogView)
//                .setPositiveButton("확인",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//                        dialogText_pwd_now = dialogView.findViewById<EditText>(R.id.password_now_editText).text.toString()
//                        dialogText_pwd_new = dialogView.findViewById<EditText>(R.id.password_new_editText).text.toString()
//                        dialogText_pwd_newcheck = dialogView.findViewById<EditText>(R.id.password_check_editText).text.toString()
//
//                        Log.d("456456", "$dialogText_pwd_now, $dialogText_pwd_new")
//                        if (passwd == dialogText_pwd_now) {
//                            val request = Login_Request(
//                                Request.Method.POST,
//                                passwordResetUrl,
//                                { response ->
//                                    if (!response.equals("no User")) {
//                                        val userSearch = Login_Request(
//                                            Request.Method.POST,
//                                            userUpdateUrl,
//                                            { responseUser ->
//                                                Log.d("dfdfd", responseUser)
//                                                if (dialogText_pwd_new == dialogText_pwd_newcheck) {
//                                                    if (!responseUser.equals("updateUser fail")) {
//                                                        Log.d(
//                                                            "AAAAAA",
//                                                            "$id, $dialogText_pwd_new, $dialogText_pwd_now"
//                                                        )
//                                                        Toast.makeText(
//                                                            context,
//                                                            String.format("비밀번호를 변경했습니다."),
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//
//                                                    } else {
//                                                        Toast.makeText(
//                                                            context,
//                                                            String.format("userUpdatefail"),
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                        Log.d("userUpdate", "Failed")
//                                                    }
//                                                } else {
//                                                    Toast.makeText(
//                                                        context,
//                                                        String.format("변경할 비밀번호가 일치하지 않습니다."),
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                            },
//                                            {
//                                                Log.d(
//                                                    "UserUpdate Failed",
//                                                    "error......${
//                                                        context?.let { it1 ->
//                                                            error(
//                                                                it1
//                                                            )
//                                                        }
//                                                    }"
//                                                )
//                                            },
//                                            hashMapOf(
//                                                "id" to id.toString(),
//                                                "passwd" to dialogText_pwd_now,
//                                                "passwd_new" to dialogText_pwd_new
//                                            )
//                                        )
//                                        val queue = Volley.newRequestQueue(context)
//                                        queue.add(userSearch)
//
//                                    } else {
//                                        Toast.makeText(
//                                            context,
//                                            String.format("기존 비밀번호가 틀립니다. 다시 확인해주세요."),
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                },
//                                {
//                                    Log.d(
//                                        "update failed",
//                                        "error......${context?.let { it1 -> error(it1) }}"
//                                    )
//                                },
//                                hashMapOf(
//                                    "id" to id.toString()
//                                )
//                            )
//                            val queue = Volley.newRequestQueue(context)
//                            queue.add(request)
//                        } else {
//                            Toast.makeText(
//                                context,
//                                String.format("기존 비밀번호가 틀립니다. 다시 확인해주세요."),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    })
//                .setNegativeButton("취소",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//
//                    })
//            builder.show()
//        }
//
//        //로그아웃
//        binding.item3List2Text.setOnClickListener {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("경고")
//                .setMessage("로그아웃하시려면 \"확인\"을 눌러주세요.")
//                .setPositiveButton("확인",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//                        val intent: Intent = Intent(context, Login::class.java)
//                        startActivity(intent)
//                    })
//                .setNegativeButton("취소",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//
//                    })
//            builder.show()
//        }
//
//        //서비스 탈퇴
//        binding.item3List3Text.setOnClickListener {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("경고")
//                .setMessage("탈퇴하시려면 \"확인\"을 눌러주세요.")
//                .setPositiveButton("확인",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//                        UserDeleteRequest()
//                    })
//                .setNegativeButton("취소",
//                    DialogInterface.OnClickListener { dialogInterface, i ->
//
//                    })
//            builder.show()
//        }
//
//        binding.item4.setOnClickListener{
//            val item4ListLayout = binding.item4ListLayout
//            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
//            else item4ListLayout.visibility = View.GONE
//        }
//
//        binding.profileItem4.setOnClickListener{
//            val item4ListLayout = binding.item4ListLayout
//            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
//            else item4ListLayout.visibility = View.GONE
//        }
//
//        //내가 구독한 비즈니스 채널
//        binding.item4List1Text.setOnClickListener{
//            val id = arguments?.getString("id")
//            val intent = Intent(context, BusinessSubList::class.java)
//            intent.putExtra("id", id)
//            startActivity(intent)
//        }
//
//        // 채널 있는지 검사
//        fetchifChan(id.toString())
//
//        binding.item4List2Text.setOnClickListener{
//            //businessChan이 0이면 (비즈니스 채널 없음) -> 신규 생성
//            if(!isChan) {
//                val intent = Intent(context, BusinessManageFirstActivity::class.java)
//                intent.putExtra("id", id)
//                intent.putExtra("userType", userType)
//                intent.putExtra("userDept", userDept)
//                startActivity(intent)
//            } else { //businessChan이 1이면 (비즈니스 채널 있음) -> 관리
//                val intent = Intent(context, BusinessManageActivity::class.java)
//                intent.putExtra("id", id)
//                intent.putExtra("isFirst", false)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
//                startActivity(intent)
//            }
//
//        }
//
//        binding.licenseLinearLayout.setOnClickListener{
//            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
//        }
//
//        binding.privacyLinearLayout.setOnClickListener{
//            startActivity(Intent(context, PrivacyPolicy::class.java))
//        }
//
//        binding.serviceLinearLayout.setOnClickListener{
//            startActivity(Intent(context, ServiceAgreement::class.java))
//        }
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        mBinding = null
//    }
//
//    private fun UserDeleteRequest() {
//        val id = arguments?.getString("id")
//        var userDeleteUrl = "http://seonho.dothome.co.kr/UserDelete.php"
//        val intent: Intent = Intent(context, Login::class.java)
//
//        val request = Login_Request(
//            Request.Method.POST,
//            userDeleteUrl,
//            { response ->
//                Log.d("456", id.toString())
//                if (!response.equals("Delete Fail")) {
//                    Toast.makeText(
//                        context,
//                        String.format("탈퇴했습니다."),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(
//                        context,
//                        String.format("탈퇴 실패했습니다."),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }, { Log.d("login failed", "error......${context?.let { it1 -> error(it1) }}") },
//            hashMapOf(
//                "id" to id.toString()
//            )
//        )
//        val queue = Volley.newRequestQueue(context)
//        queue.add(request)
//    }
//
//    fun fetchifChan(id:String) {
//        val chanNamerequest = Login_Request(
//            Request.Method.POST,
//            "http://seonho.dothome.co.kr/BusinessChanName.php",
//            { response ->
//                Log.d("fetchifChan", response)
//                if (!response.equals("Channel Name Fail")) {
//                    isChan = !response.equals("no Channel Name")
//                }
//            },
//            { Log.d("failed", "error......${activity?.applicationContext?.let { it1 -> error(it1) }}") },
//            hashMapOf(
//                "id" to id
//            )
//        )
//
//        val queue = Volley.newRequestQueue(context)
//        queue.add(chanNamerequest)
//    }
//
//    fun fetchGrade() {
//        val gradeurl = "http://seonho.dothome.co.kr/Grade_Select.php"
//        var nickname = arguments?.getString("nickname").toString()
//        val gradeImage = binding.grade
//
//        val request = Login_Request(
//            Request.Method.POST,
//            gradeurl,
//            { response ->
//                Log.d("gradedkd", response)
//
//                when (response) {
//                    "berry" -> {
//                        gradeImage.setImageResource(R.drawable.berry)
//                    }
//                    "flower" -> {
//                        gradeImage.setImageResource(R.drawable.flower)
//                    }
//                    "branch" -> {
//                        gradeImage.setImageResource(R.drawable.branch)
//                    }
//                    else -> {
//                        gradeImage.setImageResource(R.drawable.sprout)
//                    }
//                }
//
//            }, { Log.d("grade Failed", "error......${error(this)}") },
//            hashMapOf(
//                "nickname" to nickname
//            )
//        )
//        val queue = Volley.newRequestQueue(context)
//        queue.add(request)
//    }
//
//    fun fetchIdentity() {
//        val id = arguments?.getString("id").toString()
//        val userType = arguments?.getString("userType").toString()
//        val userDept = arguments?.getString("userDept").toString()
//        val userGrade = arguments?.getInt("userGrade").toString()
//        val nickname = arguments?.getString("nickname").toString()
//        val url = "http://seonho.dothome.co.kr/profile_identity.php"
//        val identity_warning_img = binding.identityWarning
//
//        val request = Login_Request(
//            Request.Method.POST,
//            url,
//            { response ->
//                val jsonArray = JSONArray(response)
//
//                for (i in 0 until jsonArray.length()) {
//
//                    val item = jsonArray.getJSONObject(i)
//
//                    val id = item.getString("id")
//                    val identity_check = item.getString("identity_check")
//                    Log.d("identititi", "$id, $identity_check")
//
//                    when(identity_check) {
//                        "false" -> {
//                            identity_warning_img.setOnClickListener {
//                                var intent = Intent(context, Profile_opencv::class.java)
//                                intent.putExtra("id", id)
//                                intent.putExtra("userType", userType)
//                                intent.putExtra("userDept", userDept)
//                                intent.putExtra("userGrade", userGrade)
//                                intent.putExtra("nickname", nickname)
//                                startActivity(intent)
//                            }
//                        }
//                        else -> {
//                            identity_warning_img.setImageResource(R.drawable.identity_check)
//                            identity_warning_img.setOnClickListener {
//                                Toast.makeText(context, "신분증이 인증되었습니다.", Toast.LENGTH_SHORT).show()
//                            }
//                            Log.d("identity", "true")
//                        }
//                    }
//
//                }
//            }, { Log.d("identity Failed", "error......${error(this)}") },
//            hashMapOf(
//                "id" to id
//            )
//        )
//        val queue = Volley.newRequestQueue(context)
//        queue.add(request)
//    }
//}



