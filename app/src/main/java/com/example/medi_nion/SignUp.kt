package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.toolbox.*
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.*
import java.util.regex.Pattern


class SignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    val REQUEST_IMAGE_CAPTURE = 1

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    lateinit var bitmap: Bitmap
    lateinit var idImgView: ImageView
    var photoUri: Uri? = null

    lateinit var tess: TessBaseAPI //Tesseract API 객체 생성
    var dataPath: String = "" //데이터 경로 변수 선언
    private var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    val tempImage: String = "" //이미지 이름.

    @SuppressLint("WrongThread", "MissingInflatedId", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        //test

        //키보드 숨기기
        val focusView: View? = currentFocus
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }


        //프래그먼트일때는
        //InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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


        //라디오 버튼들 clickListener -> group으로 안하는 이유는 check가 바뀌었을 때 말고도 버튼만 누르면 일반회원의 종류가 나열되게 하기 위하여.
        basicUserBtn.setOnClickListener {
            val basicUserGroup =
                findViewById<RadioGroup>(R.id.basicUser_RadioGroup); // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.VISIBLE // 일반회원의 종류를 담은 RadioGroup 활성화
            informView.text = "" //회원 종류에 따른 안내멘트 초기화

            //키보드 숨기기
            imm.hideSoftInputFromWindow(basicUserBtn.getWindowToken(), 0);

            basicDocBtn.setOnClickListener {
                userType = "doctor"
                informView.text = informAll
                basicUserBtn.text = "의사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicNurBtn.setOnClickListener {
                userType = "nurse"
                informView.text = informAll
                basicUserBtn.text = "간호사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicTechBtn.setOnClickListener {
                userType = "mediTech"
                informView.text = informConst
                basicUserBtn.text = "의료기사"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicOffBtn.setOnClickListener {
                userType = "office"
                informView.text = informConst
                basicUserBtn.text = "사무직"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

            basicStuBtn.setOnClickListener {
                userType = "student"
                informView.text = informConst
                basicUserBtn.text = "학생"

                basicUserGroup.visibility = View.GONE //일반회원의 종류를 보여주는 RadioGroup 없애기
            }

        }

        corpUserBtn.setOnClickListener {
            val basicUserGroup =
                findViewById<RadioGroup>(R.id.basicUser_RadioGroup); // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.GONE

            //키보드 숨기기
            imm.hideSoftInputFromWindow(corpUserBtn.getWindowToken(), 0);

            userType = "corp"
            basicUserBtn.text = "일반 회원"
            informView.text = informCorp
        }

        signUpDetail.setOnClickListener {
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var nickname_editText =
                        findViewById<EditText>(R.id.nickname_editText).text.toString()
                    var url_nicknamevalidate = "http://seonho.dothome.co.kr/nicknameValidate.php"
                    var request = SignUP_Request(
                        Request.Method.POST,
                        url_nicknamevalidate,
                        { response ->
                            if(nickname_editText.isEmpty()) {
                                nickname_warning.setTextColor(Color.RED)
                                nickname_warning.text = "닉네임을 입력해주세요."
                            }
                            if (response.equals("nickname_Validate Success")) {
                                nickname_editText = response.toString()

                                nickname_warning.setTextColor(Color.BLUE)
                                nickname_warning.text = "사용가능한 닉네임입니다."

                                Log.d(
                                    "nickname_validate",
                                    nickname_editText
                                )
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
                            if(id_editText.isEmpty()) {
                                id_warning.text = "아이디를 입력해주세요."
                                id_warning.setTextColor(Color.RED)
                            }
                            if (response.equals("id_Validate Success")) {
                                id_editText = response.toString()
                                id_warning.setTextColor(Color.BLUE)
                                id_warning.text = "사용가능한 아이디입니다."

                                Log.d(
                                    "id_validate",
                                    "$id_editText"
                                )
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
        passwd_editText.addTextChangedListener {
            passwd_warning.visibility = View.VISIBLE

            val passwdInput = passwd_editText.text
            if (!Pattern.matches(
                    "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,15}\$",
                    passwdInput
                )
            ) {
                passwd_warning.setTextColor(Color.RED);
                passwd_warning.text = "비밀번호 형식이 올바르지 않습니다.\n(숫자, 문자, 특수문자 중 2가지 포함(8~15자))"
            } else {
                passwd_warning.setTextColor(Color.parseColor("#85D6A4"))
                passwd_warning.text = "올바른 비밀번호입니다."
            }

        }

        //비밀번호 정확성 확인
        passwdCheck_editText.addTextChangedListener {
            passwdCheck_warning.visibility = View.VISIBLE

            val passwdInput = passwd_editText.text.toString()
            val passwdCheckInput = passwdCheck_editText.text.toString()
            if (!passwdCheckInput.equals(passwdInput)) {
                passwdCheck_warning.setTextColor(Color.RED);
                passwdCheck_warning.text = "비밀번호가 동일하지 않습니다."
            } else {
                passwdCheck_warning.setTextColor(Color.parseColor("#85D6A4"));
                passwdCheck_warning.text = "올바른 비밀번호입니다."
            }
        }


        var signUpButton = findViewById<Button>(R.id.signUpBtn)
        signUpButton.setOnClickListener {
            val validate = true
//            if (TextUtils.isEmpty(nickname_editText.text.toString()) ||
//                TextUtils.isEmpty(id_editText.text.toString()) ||
//                TextUtils.isEmpty(passwd_editText.text.toString()) ||
//                TextUtils.isEmpty(passwdCheck_editText.text.toString()) ||
//                (!basicUserBtn.isChecked && !corpUserBtn.isChecked)
//            ) {
//
//                var notDone_warning = findViewById<TextView>(R.id.notDone_warning)
//                notDone_warning.visibility = View.VISIBLE
//
//                Handler(Looper.getMainLooper()).postDelayed({
//                    notDone_warning.visibility = View.INVISIBLE
//                }, 2000)
            if (!validate) {
                Toast.makeText(baseContext,
                    "중복된 아이디와 닉네임이 있는지 확인해주세요.",
                    Toast.LENGTH_SHORT).show()
            }

//            } else {
                val url_SignUP = "http://seonho.dothome.co.kr/SignUP.php"


                signUPRequest(url_SignUP)

                setContentView(R.layout.signup_done)

                var goSignIn = findViewById<Button>(R.id.goSignInBtn)
                goSignIn.setOnClickListener {
                    //로그인 페이지로 이동.
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                }
//            }
        }

        //val tempImage : File = null

        storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setViews()
            } else {
                Toast.makeText(baseContext, "권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openCamera()
            } else {
                Toast.makeText(baseContext, "권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            idImgView = findViewById(R.id.idImgView)
            idImgView.visibility = View.VISIBLE
            if (it) {

                idImgView.setImageURI(photoUri)

                dataPath = "$filesDir/tesseract/" //언어데이터의 경로 미리 지정

                checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
                checkFile(File(dataPath + "tessdata/"), "eng")

                val lang: String = "kor+eng"
                tess = TessBaseAPI() //api준비
                tess.init(dataPath, lang) //해당 사용할 언어데이터로 초기화


                // OCR 동작 버튼
                lateinit var bitmap: Bitmap
                try {
                    bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                contentResolver,
                                photoUri!!
                            )
                        )
                    } else {
                        MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                idImgView.setImageBitmap(bitmap)
                processImage(bitmap) //이미지 가공후 텍스트뷰에 띄우기
//                processImage(BitmapFactory.decodeResource(resources,R.drawable.sample_kor)) //이미지 가공후 텍스트뷰에 띄우기
            }
        }

        storagePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


    //db 연동 시작
    private fun signUPRequest(url: String) {

        var basicUserBtn = findViewById<RadioButton>(R.id.basicUser_RadioBtn)
        var corpUserBtn = findViewById<RadioButton>(R.id.corpUser_RadioBtn)

        var nickname_editText = findViewById<EditText>(R.id.nickname_editText).text.toString()
        var id_editText = findViewById<EditText>(R.id.id_editText).text.toString()
        var passwd_editText = findViewById<EditText>(R.id.passwd_editText).text.toString()
        var passwdCheck_editText = findViewById<EditText>(R.id.passwdCheck_editText).text.toString()


        //POST 방식으로 db에 데이터 전송
        val request = SignUP_Request(
            Request.Method.POST,
            url,
            { response ->
                val success = true

                //비밀번호와 비밀번호 확인이 같으면 회원가입 성공
                if (passwd_editText == passwdCheck_editText) {
                    if (success) {
                        nickname_editText = response.toString()
                        id_editText = response.toString()
                        passwd_editText = response.toString()
                        basicUserBtn.text = response.toString()
                        corpUserBtn.text = response.toString()

                        Toast.makeText(
                            baseContext,
                            String.format("가입을 환영합니다. 로그인 해주세요."),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "success",
                            "$nickname_editText, $id_editText, $passwd_editText, $basicUserBtn, $corpUserBtn"
                        )
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
                hashMapOf("nickname" to nickname_editText,
                    "id" to id_editText,
                    "passwd" to passwd_editText,
                    "userType" to basicUserBtn.text.toString()
                )
            } else {
                hashMapOf("nickname" to nickname_editText,
                    "id" to id_editText,
                    "passwd" to passwd_editText,
                    "userType" to corpUserBtn.text.toString()
                )
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
        Log.d("sssssssssssssss", "$queue")
    }
    //db 연동 끝

    private fun copyFile(lang: String) {
        try {
            //언어데이타파일의 위치
            val filePath: String = "$dataPath/tessdata/$lang.traineddata"

            //AssetManager를 사용하기 위한 객체 생성
            val assetManager: AssetManager = assets;

            //byte 스트림을 읽기 쓰기용으로 열기
            val inputStream: InputStream = assetManager.open("tessdata/$lang.traineddata")
            val outStream: OutputStream = FileOutputStream(filePath)


            //위에 적어둔 파일 경로쪽으로 해당 바이트코드 파일을 복사한다.
            val buffer = ByteArray(1024)

            var read: Int = 0
            read = inputStream.read(buffer)
            while (read != -1) {
                outStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outStream.flush()
            outStream.close()
            inputStream.close()

        } catch (e: FileNotFoundException) {
            Log.v("오류발생", e.toString())
        } catch (e: IOException) {
            Log.v("오류발생", e.toString())
        }

    }

    /***
     *  언어 데이터 파일이 기기에 존재하는지 체크하는 기능
     *  @param dir: 언어 데이터 파일 경로
     *  @param lang: 언어 종류 데이터 파일
     *
     *  -> 파일 없으면 파일 생성
     *  -> 있으면 언어 종류 데이터 파일 복사
     */
    private fun checkFile(dir: File, lang: String) {

        //파일의 존재여부 확인 후 내부로 복사
        if (!dir.exists() && dir.mkdirs()) {
            copyFile(lang)
        }

        if (dir.exists()) {
            val datafilePath: String = "$dataPath/tessdata/$lang.traineddata"
            val dataFile: File = File(datafilePath)
            if (!dataFile.exists()) {
                copyFile(lang)
            }
        }

    }

    /***
     *  이미지에서 텍스트 추출해서 결과뷰에 보여주는 기능
     *  @param bitmap: 이미지 비트맵
     */
    private fun processImage(bitmap: Bitmap) {
        Toast.makeText(applicationContext, "잠시 기다려 주세요", Toast.LENGTH_LONG).show()
        var ocrResult: String? = null;
//        tess.setImage(photoFile)
        tess.setImage(bitmap.copy(Bitmap.Config.ARGB_8888, true))
        ocrResult = tess.utF8Text
        val result = findViewById<TextView>(R.id.text_result)
        result.text = ocrResult
    }


    private fun setViews() {
        val verifyingBtn = findViewById<Button>(R.id.id_verify_btn)
        verifyingBtn.setOnClickListener {

            cameraPermission.launch(android.Manifest.permission.CAMERA)
        }

    }

    private fun openCamera() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        photoUri = FileProvider.getUriForFile(
            this, "${packageName}.provider", photoFile
        )

        cameraLauncher.launch(photoUri)
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

