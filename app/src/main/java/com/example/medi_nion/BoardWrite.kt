package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
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
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64.getEncoder
import kotlin.concurrent.thread


class BoardWrite : AppCompatActivity() {

    private val GALLERY = 1
    lateinit var ImageData : Uri
    lateinit var image : String


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)

        var id = intent.getStringExtra("id")

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

        board_select.setOnClickListener {
            select_RadioGroup.visibility = View.VISIBLE
            select_RadioGroup.bringToFront()

            free_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "전체 게시판"
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
        var id = intent?.getStringExtra("id").toString()
        var postTitle = findViewById<EditText>(R.id.editText_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.editText_Content).text.toString()
        var board_select = findViewById<TextView>(R.id.board_select).text.toString()
//        var image = findViewById<ImageButton>(R.id.imageButton_gallery).toString()
        var imageSrc = findViewById<TextView>(R.id.imageSrc).text.toString()

        var img1 = image.substring(0,10313)
        var img2 = image.substring(10313,20624)

        val current: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val postTime = current.format(formatter)

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

                    var intent = Intent(applicationContext, Board::class.java)
                    intent.putExtra("id", id)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
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
                mutableMapOf<String, String>(
                    "id" to id,
                    "board" to board_select,
                    "title" to postTitle,
                    "content" to postContent,
                    "time" to postTime,
                    "image1" to img1,
                    "image2" to img2
                )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

        findViewById<TextView>(R.id.loading_textView).visibility = View.VISIBLE
        var progressBar = findViewById<ProgressBar>(R.id.progressbar)
        progressBar.visibility = View.VISIBLE

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
                imgbtn.setImageURI(ImageData)
//                findViewById<TextView>(R.id.imageSrc).text = ImageData.toString()

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

                    Log.d("5", "555555")
                    Log.d("image!!", "$image")

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

    fun bitmapToByteArray(bitmap : Bitmap) : String {
        var image : String
        var stream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var byteArray : ByteArray = stream.toByteArray()
        image = "&image=" + byteArrayToBinaryString(byteArray)
        return image
    }

    fun byteArrayToBinaryString(b : ByteArray) : String{
        var sb : StringBuilder = StringBuilder()
        for(i in 0..b.size-1){
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString()
    }

    fun byteToBinaryString(n : Byte) : String{
        var sb : StringBuilder = StringBuilder("00000000")
        for(bit in 0..7){
            if(((n.toInt() shr bit) and 1) > 0){
                sb.setCharAt(7-bit, '1')
            }
        }
        return sb.toString()
    }

    @SuppressLint("Range")
    fun getPath(uri : Uri) : String{
        var cursor : Cursor? = getContentResolver().query(uri, null, null, null, null)
        var document_id : String = ""
        var path : String = ""
        if (cursor != null) {
            cursor.moveToFirst()
            document_id = cursor.getString(0)
            document_id = document_id.substring(document_id.lastIndexOf(":")+1)
        }
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID+"=?",
            arrayOf(document_id), null)
        if (cursor != null) {
            cursor.moveToFirst()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return path
    }

    //Convert the image URI to the direct file system path of the image file
    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(
            contentUri,
            proj,  // Which columns to return
            null,  // WHERE clause; which rows to return (all rows)
            null,  // WHERE clause selection arguments (none)
            null
        ) // Order-by clause (ascending by name)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun resize(bitmap: Bitmap): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val config: Configuration = Resources.getSystem().configuration
        bitmap = if (config.smallestScreenWidthDp >= 800) Bitmap.createScaledBitmap(
            bitmap!!,
            400,
            240,
            true
        ) else if (config.smallestScreenWidthDp >= 600) Bitmap.createScaledBitmap(
            bitmap!!, 300, 180, true
        ) else if (config.smallestScreenWidthDp >= 400) Bitmap.createScaledBitmap(
            bitmap!!, 200, 120, true
        ) else if (config.smallestScreenWidthDp >= 360) Bitmap.createScaledBitmap(
            bitmap!!, 180, 108, true
        ) else Bitmap.createScaledBitmap(bitmap!!, 160, 96, true)
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
//        Log.d("23l2i3o", Base64.decode(image,0).toString())
        var temp = ""
        try {
            //temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        Log.d("ooooo", temp)
        return base64Image
    }
}
