package com.example.medi_nion

//import com.example.medi_nion.interface1.SignUp_Request
//import com.example.medi_nion.`object`.RetrofitCilent_Request
//import com.example.medi_nion.dataclass.Data_SignUp_Request
import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.googlecode.tesseract.android.TessBaseAPI
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import retrofit2.*
import java.io.*
import java.lang.reflect.Type
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.sqrt


class Retrofit_SignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    val REQUEST_IMAGE_CAPTURE = 1
    val TAKE_PICTURE = 2

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    lateinit var idImgView: ImageView
    var photoUri: Uri? = null
    lateinit var bitmap: Bitmap
    var image : String = "null"

    lateinit var tess: TessBaseAPI //Tesseract API 객체 생성
    var dataPath: String = "" //데이터 경로 변수 선언
    private var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    val tempImage: String = "" //이미지  OpenCVLoader.initDebug()이름.

    init{
        OpenCVLoader.initDebug()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongThread", "MissingInflatedId", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        //test

        OpenCVLoader.initDebug()

        //키보드 숨기기
        val focusView: View? = currentFocus
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }

        val camera_permission_btn = findViewById<Button>(R.id.id_verify_btn)
        camera_permission_btn.setOnClickListener {
            Log.d("0-09123","permission1")
            val cameraPermissionCheck = ContextCompat.checkSelfPermission(
                this@Retrofit_SignUp,
                android.Manifest.permission.CAMERA
            )
            if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    1000
                )
            } else { //권한이 있는 경우
                Log.d("0-09123","permission2")
                openCamera()
            }
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
                findViewById<RadioGroup>(R.id.basicUser_RadioGroup) // 일반회원의 종류를 담은 RadioGroup, RadioButton
            basicUserGroup.visibility = View.GONE

            //키보드 숨기기
            imm.hideSoftInputFromWindow(corpUserBtn.getWindowToken(), 0)

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
                            if (id_editText.isEmpty()) {
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

//        passwdCheck_editText.setOnClickListener {
//            Log.d("passwd", "1")
//            passwdCheck_warning.visibility = View.VISIBLE
//
//            val passwdInput = passwd_editText.text.toString()
//            val passwdCheckInput = passwdCheck_editText.text.toString()
//            if (passwdCheckInput != passwdInput) {
//                passwdCheck_warning.setTextColor(Color.RED)
//                passwdCheck_warning.text = "비밀번호가 동일하지 않습니다."
//            } else {
//                passwdCheck_warning.setTextColor(Color.BLUE)
//                passwdCheck_warning.text = "올바른 비밀번호입니다."
//            }
//        }


        var signUpButton = findViewById<Button>(R.id.signUpBtn)
        signUpButton.setOnClickListener {
            val validate = true
            if (TextUtils.isEmpty(nickname_editText.text.toString()) ||
                TextUtils.isEmpty(id_editText.text.toString()) ||
                TextUtils.isEmpty(passwd_editText.text.toString()) ||
                TextUtils.isEmpty(passwdCheck_editText.text.toString()) ||
                TextUtils.isEmpty(passwdCheck_editText.text.toString())||
                TextUtils.isEmpty(identity.text.toString())||
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

                setContentView(R.layout.signup_done)

                var goSignIn = findViewById<Button>(R.id.goSignInBtn)
                goSignIn.setOnClickListener {
                    //로그인 페이지로 이동.
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                }
            }
        }

        //val tempImage : File = null

        storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setViews()
            } else {
//                Toast.makeText(baseContext, "권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }

        cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("0-09123", "oepn Cmaera")
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

                val matrix = ColorMatrix()
                matrix.setSaturation(0F)

                val filter = ColorMatrixColorFilter(matrix)
                idImgView.colorFilter = filter
                idImgView.setImageURI(photoUri)


                doOCR()
//                if (!OpenCVLoader.initDebug()) {
//                    Log.d(
//                        "OpenCV",
//                        "Internal OpenCV library not found. Using OpenCV Manager for initialization"
//                    )
//                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
//                } else {
//                    Log.d("OpenCV", "OpenCV library found inside package. Using it!")
//                    mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
//                }


//                idImgView.setImageBitmap(bitmap)
//                processImage(bitmap) //이미지 가공후 텍스트뷰에 띄우기
//                processImage(BitmapFactory.decodeResource(resources,R.drawable.sample_kor)) //이미지 가공후 텍스트뷰에 띄우기




            }
        }

        storagePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOCR() {
        try {
            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(
                    contentResolver,
                    photoUri!!
                )

                ImageDecoder.decodeBitmap(
                    source
                ) { decoder: ImageDecoder, _: ImageInfo?, _: ImageDecoder.Source? ->
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        //흑백 영상으로 전환
        Log.d("ggdf", "흑백")
        val graySrc = Mat()
        Imgproc.cvtColor(mat, graySrc, Imgproc.COLOR_RGB2GRAY)

        //2.이진화
        Log.d("tess-two", "이진화")
        val binarySrc = Mat()
        Imgproc.threshold(graySrc, binarySrc, 0.0, 255.0, Imgproc.THRESH_OTSU)
        Log.d("binary", binarySrc.toString())

        //3. 윤곽선 검출
        // 윤곽선 찾기
        Log.d("tess-two", "윤곽선")
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            binarySrc,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        //3-1 가장 면적이 큰 윤곽선 검충 (검출하고자 하는 문서가 가장 큰 피사체)
        // 가장 면적이 큰 윤곽선 찾기
        var biggestContour: MatOfPoint? = null
        var biggestContourArea: Double = 0.0
        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > biggestContourArea) {
                biggestContour = contour
                biggestContourArea = area
            }
        }

        if (biggestContour == null) {
            throw IllegalArgumentException("No Contour")
        }
        // 너무 작아도 안됨
        if (biggestContourArea < 400) {
            throw IllegalArgumentException("too small")
        }

        //3-2 근사화하기 (도형의 꼭짓점을 더 명확하게 찾기)
        val candidate2f = MatOfPoint2f(*biggestContour.toArray())
        val approxCandidate = MatOfPoint2f()
        Imgproc.approxPolyDP(
            candidate2f,
            approxCandidate,
            Imgproc.arcLength(candidate2f, true) * 0.02,
            true
        )

        //3-3 사각형인지 판별 -> 신분증은 사각형이므로..
        // 사각형 판별
        if (approxCandidate.rows() != 4) {
            Toast.makeText(applicationContext, "신분증 인식이 명확하지 않습니다\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
            openCamera()
            Log.d("newjeans", "omomgot")
            //throw java.lang.IllegalArgumentException("It's not rectangle")
        }
        // 컨벡스(볼록한 도형)인지 판별
        if (!Imgproc.isContourConvex(MatOfPoint(*approxCandidate.toArray()))) {
            Toast.makeText(applicationContext, "신분증 인식이 명확하지 않습니다\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
            openCamera()
            Log.d("newjeans1", "omomgot1")
            //throw java.lang.IllegalArgumentException("It's not convex")
        }

        //4. 투시변환
        // 좌상단부터 시계 반대 방향으로 정점을 정렬한다.
        Log.d("tess-two", "투시")
        val points = arrayListOf(
            org.opencv.core.Point(approxCandidate.get(0, 0)[0],
                approxCandidate.get(0, 0)[1]
            ),
            org.opencv.core.Point(approxCandidate.get(1, 0)[0],
                approxCandidate.get(1, 0)[1]
            ),
            org.opencv.core.Point(approxCandidate.get(2, 0)[0],
                approxCandidate.get(2, 0)[1]
            ),
            org.opencv.core.Point(approxCandidate.get(3, 0)[0],
                approxCandidate.get(3, 0)[1]
            ),
        )
        points.sortBy { it.x } // x좌표 기준으로 먼저 정렬

        if (points[0].y > points[1].y) {
            val temp = points[0]
            points[0] = points[1]
            points[1] = temp
        }

        if (points[2].y < points[3].y) {
            val temp = points[2]
            points[2] = points[3]
            points[3] = temp
        }
        // 원본 영상 내 정점들
        val srcQuad = MatOfPoint2f().apply { fromList(points) }

        val maxSize = calculateMaxWidthHeight(
            tl = points[0],
            bl = points[1],
            br = points[2],
            tr = points[3]
        )
        val dw = maxSize.width
        val dh = dw * maxSize.height/maxSize.width
        val dstQuad = MatOfPoint2f(
            org.opencv.core.Point(0.0, 0.0),
            org.opencv.core.Point(0.0, dh),
            org.opencv.core.Point(dw, dh),
            org.opencv.core.Point(dw, 0.0)
        )

        // 투시변환 매트릭스 구하기
        val perspectiveTransform = Imgproc.getPerspectiveTransform(srcQuad, dstQuad)

        //사진 자르기?


        // 투시변환 된 결과 영상 얻기
        val dst = Mat()
        Imgproc.warpPerspective(mat, dst, perspectiveTransform, Size(dw, dh))


        Log.d("tess-two", "ocr")
        Toast.makeText(applicationContext, "신분증 인식에 2~3분정도\n소요됩니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
        printOCRResult(dst)
    }

    // 사각형 꼭짓점 정보로 사각형 최대 사이즈 구하기
// 평면상 두 점 사이의 거리는 직각삼각형의 빗변길이 구하기와 동일
    private fun calculateMaxWidthHeight(
        tl:org.opencv.core.Point,
        tr:org.opencv.core.Point,
        br:org.opencv.core.Point,
        bl:org.opencv.core.Point,
    ): Size {
        // Calculate width
        val widthA = sqrt(((tl.x - tr.x) * (tl.x - tr.x) + (tl.y - tr.y) * (tl.y - tr.y)))
        val widthB = sqrt(((bl.x - br.x) * (bl.x - br.x) + (bl.y - br.y) * (bl.y - br.y)))
        val maxWidth = max(widthA, widthB)
        // Calculate height
        val heightA = sqrt(((tl.x - bl.x) * (tl.x - bl.x) + (tl.y - bl.y) * (tl.y - bl.y)))
        val heightB = sqrt(((tr.x - br.x) * (tr.x - br.x) + (tr.y - br.y) * (tr.y - br.y)))
        val maxHeight = max(heightA, heightB)
        return Size(maxWidth, maxHeight)
    }


//    fun printOCRResult(src: Mat){
//        with(TessBaseAPI()){
//            val result = findViewById<TextView>(R.id.text_result)
//            dataPath = "$filesDir/tesseracts/"
//            checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
//            checkFile(File(dataPath + "tessdata/"), "eng")
//            init(dataPath, "kor+eng")
//
//            val dst = Mat()
//            Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2RGB)
//            Log.d("scr", src.toString())
//            val bitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
//            Utils.matToBitmap(dst, bitmap)
//            Log.d("dst", dst.toString())
//            setImage(bitmap)
//            result.text = utF8Text
//            Log.e("NameCardProcessor","utF8Text :\n$utF8Text")
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printOCRResult(src: Mat) {
        with(TessBaseAPI()) {
            val result = findViewById<TextView>(R.id.text_result)
            dataPath = "$filesDir/tesseracts/"
            checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
            checkFile(File(dataPath + "tessdata/"), "eng")
            init(dataPath, "kor+eng")

            // Improve image quality by adjusting brightness and contrast
            val contrast = 1.5
            val brightness = 20.0
            Core.addWeighted(src, contrast, Mat.zeros(src.size(), src.type()), 0.0, brightness, src)

            // Apply image preprocessing techniques
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
            Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 0.0)
            Imgproc.threshold(src, src, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU)
            Imgproc.medianBlur(src, src, 3)

            // Set page segmentation mode to treat the image as a single block of text
            pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK

            // Set OCR engine mode to use LSTM-based OCR engine for better accuracy
            //val ocrEngineMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK
            val ocrEngineMode = TessBaseAPI.PageSegMode.PSM_SINGLE_COLUMN
            pageSegMode = ocrEngineMode
            val tessdataDir = File("$filesDir/tessdata/")
            val engTrainedData = File(tessdataDir, "eng.traineddata")
            val korTrainedData = File(tessdataDir, "kor.traineddata")
            if (engTrainedData.exists() && korTrainedData.exists()) {
                val lang = "kor+eng"
                setVariable(
                    TessBaseAPI.VAR_CHAR_WHITELIST,
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
                )
                init(filesDir.absolutePath + "/tessdata/", lang)
            } else {
                Log.e("printOCRResult", "Trained data files are missing")
            }

            // Recognize text and set text result to the TextView
            //setImage(src)
            //recognize(null)
            setImage(bitmap)
            bitmap = resize(bitmap)!!
            image = BitMapToString(bitmap)
            result.text = utF8Text

            Log.e("NameCardProcessor","utF8Text :\n$utF8Text")
        }
    }


    //db 연동 시작
    @SuppressLint("HardwareIds")
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
        var identity1 = ""
        var identity2 = ""
        var idimgView = findViewById<ImageView>(R.id.idImgView)
        var identity_image1 = ""
        var identity_image2 = ""

        var token = ""


        identity_opencv = identity_before.replace("\n", "")
        identity_opencv = identity_opencv.replace(" ", "")
        identity1 = identity_opencv.substring(0, identity_opencv.length/2+1)
        identity2 = identity_opencv.substring(identity_opencv.length/2+1, identity_opencv.length)
        identity_check = identity_opencv.contains(identity).toString()
        identity_image1 = image.substring(0,image.length/2+1)
        identity_image2 = image.substring(image.length/2+1,image.length)

        Log.d("identity", "$identity, $identity_opencv, $identity_check")

        val android_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("ANDROID_ID", android_id)

        var spinner = findViewById<Spinner>(R.id.userDept_spinner)
        var userDept = spinner.selectedItem.toString()

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
        val noti_FCM = "http://seonho.dothome.co.kr/notification_FCM.php"

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("캬캬ㅑ캬", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            token = task.result
//
//            // Log and toast
//            Log.d("캬캬ㅑ캬", token)
//
////                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        })
        val request = SignUP_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("REPSON", response.toString())
                //비밀번호와 비밀번호 확인이 같으면 회원가입 성공
                if (passwd == passwdCheck_editText) {
                    if (!response.equals("SignUP fail")) {

                        Log.d("SignUP", "SUCCESS")

                        Toast.makeText(
                            baseContext,
                            String.format("가입을 환영합니다. 로그인 해주세요."),
                            Toast.LENGTH_SHORT
                        ).show()
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
                    "identity" to identity,
                    "identity_opencv1" to identity1,
                    "identity_opencv2" to identity2,
                    "identity_check" to identity_check,
                    "identity_image1" to identity_image1,
                    "identity_image2" to identity_image2
                )
            } else {
                hashMapOf(
                    "id" to id,
                    "device_id" to token,
                    "passwd" to passwd,
                    "nickname" to nickname,
                    "userType" to corpUserBtn.text.toString(),
                    "userDept" to corpUserBtn.text.toString(),
                    "identity" to identity,
                    "identity_opencv1" to identity1,
                    "identity_opencv2" to identity2,
                    "identity_check" to identity_check,
                    "identity_image1" to identity_image1,
                    "identity_image2" to identity_image2
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

    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val grayBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f)
        }
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayBitmap
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bitmap compress
        val arr = baos.toByteArray()
        val base64Image = Base64.encodeToString(arr, Base64.DEFAULT)
//        findViewById<TextView>(R.id.imageSrc).text = arr.toString()
//        val image: ByteArray? = Base64.encode(arr,0)
//        val image: String = getEncoder(arr)
        var temp = ""
        try {
            //temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return base64Image
    }

    private fun resize(bitmap: Bitmap): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val config: Configuration = Resources.getSystem().configuration
        var bitmap_width : Int? = bitmap?.width
        var bitmap_height : Int? = bitmap?.height

        bitmap = Bitmap.createScaledBitmap(bitmap!!, 240, 480, true)
        Log.d("please", "$bitmap_height, $bitmap_width")
        return bitmap
    }

}