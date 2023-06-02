package com.example.medi_nion

//import com.example.medi_nion.interface1.SignUp_Request
//import com.example.medi_nion.`object`.RetrofitCilent_Request
//import com.example.medi_nion.dataclass.Data_SignUp_Request
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.ImageDecoder.ImageInfo
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.android.synthetic.main.business_writing_img_item.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.*
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.sqrt


class SignUP : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    val REQUEST_IMAGE_CAPTURE = 1
    val TAKE_PICTURE = 2
    var lastSelected = ""
    var userDept = ""

    private var selectedUserType = ""
    private var selectedUserDept = ""
    lateinit var idImgView: ImageView
    var photoUri: Uri? = null
    lateinit var bitmap: Bitmap
    var image : String = "null"


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongThread", "MissingInflatedId", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        //키보드 숨기기
        val focusView: View? = currentFocus
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }

        var userTypeGroup = findViewById<RadioGroup>(R.id.userType_RadioGroup)
        var userType: String = "" //사용자의 유형을 저장할 변수.


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

        basicUserBtn.setOnClickListener {
            val basicUserArray = arrayListOf("의사", "간호사", "의료기사", "사무직", "학생")
            showBottomSheet(basicUserArray, "userType")
        }


        corpUserBtn.setOnClickListener {
            val basicUserGroup =
                findViewById<RadioGroup>(R.id.basicUser_RadioGroup) // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.GONE

            imm.hideSoftInputFromWindow(corpUserBtn.getWindowToken(), 0)

            userType = "corp"
            basicUserBtn.text = "일반 회원"
            informView.text = informCorp
        }

        signUpDetail.setOnClickListener {
            var basicType = findViewById<RadioButton>(R.id.basicUser_RadioBtn)
            basicType.visibility = View.GONE
        }

        var userDept_selection = findViewById<TextView>(R.id.userDept_selection)
        Log.d("89713213", userDept_selection.toString())
        userDept_selection.setOnClickListener {

            var userDeptArray = getResources().getStringArray(R.array.userDept_spinner_array)
            var userDeptArrayList = ArrayList<String>()
            for (i in userDeptArray) {
                userDeptArrayList.add(i)
            }
            showBottomSheet(userDeptArrayList, "userDept")
        }

        var nickname_editText = findViewById<EditText>(R.id.nickname_editText)
        var nickname_warning = findViewById<TextView>(R.id.nickname_warning)

        var id_editText = findViewById<EditText>(R.id.id_editText)
        var id_warning = findViewById<TextView>(R.id.id_warning)

        var passwd_editText = findViewById<EditText>(R.id.passwd_editText)
        var passwd_warning = findViewById<TextView>(R.id.passwd_warning)

        var passwdCheck_editText = findViewById<EditText>(R.id.passwdCheck_editText)
        var passwdCheck_warning = findViewById<TextView>(R.id.passwdCheck_warning)

        var identity_editText = findViewById<EditText>(R.id.identity_editText)
        var identity = findViewById<TextView>(R.id.text_result)
        var identity_check = ""

        //닉네임 중복 여부 및 정규식 확인
        val nickname_queue = Volley.newRequestQueue(this)
        Handler(Looper.getMainLooper()).postDelayed({
            nickname_editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    nickname_warning.visibility = View.VISIBLE
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var nickname_editText =
                        findViewById<EditText>(R.id.nickname_editText).text.toString()
                    var url_nicknamevalidate = "http://seonho.dothome.co.kr/nicknameValidate.php"

                    var request = SignUP_Request(

                        Request.Method.POST,
                        url_nicknamevalidate,
                        { response ->
                            if (nickname_editText.isEmpty()) {
                                nickname_warning.setTextColor(Color.RED)
                                nickname_warning.text = "닉네임을 입력해주세요."
                            }
                            if (response.equals("nickname_Validate Success")) {
                                nickname_editText = response.toString()

                                nickname_warning.setTextColor(Color.BLUE)
                                nickname_warning.text = "사용가능한 닉네임입니다."

                            } else {
                                nickname_warning.setTextColor(Color.RED)
                                nickname_warning.text = "이미 사용중인 닉네임입니다."
                            }
                        },
                        {
                            Log.d(
                                "nickname_validate failed",
                                "error......${error(applicationContext)}"
                            )
                        },
                        hashMapOf(
                            "nickname" to nickname_editText
                        )
                    )
                    nickname_queue.add(request)
                    Log.d("nickname request", "$request, $nickname_queue")
                }

                override fun afterTextChanged(s: Editable?) {
                    nickname_warning.visibility = View.VISIBLE
                }
            })
        }, 2000)


        //아이디 중복 여부 및 정규식 확인
        val id_queue = Volley.newRequestQueue(this)
        Handler(Looper.getMainLooper()).postDelayed({
            id_editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    id_warning.visibility = View.VISIBLE
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    var url_idvalidate = "http://seonho.dothome.co.kr/idValidate.php"
                    var id_editText = findViewById<EditText>(R.id.id_editText).text.toString()

                    var request = SignUP_Request(
                        Request.Method.POST,
                        url_idvalidate,
                        { response ->
                            if (id_editText.isEmpty()) {
                                id_warning.text = "아이디를 입력해주세요."
                                id_warning.setTextColor(Color.RED)
                            }
                            if (response.equals("id_Validate Success")) {
                                id_editText = response.toString()
                                id_warning.setTextColor(Color.BLUE)
                                id_warning.text = "사용가능한 아이디입니다."

                            } else {
                                id_warning.setTextColor(Color.RED)
                                id_warning.text = "이미 사용중인 아이디입니다."
                            }
                        },
                        { Log.d("id_validate failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "id" to id_editText
                        )
                    )
                    id_queue.add(request)
                    Log.d("idrequest", "$request, $id_queue")
                }

                override fun afterTextChanged(s: Editable?) {
                    id_warning.visibility = View.VISIBLE
                }
            })
        }, 2000)


        //비밀번호 확인 먼저 입력하고 비밀번호 입력하면 동일하지 않게 뜬다.... -> 고치기
        //비밀번호 정규식 확인 -> 숫자, 문자, 특수문자 중 2가지 포함(8~15자)
        Handler(Looper.getMainLooper()).postDelayed({
            passwd_editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    passwd_warning.visibility = View.VISIBLE
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val passwd_editText =
                        findViewById<EditText>(R.id.passwd_editText).text.toString()
                    val passwdInput = passwd_editText
                    if (!Pattern.matches(
                            "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,15}\$",
                            passwdInput
                        )
                    ) {
                        passwd_warning.setTextColor(Color.RED);
                        passwd_warning.text = "비밀번호 형식이 올바르지 않습니다.\n(숫자, 문자, 특수문자 중 2가지 포함(8~15자))"
                    } else {
                        passwd_warning.setTextColor(Color.BLUE)
                        passwd_warning.text = "올바른 비밀번호입니다."
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    nickname_warning.visibility = View.VISIBLE
                }
            })
        }, 2000)

        //비밀번호 정확성 확인
        Handler(Looper.getMainLooper()).postDelayed({
            passwdCheck_editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    passwdCheck_warning.visibility = View.VISIBLE
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val passwdCheck_editText =
                        findViewById<EditText>(R.id.passwdCheck_editText)

                    val passwdInput = passwd_editText.text.toString()
                    val passwdCheckInput = passwdCheck_editText.text.toString()

                    if (passwdCheckInput != passwdInput) {
                        passwdCheck_warning.setTextColor(Color.RED)
                        passwdCheck_warning.text = "비밀번호가 동일하지 않습니다."
                    } else {
                        passwdCheck_warning.setTextColor(Color.BLUE)
                        passwdCheck_warning.text = "올바른 비밀번호입니다."
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    passwdCheck_warning.visibility = View.VISIBLE
                }
            })
        }, 2000)

        var signUpButton = findViewById<Button>(R.id.signUpBtn)
        signUpButton.setOnClickListener {
            val validate = true
            if (TextUtils.isEmpty(nickname_editText.text.toString()) ||
                TextUtils.isEmpty(id_editText.text.toString()) ||
                TextUtils.isEmpty(passwd_editText.text.toString()) ||
                TextUtils.isEmpty(passwdCheck_editText.text.toString()) ||
                TextUtils.isEmpty(passwdCheck_editText.text.toString()) ||
//                TextUtils.isEmpty(identity.text.toString())||
                (!basicUserBtn.isChecked && !corpUserBtn.isChecked)
            ) {

                var notDone_warning = findViewById<TextView>(R.id.notDone_warning)
                notDone_warning.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    notDone_warning.visibility = View.INVISIBLE
                }, 2000)
                if (!validate) {
                    Toast.makeText(
                        baseContext,
                        "중복된 아이디와 닉네임이 있는지 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                signUPRequest()
            }
        }
    }

    private fun showBottomSheet(items : ArrayList<String> , type : String){
        val bottomSheetView = layoutInflater.inflate(R.layout.normal_dialog, null)
        bottomSheetView.setBackgroundColor(Color.parseColor("#00000000"))
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        var userTypeGroup = findViewById<RadioGroup>(R.id.userType_RadioGroup)
        var userType: String = "" //사용자의 유형을 저장할 변수.
        var informView = findViewById<TextView>(R.id.informView)

        var informAll = "사용자 인증 후 모든 기능을 이용할 수 있습니다."
        var informConst = "사용자 인증 후에도 특정 접근이 제한될 수 있습니다."
        var informCorp = "사용자 인증 후 특정 기능을 이용하실 수 있습니다."

        val cancelBtn = bottomSheetDialog.findViewById<TextView>(R.id.cancel)
        cancelBtn?.setOnClickListener{
            bottomSheetDialog.dismiss()
        }

        val selectBtn = bottomSheetDialog.findViewById<TextView>(R.id.select)
        val basicUserBtn = findViewById<RadioButton>(R.id.basicUser_RadioBtn)

        selectBtn?.setOnClickListener{

            if(type == "userType"){
                if (selectedUserType == "의사") {
                    userType = "doctor"
                    informView.text = informAll
                } else if (selectedUserType == "간호사") {
                    userType = "doctor"
                    informView.text = informAll
                } else if (selectedUserType == "의료기사") {
                    userType = "mediTech"
                    informView.text = informConst
                } else if (selectedUserType == "사무직") {
                    userType = "office"
                    informView.text = informConst
                } else if (selectedUserType == "학생") {
                    userType = "student"
                    informView.text = informConst
                }

                basicUserBtn.text = selectedUserType
            }

            else if(type == "userDept"){
                Log.d("slslslsle", selectedUserDept)
                findViewById<EditText>(R.id.userDept_selection).setText(selectedUserDept)
            }

            bottomSheetDialog.dismiss()
            if(type == "userType")
                lastSelected = selectedUserType
            else if(type == "userDept")
                lastSelected = selectedUserDept
        }

        val dialogRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.dialog_recyclerView)
        val dialogAdapter = DialogRecyclerAdapter(items, lastSelected)
        dialogRecyclerView?.adapter = dialogAdapter


        dialogAdapter.setOnItemClickListener(
            object : DialogRecyclerAdapter.OnItemClickListener{
                override fun onItemClick(v: View, data: String) {
                    if(type == "userType")
                        selectedUserType = data
                    else if(type == "userDept")
                        selectedUserDept = data

                }

            }
        )

        bottomSheetDialog.show()
    }

    //db 연동 시작
    @SuppressLint("HardwareIds", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUPRequest() {
        val basicUserBtn = findViewById<RadioButton>(R.id.basicUser_RadioBtn)
        val corpUserBtn = findViewById<RadioButton>(R.id.corpUser_RadioBtn)

        val nickname = findViewById<EditText>(R.id.nickname_editText).text.toString()
        val id = findViewById<EditText>(R.id.id_editText).text.toString()
        val passwd = findViewById<EditText>(R.id.passwd_editText).text.toString()
        val passwdCheck_editText = findViewById<EditText>(R.id.passwdCheck_editText).text.toString()
        val userMedal = 0
        val userGrade = 0
        val identity = findViewById<EditText>(R.id.identity_editText).text.toString()
        var identity_opencv = ""
        val identity_before = findViewById<TextView>(R.id.text_result).text.toString()
        var identity_check = ""
        var idimgView = findViewById<ImageView>(R.id.idImgView)
        var token = "sdfdsf"

        identity_opencv = identity_before.replace("\n", "")
        identity_opencv = identity_opencv.replace(" ", "")
        identity_check = identity_opencv.contains(identity).toString()


        userDept = selectedUserDept
        if (userDept.equals("내과 (심장내과, 혈액내과, 호흡기내과, 소화기내과 등)")) {
            userDept = "내과"
        } else if (userDept.equals("외과 (혈관외과 등)")) {
            userDept = "외과"
        } else if (userDept.equals("신경과 (신경외과)")) {
            userDept = "신경과"
        } else if (userDept.equals("기타 (핵의학과, 진단검사의학과, 재활의학과 등)")) {
            userDept = "기타"
        }

        val url = "http://seonho.dothome.co.kr/SignUP.php"

        val request = SignUP_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("REPSON", response.toString())
                //비밀번호와 비밀번호 확인이 같으면 회원가입 성공
                if (passwd == passwdCheck_editText) {
                    if (!response.equals("SignUP fail")) {
                        Toast.makeText(
                            baseContext,
                            String.format("가입을 환영합니다. 로그인 해주세요."),
                            Toast.LENGTH_SHORT
                        ).show()

                        setContentView(R.layout.signup_done)

                        var goSignIn = findViewById<Button>(R.id.goSignInBtn)
                        goSignIn.setOnClickListener {
                            //로그인 페이지로 이동.
                            val intent = Intent(applicationContext, Login::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "비밀번호를 다시 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { Log.d("failed", "error......${error(applicationContext)}") },
            if(basicUserBtn.isChecked) {
                hashMapOf(
                    "id" to id,
                    "device_id" to token,
                    "passwd" to passwd,
                    "nickname" to nickname,
                    "userType" to basicUserBtn.text.toString(),
                    "userDept" to userDept,
                    "opencv" to "",
                    "identity" to identity,
                    "identity_check" to "false",
                    "identity_image" to ""
                )
            } else {
                hashMapOf(
                    "id" to id,
                    "device_id" to token,
                    "passwd" to passwd,
                    "nickname" to nickname,
                    "userType" to corpUserBtn.text.toString(),
                    "userDept" to corpUserBtn.text.toString(),
                    "opencv" to "",
                    "identity" to identity,
                    "identity_check" to "false",
                    "identity_image" to ""
                )
            }

        )
        request.retryPolicy = DefaultRetryPolicy(
            0,
            -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView: View? = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}