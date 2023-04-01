package com.example.medi_nion

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.databinding.ProfileBinding
import org.json.JSONArray
import org.json.JSONObject

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

        val id = arguments?.getString("id")
        val userType = arguments?.getString("userType")
        val userDept = arguments?.getString("userDept")

        //나의 활동 
        binding.item1.setOnClickListener{
            val item1ListLayout = binding.item1ListLayout
            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
            else item1ListLayout.visibility = View.GONE
        }
//        binding.item1.setOnClickListener{
//            val item1ListLayout = binding.item1ListLayout
//            if(item1ListLayout.visibility == View.GONE) item1ListLayout.visibility = View.VISIBLE
//            else item1ListLayout.visibility = View.GONE
//        }


        //내 게시물 -> board에 내 게시물 검색해서 만들기
        binding.item1List1Text.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "post")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 스크랩 -> board에 내 스크랩 검색해서 만들기
        binding.item1List2Text.setOnClickListener{
            val intent = Intent(context, Board_profile::class.java)
            intent.putExtra("id", id)
            intent.putExtra("profileMenuType", "scrap")
            intent.putExtra("userType", userType)
            intent.putExtra("userDept", userDept)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //어플 설정 -> 리스트업
        binding.item2.setOnClickListener {
            val item2ListLayout = binding.item2ListLayout
            if(item2ListLayout.visibility == View.GONE) item2ListLayout.visibility = View.VISIBLE
            else item2ListLayout.visibility = View.GONE
        }

        //계정 설정 -> 리스트업
        binding.item3.setOnClickListener {
            val item3ListLayout = binding.item3ListLayout
            if(item3ListLayout.visibility == View.GONE) item3ListLayout.visibility = View.VISIBLE
            else item3ListLayout.visibility = View.GONE
        }

        binding.item3List2Text.setOnClickListener {
            val intent: Intent = Intent(context, Login::class.java)
            startActivity(intent)
            Toast.makeText(context, "로그아웃했습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.item3List3Text.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("탈퇴하시겠습니까?")
                .setMessage("탈퇴하시려면 \"확인\"을 눌러주세요.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        UserDeleteRequest()
                        val intent: Intent = Intent(context, Login::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialogInterface, i ->

                    })
            builder.show()
        }

        binding.item4.setOnClickListener{
            val item4ListLayout = binding.item4ListLayout
            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
            else item4ListLayout.visibility = View.GONE
        }

        binding.profileItem4.setOnClickListener{
            val item4ListLayout = binding.item4ListLayout
            if(item4ListLayout.visibility == View.GONE) item4ListLayout.visibility = View.VISIBLE
            else item4ListLayout.visibility = View.GONE
        }

        //내가 구독한 비즈니스 채널
        binding.item4List1Text.setOnClickListener{

        }

        // 채널 있는지 검사
        fetchifChan(id.toString())

        binding.item4List2Text.setOnClickListener{
            //businessChan이 0이면 (비즈니스 채널 없음) -> 신규 생성
            Log.d("isChan 333333333", isChan.toString())
            if(!isChan) {
                val intent = Intent(context, BusinessManageFirstActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("userType", userType)
                intent.putExtra("userDept", userDept)
                startActivity(intent)
            } else { //businessChan이 1이면 (비즈니스 채널 있음) -> 관리
                val intent = Intent(context, BusinessManageActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("isFirst", true)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                startActivity(intent)
            }

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

        val request = Login_Request(
            Request.Method.POST,
            userDeleteUrl,
            { response ->
                if (!response.equals("Delete Fail")) {
                    Toast.makeText(
                        context,
                        String.format("탈퇴했습니다."),
                        Toast.LENGTH_SHORT
                    ).show()
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
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success");
                if (success) {
                    isChan = true
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
}

