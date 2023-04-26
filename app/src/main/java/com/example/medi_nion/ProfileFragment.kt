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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.databinding.ProfileBinding
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ProfileFragment : Fragment(R.layout.profile) {
    private var mBinding: ProfileBinding? = null
    private var isChan: Boolean = false

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    var photoUri: Uri? = null
    var image : String = "null"
    lateinit var bitmap: Bitmap

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = ProfileBinding.inflate(inflater, container, false)

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

        var identity_img = view?.findViewById<ImageView>(R.id.identity_imageView)
        var identity_warningImage = view?.findViewById<ImageView>(R.id.identity_warning)

        cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("01503", "oepn Cmaera")
                openCamera()
            } else {
                Toast.makeText(context, "권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            identity_img?.setImageURI(photoUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bitmap = photoUri?.let { it1 -> ImageuriToBitmap(it1) }!!
                image = BitMapToString(bitmap)
            }
            Log.d("phto", "uri")
        }

        binding.textViewNickname.text = nickname
        binding.textViewDept.text = userDept
        binding.textViewJob.text = userType
        fetchGrade()
        fetchIdentity()

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
            intent.putExtra("userGrade", userGrade)
            intent.putExtra("board", "자유 게시판")
            startActivity(intent)
        }

        //내 좋아요 ->
        binding.item1List3Text.setOnClickListener{
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
        binding.item1List4Text.setOnClickListener{
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
        binding.item1List2Text.setOnClickListener{
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
        binding.item1List5Text.setOnClickListener{
            val intent = Intent(context, Business_bookmark__profile::class.java)
            intent.putExtra("id", id)
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

        //비밀번호 설정
        binding.item3List1Text.setOnClickListener {
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
        binding.item3List2Text.setOnClickListener {
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
        binding.item3List3Text.setOnClickListener {
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
            val id = arguments?.getString("id")
            val intent = Intent(context, BusinessSubList::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        // 채널 있는지 검사
        fetchifChan(id.toString())

        binding.item4List2Text.setOnClickListener{
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
                                if(hasPermission(activity, android.Manifest.permission.CAMERA)) {
                                    openCamera()
                                    Log.d("123456", "789")
                                    updateIdentity()
                                } else {
                                    setViews()
                                    Log.d("789", "789")
                                }
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

    fun updateIdentity() {
        val id = arguments?.getString("id").toString()
        val updateidentityurl = "http://seonho.dothome.co.kr/identity_update.php"
        var img1 : String = ""
        var img2 : String = ""

        if(image != "null"){
            img1 = image.substring(0,image.length/2+1)
            img2 = image.substring(image.length/2+1,image.length)
        }

        val request = Login_Request(
            Request.Method.POST,
            updateidentityurl,
            { response ->
                Log.d("idendkd", response.toString())
                if(!response.equals("updateIdentity fail")) {
                    Toast.makeText(context, "신분증을 분석합니다. 최대 3일 소요됩니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "신분증을 다시 촬영해주세요.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Log.d(
                    "Indentity update failed",
                    "error......${context?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf(
                "id" to id,
                "identity_image1" to img1,
                "identity_image2" to img2
            )
        )
        val queue = Volley.newRequestQueue(context)
        queue.add(request)

    }

    fun hasPermission(context: FragmentActivity?, permission: String): Boolean {
        val cameraPermissionCheck = context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1,
                android.Manifest.permission.CAMERA
            )
        }
        return if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            Log.d("LDF", "DLF")
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
            false
        } else { //권한이 있는 경우
            Log.d("dsfs","dfs")
            true
        }
    }

    //카메라 권한 요청
    fun setViews() {
        binding.identityWarning.setOnClickListener {
            cameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        Log.d("123", "456")

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        photoUri = context?.let {
            FileProvider.getUriForFile(
                it, "${context?.packageName}.provider", photoFile
            )
        }
        cameraLauncher.launch(photoUri)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ImageuriToBitmap(photouri: Uri): Bitmap {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = context?.let {
                ImageDecoder.createSource(
                    it.contentResolver,
                    photoUri!!
                )
            }?.let { ImageDecoder.decodeBitmap(it) }!!
            } else {
                MediaStore.Images.Media.getBitmap(context?.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
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

