package com.example.medi_nion

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import org.w3c.dom.Text
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        //test

        var userTypeGroup = findViewById<RadioGroup>(R.id.userType_RadioGroup)
        var userType : String = "" //사용자의 유형을 저장할 변수.


        var informView = findViewById<TextView>(R.id.informView)

        var informAll = "사용자 인증 후 모든 기능을 이용할 수 있습니다."
        var informConst = "사용자 인증 후에도 특정 접근이 제한될 수 있습니다."
        var informCorp = "사용자 인증 후 특정 기능을 이용하실 수 있습니다."

        var signUpDetail: ScrollView = findViewById<ScrollView>(R.id.signUpDetail)


        //라디오 버튼들 모음
        var basicUserBtn = findViewById<RadioButton>(R.id.basicUser_RadioBtn)
        var corpUserBtn = findViewById<RadioButton>(R.id.corpUser_RadioBtn)

        var basicDocBtn = findViewById<RadioButton>(R.id.doctor_RadioBtn)
        var basicNurBtn = findViewById<RadioButton>(R.id.nurse_RadioBtn)
        var basicTechBtn = findViewById<RadioButton>(R.id.MediTech_RadioBtn)
        var basicOffBtn = findViewById<RadioButton>(R.id.office_RadioBtn)
        var basicStuBtn = findViewById<RadioButton>(R.id.student_RadioBtn)

        //라디오 버튼들 clickListener -> group으로 안하는 이유는 check가 바뀌었을 때 말고도 버튼만 누르면 일반회원의 종류가 나열되게 하기 위하여.
        basicUserBtn.setOnClickListener{
            val basicUserGroup = findViewById<RadioGroup>(R.id.basicUser_RadioGroup); // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.VISIBLE // 일반회원의 종류를 담은 RadioGroup 활성화
            informView.text = "" //회원 종류에 따른 안내멘트 초기화

            basicDocBtn.setOnClickListener{
                userType = "doctor"
                informView.text = informAll
                basicUserBtn.text = "의사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicNurBtn.setOnClickListener{
                userType = "nurse"
                informView.text = informAll
                basicUserBtn.text = "간호사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicTechBtn.setOnClickListener{
                userType = "mediTech"
                informView.text = informConst
                basicUserBtn.text = "의료기사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicOffBtn.setOnClickListener{
                userType = "office"
                informView.text = informConst
                basicUserBtn.text = "사무직"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicStuBtn.setOnClickListener{
                userType = "student"
                informView.text = informConst
                basicUserBtn.text = "학생"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

        }

        corpUserBtn.setOnClickListener{
            val basicUserGroup = findViewById<RadioGroup>(R.id.basicUser_RadioGroup); // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.GONE

            userType = "corp"
            basicUserBtn.text = "일반회원"
            informView.text = informCorp
        }

        signUpDetail.setOnClickListener{
            var basicType = findViewById<RadioButton>(R.id.basicUser_RadioBtn)
            basicType.visibility = View.GONE
        }

        var nickname_editText = findViewById<EditText>(R.id.nickname_editText)
        var nickname_warning = findViewById<TextView>(R.id.nickname_warning)

        var id_editText = findViewById<EditText>(R.id.id_editText)
        var id_warning = findViewById<TextView>(R.id.id_warning)

        var passwd_editText = findViewById<EditText>(R.id.passwd_editText)
        var passwd_warning = findViewById<TextView>(R.id.passwd_warning)

        var passwdCheck_editText = findViewById<EditText>(R.id.passwdCheck_editText)
        var passwdCheck_warning = findViewById<TextView>(R.id.passwdCheck_warning)


        //닉네임 중복 여부 확인
        nickname_editText.addTextChangedListener{
            nickname_warning.visibility = View.VISIBLE
            nickname_warning.text = "입력하는중..."
        }

        //아이디 중복 여부 및 정규식 확인
        id_editText.addTextChangedListener{
            id_warning.visibility = View.VISIBLE
            id_warning.text = "입력하는 중..."
        }

        //비밀번호 정규식 확인 -> 숫자, 문자, 특수문자 중 2가지 포함(8~15자)
        passwd_editText.addTextChangedListener{
            passwd_warning.visibility = View.VISIBLE

            val passwdInput = passwd_editText.text
            if(!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,15}\$", passwdInput))
            {
                passwd_warning.setTextColor(Color.RED);
                passwd_warning.text = "비밀번호 형식이 올바르지 않습니다.(숫자, 문자, 특수문자 중 2가지 포함(8~15자))"
            }
            else{
                passwd_warning.setTextColor(Color.parseColor("#85D6A4"))
                passwd_warning.text = "올바른 비밀번호입니다."
            }

        }

        //비밀번호 정확성 확인
        passwd_editText.addTextChangedListener{
            passwdCheck_warning.visibility = View.VISIBLE

            val passwdInput = passwd_editText.text
            val passwdCheckInput = passwdCheck_editText.text
            if(!passwdCheckInput.equals(passwdInput))
            {
                passwdCheck_warning.setTextColor(Color.RED);
                passwdCheck_warning.text = "비밀번호가 동일하지 않습니다."
            }
            else{
                passwdCheck_warning.setTextColor(Color.parseColor("#85D6A4"));
                passwdCheck_warning.text = "올바른 비밀번호입니다."
            }
        }


        var signUpButton = findViewById<Button>(R.id.signUpBtn)
        signUpButton.setOnClickListener{
            if(nickname_editText==null || id_editText==null || passwd_editText==null  || passwd_editText==null ||
                basicUserBtn.isChecked==false || corpUserBtn.isChecked==false ) {
                var notDone_warning = findViewById<TextView>(R.id.notDone_warning)
                notDone_warning.visibility = View.VISIBLE
            }

            else{
                setContentView(R.layout.signup_done)

                var goSignIn = findViewById<Button>(R.id.goSignInBtn)
                goSignIn.setOnClickListener{
                    //로그인 페이지로 이동.
                }
            }


        }

    }
}