package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BoardWrite : AppCompatActivity() {

    private val GALLERY = 1
    lateinit var ImageData : Uri
    var image : String = "null"
    var flagUpdate = "false"
    var lastSelected = ""

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
        var userMedal = intent.getIntExtra("userMedal", 0)

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
        var postbtn = findViewById<TextView>(R.id.post_Btn)
        var cancelbtn = findViewById<TextView>(R.id.cancel_Btn)
        var board_select = findViewById<TextView>(R.id.board_select)

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //intent.setType("image/*)
            startActivityForResult(intent, GALLERY)
        }

        board_select.text = board

        board_select.setOnClickListener {
            var boardTypeArray = resources.getStringArray(R.array.boardType)
            var boardTypeArrayList = ArrayList<String>()
            for(i in boardTypeArray){
                boardTypeArrayList.add(i)
            }
            showBottomSheet(boardTypeArrayList, "boardType")

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
                createBoardRequest()
            }

        }

        cancelbtn.setOnClickListener{

        }
    }

    private fun showBottomSheet(items : ArrayList<String> , type : String){
        val bottomSheetView = layoutInflater.inflate(R.layout.normal_dialog, null)
        bottomSheetView.setBackgroundColor(Color.parseColor("#00000000"))
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        var board_select = findViewById<TextView>(R.id.board_select)

        val cancelBtn = bottomSheetDialog.findViewById<TextView>(R.id.cancel)
        cancelBtn?.setOnClickListener{
            bottomSheetDialog.dismiss()
        }

        val selectBtn = bottomSheetDialog.findViewById<TextView>(R.id.select)

        selectBtn?.setOnClickListener{
            board_select.text = lastSelected
            bottomSheetDialog.dismiss()
        }

        val dialogRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.dialog_recyclerView)
        val dialogAdapter = DialogRecyclerAdapter(items, lastSelected)
        dialogRecyclerView?.adapter = dialogAdapter


        dialogAdapter.setOnItemClickListener(
            object : DialogRecyclerAdapter.OnItemClickListener{
                override fun onItemClick(v: View, data: String) {
                    lastSelected = data

                }

            }
        )

        bottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBoardRequest() {
        var updateUrl = "http://seonho.dothome.co.kr/updateBoard.php"
        var id = intent?.getStringExtra("id").toString()
        var board = intent.getStringExtra("board").toString()
        var userType = intent.getStringExtra("userType").toString()
        var userDept = intent.getStringExtra("userDept").toString()
        var userMedal = intent.getIntExtra("userMedal", 0)
        var postTitle = findViewById<EditText>(R.id.editText_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.editText_Content).text.toString()
        var board_select = findViewById<TextView>(R.id.board_select).text.toString()
//        var image = findViewById<ImageButton>(R.id.imageButton_gallery).toString()

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
            val request = Upload_Request(
                Request.Method.POST,
                updateUrl,
                { response ->
                    Log.d("89712312", response)
                    if (!response.equals("update fail")) {
                        Toast.makeText(
                            baseContext,
                            String.format("게시물 수정이 완료되었습니다."),
                            Toast.LENGTH_SHORT
                        ).show()

                        var intent = Intent(applicationContext, Board::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("board", board)
                        Log.d("게시물 수정 완료", userDept)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("userMedal", userMedal)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "게시물 수정이 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { Log.d("failed", "error......${error(applicationContext)}") },
                mutableMapOf(
                    "id" to id,
                    "board" to board_select,
                    "post_num" to post_num,
                    "title" to postTitle,
                    "content" to postContent,
                    "image1" to img1,
                    "image2" to img2
                )
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        } //if문끝
        else {
            val postUrl = "http://seonho.dothome.co.kr/createBoard.php"
            Log.d("123", "123")

            val request = Upload_Request(
                Request.Method.POST,
                postUrl,
                { response ->
                    if (!response.equals("upload fail")) {
                        Toast.makeText(
                            baseContext,
                            String.format("게시물 업로드가 완료되었습니다."),
                            Toast.LENGTH_SHORT
                        ).show()

                        UpdateGrade()

                        var intent = Intent(applicationContext, Board::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("board", board)
                        intent.putExtra("userType", userType)
                        intent.putExtra("userDept", userDept)
                        intent.putExtra("userMedal", userMedal)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "게시물 업로드가 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { Log.d("failed", "error......${error(applicationContext)}") },
                mutableMapOf(
                    "update" to flagUpdate,
                    "id" to id,
                    "board" to board_select,
                    "userType" to userType,
                    "userDept" to userDept,
                    "title" to postTitle,
                    "content" to postContent,
                    "image1" to img1,
                    "image2" to img2
                )
            )
            request.retryPolicy = DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
        findViewById<TextView>(R.id.loading_textView).visibility = View.VISIBLE
        var progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE
    }

    fun UpdateGrade() {
        val medalurl = "http://seonho.dothome.co.kr/Grade.php"
        var nickname = intent.getStringExtra("nickname").toString()

        val request = Login_Request(
            Request.Method.POST,
            medalurl,
            { response ->
                if(response != "grade fail") {
                    Log.d("userGraade", "up")
                }

            }, { Log.d("grade Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "nickname" to nickname
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
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

                    imgbtn.setImageBitmap(bitmap)

//                    image = bitmapToByteArray(bitmap)
                    image = BitMapToString(bitmap)


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

        bitmap = Bitmap.createScaledBitmap(bitmap!!, 240, 480, true)
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