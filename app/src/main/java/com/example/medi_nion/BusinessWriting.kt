package com.example.medi_nion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BusinessWriting : AppCompatActivity() { //비즈니스 글작성
    private val GALLERY = 1
    lateinit var ImageData : Uri
    var image : String = "null"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_writing)

        var id = intent.getStringExtra("id").toString()

        val url_Business = "http://seonho.dothome.co.kr/BusinessWriting.php"

        var imgbtn = findViewById<ImageButton>(R.id.imageButton_business)
        var postbtn = findViewById<Button>(R.id.postBtn_business)

        var postTitle = findViewById<EditText>(R.id.business_Title)
        var postContent = findViewById<EditText>(R.id.business_Content)

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //intent.setType("image/*)
            startActivityForResult(intent, GALLERY)
        }

        postbtn.setOnClickListener {
            if (postTitle.length() == 0 || postContent.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("경고")
                    .setMessage("제목 또는 내용을 입력해주세요.")
                    .setNegativeButton(
                        "닫기"
                    ) { dialog, which ->
                    }
                builder.show()
            } else {
                createBoardRequest(url_Business)
            }

        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBoardRequest(postUrl: String) {
        var id = intent?.getStringExtra("id").toString()

        var postTitle = findViewById<EditText>(R.id.business_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.business_Content).text.toString()

        var img1 : String = ""
        var img2 : String = ""
        if(image != "null"){
            img1 = image.substring(0,image.length/2+1)
            img2 = image.substring(image.length/2+1,image.length)
        }


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
                        String.format("채널 포스트 업로드가 완료되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    var intent = Intent(applicationContext, BusinessManageActivity::class.java)
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
            mutableMapOf(
                "id" to id,
                "title" to postTitle,
                "content" to postContent,
                "time" to postTime,
                "image1" to img1,
                "image2" to img2
            )
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)

        findViewById<TextView>(R.id.loading_textView_business).visibility = View.VISIBLE
        var progressBar = findViewById<ProgressBar>(R.id.progressbar_business)
        progressBar.visibility = View.VISIBLE

    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //사진 첨부시 갤러리로 이동시켜주는,,
        super.onActivityResult(requestCode, resultCode, data)
        var imgbtn = findViewById<ImageButton>(R.id.imageButton_business)

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
        var temp = ""
        try {
            //temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return base64Image
    }
}