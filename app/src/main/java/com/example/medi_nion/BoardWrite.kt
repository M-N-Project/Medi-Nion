package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.Retrofit2_Dataclass.Data_CreateBoard_Request
import com.example.medi_nion.Retrofit2_Interface.Create_Board_Request
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import com.example.medi_nion.Retrofit2_Dataclass.Data_UpdateBoard
import com.example.medi_nion.Retrofit2_Interface.UpdateBoard_Request
import kotlinx.android.synthetic.main.business_board_item.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class BoardWrite : AppCompatActivity() {

    private val GALLERY = 1
    lateinit var ImageData : Uri
    var image : String = "null"
    var flagUpdate = "false"


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)

        var id = intent.getStringExtra("id")
        var board = intent.getStringExtra("board").toString()
        val update = intent.getIntExtra("update", 0)
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()

        //수정으로 글쓰기 화면 넘어왔을 때
        if(update == 1){
            flagUpdate = "true"
            val title = intent.getStringExtra("title").toString()
            val content = intent.getStringExtra("content").toString()
            val image = intent.getStringExtra("image").toString()
            val post_num = intent.getStringExtra("post_num").toString()

            var editText_title = findViewById<EditText>(R.id.editText_Title)
            var editText_content = findViewById<EditText>(R.id.editText_Content)
            val post_img = findViewById<ImageView>(R.id.imageButton_gallery)

            editText_title.setText(title)
            editText_content.setText(content)

            if(image!=null){
                val bitmap: Bitmap? = StringToBitmaps(image)
                post_img.setImageBitmap(bitmap)
            }

        }

        var imgbtn = findViewById<ImageButton>(R.id.imageButton_gallery)
        var postTitle = findViewById<EditText>(R.id.editText_Title)
        var postContent = findViewById<EditText>(R.id.editText_Content)
        var postbtn = findViewById<Button>(R.id.post_Btn)
        var board_select = findViewById<TextView>(R.id.board_select)
        var select_RadioGroup = findViewById<RadioGroup>(R.id.select_RadioGroup)
        var free_RadioBtn = findViewById<RadioButton>(R.id.free_RadioBtn)
        var job_RadioBtn = findViewById<RadioButton>(R.id.job_RadioBtn)
        var department_RadioBtn = findViewById<RadioButton>(R.id.department_RadioBtn)
        var my_hospital_RadioBtn = findViewById<RadioButton>(R.id.my_hospital_RadioBtn)
        var market_RadioBtn = findViewById<RadioButton>(R.id.market_RadioBtn)
        var QnA_RadioBtn = findViewById<RadioButton>(R.id.QnA_RadioBtn)

        val url_Post = "http://seonho.dothome.co.kr/createBoard.php"

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //intent.setType("image/*)
            startActivityForResult(intent, GALLERY)
        }

        board_select.text = board

        board_select.setOnClickListener {
            if(select_RadioGroup.visibility == View.GONE){
                select_RadioGroup.visibility = View.VISIBLE
                select_RadioGroup.bringToFront()
            }
            else select_RadioGroup.visibility = View.GONE


            free_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "자유 게시판"
            }

            job_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "직종별 게시판"
            }

            department_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "진료과별 게시판"
            }

            my_hospital_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "우리 병원 게시판"
            }

            market_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "장터 게시판"
            }

            QnA_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "QnA 게시판"
            }
        }

        postbtn.setOnClickListener {
            if(postTitle.length() == 0 || postContent.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("경고")
                    .setMessage("제목 또는 내용을 입력해주세요.")
                    .setNegativeButton("닫기"
                    ) { dialog, which ->
                    }
                builder.show()
            } else {
                createBoardRequest(url_Post)
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBoardRequest(postUrl: String) {
        var updateUrl = "http://seonho.dothome.co.kr/updateBoard.php"
        var id = intent?.getStringExtra("id").toString()
        var board = intent.getStringExtra("board").toString()
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()
        var postTitle = findViewById<EditText>(R.id.editText_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.editText_Content).text.toString()
        var board_select = findViewById<TextView>(R.id.board_select).text.toString()
//        var image = findViewById<ImageButton>(R.id.imageButton_gallery).toString()
        var imageSrc = findViewById<TextView>(R.id.imageSrc).text.toString()

        var select_RadioGroup = findViewById<RadioGroup>(R.id.select_RadioGroup)

        val currentTime = Calendar.getInstance().time
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val current: String = format.format(currentTime)

        var img1 : String = ""
        var img2 : String = ""
        if(image != "null"){
            img1 = image.substring(0,image.length/2+1)
            img2 = image.substring(image.length/2+1,image.length)
        }

        if(flagUpdate == "true"){
            val post_num = intent.getStringExtra("post_num").toString()


            val gson = GsonBuilder().setLenient().create()
            val uri = "http://seonho.dothome.co.kr/"

            val retrofit = createOkHttpClient()?.let {
                Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(it)
                    .build()
            }
            val server = retrofit?.create(UpdateBoard_Request::class.java)

            val call : Call<Data_UpdateBoard>? = server?.updateBoard(id, board_select, post_num, postTitle, postContent, currentTime.toString(), img1, img2)
            if (call != null) {
                call.clone()
                    ?.enqueue(object :
                        Callback<Data_UpdateBoard> {
                        override fun onFailure(call: Call<Data_UpdateBoard>, t: Throwable) {
                            t.localizedMessage?.let { Log.d("retrofit3 fail", it) }
                            Toast.makeText(applicationContext, "수정 실패", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<Data_UpdateBoard>,
                            response: Response<Data_UpdateBoard>
                        ) {
                            //if (!response.equals("SignUP fail")) {
                            Log.d("retrofit3 success", response.toString())
                            Toast.makeText(applicationContext, "수정 성공", Toast.LENGTH_SHORT).show()

                            var intent = Intent(applicationContext, Board::class.java)
                            intent.putExtra("id", id)
                            intent.putExtra("board", board)
                            Log.d("게시물 수정 완료", userDept)
                            intent.putExtra("userType", userType)
                            intent.putExtra("userDept", userDept)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                            startActivity(intent)
                            //}
                        }

                    })
            }

//            val request = Upload_Request(
//                Request.Method.POST,
//                updateUrl,
//                { response ->
//                    //Log.d("11??", response)
//                    if (!response.equals("upload fail")) {
//                        Toast.makeText(
//                            baseContext,
//                            String.format("게시물 업로드가 완료되었습니다."),
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        var intent = Intent(applicationContext, Board::class.java)
//                        intent.putExtra("id", id)
//                        intent.putExtra("board", board)
//                        Log.d("게시물 수정 완료", userDept)
//                        intent.putExtra("userType", userType)
//                        intent.putExtra("userDept", userDept)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
//                        startActivity(intent)
//
//                    } else {
//                        Toast.makeText(
//                            applicationContext,
//                            "게시물 업로드가 실패했습니다.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                },
//                { Log.d("failed", "error......${error(applicationContext)}") },
//                mutableMapOf(
//                    "id" to id,
//                    "board" to board_select,
//                    "post_num" to post_num,
//                    "title" to postTitle,
//                    "content" to postContent,
//                    "time" to currentTime.toString(),
//                    "image1" to img1,
//                    "image2" to img2
//                )
//            )
//            val queue = Volley.newRequestQueue(this)
//            queue.add(request)
        } //if문끝
        else {

            val gson = GsonBuilder().setLenient().create()
            val uri = "http://seonho.dothome.co.kr/"

            val retrofit = createOkHttpClient()?.let {
                Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(nullOnEmptyConverterFactory)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(it)
                    .build()
            }

            val server = retrofit?.create(Create_Board_Request::class.java)

            val call : Call<Data_CreateBoard_Request>? = server?.Create_Board(flagUpdate, id, board_select, postTitle, postContent, img1, img2)
//                    "userType" to userType,
//                    "userDept" to userDept

            call?.enqueue(object :
                Callback<Data_CreateBoard_Request> {
                override fun onFailure(call: Call<Data_CreateBoard_Request>, t: Throwable) {
                    t.localizedMessage?.let { Log.d("createBoard fail", it) }
                    Toast.makeText(applicationContext, "createBoard fail", Toast.LENGTH_SHORT).show()
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(
                    call: Call<Data_CreateBoard_Request>,
                    response: Response<Data_CreateBoard_Request>
                ) {
                    Log.d("createBoard success", response.toString())
                    Toast.makeText(applicationContext, "createBoard success", Toast.LENGTH_SHORT).show()

                    var intent = Intent(applicationContext, Board::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("board", board)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                        startActivity(intent)
                }
            })

//            val request = Upload_Request(
//                Request.Method.POST,
//                postUrl,
//                { response ->
//                    Log.d("11??", response)
//                    if (!response.equals("upload fail")) {
//                        Toast.makeText(
//                            baseContext,
//                            String.format("게시물 업로드가 완료되었습니다."),
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        var intent = Intent(applicationContext, Board::class.java)
//                        intent.putExtra("id", id)
//                        intent.putExtra("board", board)
//                        intent.putExtra("userType", userType)
//                        intent.putExtra("userDept", userDept)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
//                        startActivity(intent)
//
//                    } else {
//                        Toast.makeText(
//                            applicationContext,
//                            "게시물 업로드가 실패했습니다.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                },
//                { Log.d("failed", "error......${error(applicationContext)}") },
//                mutableMapOf(
//                    "update" to flagUpdate,
//                    "id" to id,
//                    "board" to board_select,
//                    "userType" to userType,
//                    "userDept" to userDept,
//                    "title" to postTitle,
//                    "content" to postContent,
//                    "image1" to img1,
//                    "image2" to img2
//                )
//            )
//            val queue = Volley.newRequestQueue(this)
//            queue.add(request)
        }

        findViewById<TextView>(R.id.loading_textView).visibility = View.VISIBLE
        var progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE

    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val delegate: Converter<ResponseBody, *> =
                retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
            return Converter { body -> if (body.contentLength() == 0L) null else delegate.convert(body) }
        }
    }

    private fun createOkHttpClient(): OkHttpClient? {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //사진 첨부시 갤러리로 이동시켜주는,,
        super.onActivityResult(requestCode, resultCode, data)
        var imgbtn = findViewById<ImageButton>(R.id.imageButton_gallery)

        if( resultCode == Activity.RESULT_OK) { //호출 코드 확인
            if( requestCode ==  GALLERY)
            {
                ImageData = data?.data!!

                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImageData)
                    imgbtn.setImageBitmap(bitmap)

                    var source: ImageDecoder.Source? =
                        ImageData?.let { ImageDecoder.createSource(contentResolver, it) }
                    bitmap = source?.let { ImageDecoder.decodeBitmap(it) }


                    bitmap = resize(bitmap)

//                    image = bitmapToByteArray(bitmap)
                    image = BitMapToString(bitmap)
                    findViewById<TextView>(R.id.imageSrc).text = image


                } catch (e: FileNotFoundException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: OutOfMemoryError) {
                    Toast.makeText(applicationContext, "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun resize(bitmap: Bitmap): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val config: Configuration = Resources.getSystem().configuration
        var bitmap_width : Int? = bitmap?.width
        var bitmap_height : Int? = bitmap?.height

        bitmap = Bitmap.createScaledBitmap(bitmap!!, bitmap_width!!, bitmap_height!!, true)
        Log.d("please", "$bitmap_height, $bitmap_width")
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

    // String -> Bitmap 변환
    fun StringToBitmaps(image: String?): Bitmap? {
        try {
            val encodeByte = Base64.decode(image, Base64.DEFAULT)
            val bitmap : Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            return bitmap
        } catch (e: Exception) {
            e.message
            return null
        }
    }
}
