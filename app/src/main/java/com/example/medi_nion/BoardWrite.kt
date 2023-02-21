package com.example.medi_nion

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.signup_detail.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLEncoder


class BoardWrite : AppCompatActivity() {

    private val GALLERY = 1

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)


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
                var intent = Intent(applicationContext, Board::class.java)
                startActivity(intent)
            }

        }
    }

    private fun createBoardRequest(postUrl: String) {
        var id = intent?.getStringExtra("id").toString()
        var postTitle = findViewById<EditText>(R.id.editText_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.editText_Content).text.toString()
        var board_select = findViewById<TextView>(R.id.board_select).text.toString()
        var image = findViewById<ImageButton>(R.id.imageButton_gallery).toString()

        Log.d("Write id", "$id")

        val request = SignUP_Request(
            Request.Method.POST,
            postUrl,
            { response ->
                if (!response.equals("upload fail")) {
                    postTitle = response.toString()
                    postContent = response.toString()
                    board_select = response.toString()
                    image = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("게시물 업로드가 완료되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "Post success1",
                        "$id, $board_select, $postTitle, $postContent, $image"
                    )

                    Log.d("response", "${response}")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "게시물 업로드가 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { Log.d("failed", "error......${error(applicationContext)}") },
                hashMapOf(
                    "id" to id,
                    "board" to board_select,
                    "title" to postTitle,
                    "content" to postContent,
                    "image" to image
                )
        )
        Log.d("hash???", "12345678")
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
                var ImageData: Uri? = data?.data
                imgbtn.setImageURI(ImageData)

                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImageData)
                    imgbtn.setImageBitmap(bitmap)
                    Log.d("1", "1111111")

                    var source: ImageDecoder.Source? =
                        ImageData?.let { ImageDecoder.createSource(contentResolver, it) }
                    Log.d("2", "2222222")
                    bitmap = source?.let { ImageDecoder.decodeBitmap(it) }

                    Log.d("3", "3333333")

                    bitmap = resize(bitmap)

                    Log.d("4", "4444444")

                    var image = BitMapToString(bitmap)

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

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bitmap compress
        val arr = baos.toByteArray()
        val image: String = Base64.encodeToString(arr, Base64.DEFAULT)
        var temp = ""
        try {
            temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return temp
    }
}
