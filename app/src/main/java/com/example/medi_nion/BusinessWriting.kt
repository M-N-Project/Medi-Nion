package com.example.medi_nion

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_writing.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class BusinessWriting : AppCompatActivity() { //비즈니스 글작성
    private val GALLERY_MULTI = 100

    var image1 : String = "null"
    var image2 : String = "null"
    var image3 : String = "null"
    var image4 : String = "null"
    var image5 : String = "null"

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_writing)

        val image_delete = findViewById<ImageView>(R.id.postImg_delete)
        image_delete.visibility = View.GONE
        val image1_ImageView = findViewById<ImageView>(R.id.business_postImg1)
        val image2_ImageView = findViewById<ImageView>(R.id.business_postImg2)
        val image3_ImageView = findViewById<ImageView>(R.id.business_postImg3)
        val image4_ImageView = findViewById<ImageView>(R.id.business_postImg4)
        val image5_ImageView = findViewById<ImageView>(R.id.business_postImg5)

        val url_Business = "http://seonho.dothome.co.kr/BusinessWriting.php"

        var imgbtn = findViewById<ImageButton>(R.id.imageButton_business)
        var postbtn = findViewById<Button>(R.id.postBtn_business)

        var postTitle = findViewById<EditText>(R.id.business_Title)
        var postContent = findViewById<EditText>(R.id.business_Content)

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            //사진을 여러개 선택할수 있도록 한다
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                GALLERY_MULTI
            )
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

        var imgListener : OnClickListener = OnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true )
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                GALLERY_MULTI
            )
        }

        image1_ImageView.setOnClickListener(imgListener)
        image2_ImageView.setOnClickListener(imgListener)
        image3_ImageView.setOnClickListener(imgListener)
        image4_ImageView.setOnClickListener(imgListener)
        image5_ImageView.setOnClickListener(imgListener)

        image_delete.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("이미지 삭제")
                .setMessage("선택한 이미지를 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        image1_ImageView.setImageResource(0)
                        image1_ImageView.visibility = View.GONE
                        image1 = "null"

                        image2_ImageView.setImageResource(0)
                        image2_ImageView.visibility = View.GONE
                        image2 = "null"

                        image3_ImageView.setImageResource(0)
                        image3_ImageView.visibility = View.GONE
                        image3 = "null"

                        image4_ImageView.setImageResource(0)
                        image4_ImageView.visibility = View.GONE
                        image4 = "null"

                        image5_ImageView.setImageResource(0)
                        image5_ImageView.visibility = View.GONE
                        image5 = "null"

                        image_delete.visibility = View.GONE

                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createBoardRequest(postUrl: String) {
        var id = intent?.getStringExtra("id").toString()
        var chanName : String = ""

        var postTitle = findViewById<EditText>(R.id.business_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.business_Content).text.toString()

        val chanNamerequest = Login_Request(
            Request.Method.POST,
            "http://seonho.dothome.co.kr/BusinessChanName.php",
            { response ->
                Log.d("09123", response)
                if(response!= "no Channel Name"){
                    val jsonArray = JSONArray(response)

                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)
                        val success = item.getBoolean("success");
                        if (success) {
                            for (i in item.length() - 1 downTo 0) {
                                chanName = item.getString("chanName")
                                Log.d("business chanName", chanName)
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "chanName 검색 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val request: StringRequest =
                            object : StringRequest(
                                Method.POST,
                                postUrl,
                                object : Response.Listener<String?> {
                                    override fun onResponse(response: String?) {
                                        Log.d("bussinewriting", response.toString())

                                        var intent = Intent(
                                            applicationContext,
                                            BusinessManageActivity::class.java
                                        )
                                        intent.putExtra("id", id)
                                        intent.putExtra("isFirst", false)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //뒤로가기 눌렀을때 글쓰기 화면으로 다시 오지 않게 하기위해.
                                        startActivity(intent)
                                    }
                                },
                                object : Response.ErrorListener {
                                    override fun onErrorResponse(error: VolleyError) {
                                        Log.d("bussinewriting", error.toString())
                                    }
                                }) {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String>? {
                                    val map: MutableMap<String, String> = HashMap()
                                    // 1번 인자는 PHP 파일의 $_POST['']; 부분과 똑같이 해줘야 한다
                                    map["id"] = id
                                    map["Channel_Name"] = chanName
                                    map["title"] = postTitle
                                    map["content"] = postContent
                                    map["Channel_Profile_Img1"] = image1
                                    map["Channel_Profile_Img2"] = image2
                                    map["Channel_Profile_Img3"] = image3
                                    map["Channel_Profile_Img4"] = image4
                                    map["Channel_Profile_Img5"] = image5

                                    return map
                                }
                            }

                        request.setRetryPolicy(
                            DefaultRetryPolicy(
                                40000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                            )
                        )
                        val queue = Volley.newRequestQueue(applicationContext)
                        queue.add(request)
                    }
                }
            },
            { Log.d("failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to id
            )
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(chanNamerequest)

        findViewById<TextView>(R.id.loading_textView_business).visibility = View.VISIBLE
        var progressBar = findViewById<ProgressBar>(R.id.progressbar_business)
        progressBar.visibility = View.VISIBLE

    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val image_delete = findViewById<ImageView>(R.id.postImg_delete)
        val image1_ImageView = findViewById<ImageView>(R.id.business_postImg1)
        val image2_ImageView = findViewById<ImageView>(R.id.business_postImg2)
        val image3_ImageView = findViewById<ImageView>(R.id.business_postImg3)
        val image4_ImageView = findViewById<ImageView>(R.id.business_postImg4)
        val image5_ImageView = findViewById<ImageView>(R.id.business_postImg5)
        //사진버튼 눌러서 여러개 선택.
        if (requestCode == GALLERY_MULTI) {
            val uri = data?.data
            val clipData = data?.clipData
            var imageCnt = 0

            //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
            if (clipData != null) {
                imageCnt += clipData.itemCount
                if(imageCnt > 5) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setTitle("사진 개수 초과")
                        .setMessage("사진은 최대 5개만 첨부 가능합니다.")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                                imageCnt = 5
                            })
                    // 다이얼로그를 띄워주기
                    builder.show()
                }
                Log.d("imgccc", "${clipData.itemCount} / $imageCnt")
                for (i in 0 until imageCnt) {
                    image_delete.visibility = View.VISIBLE
                    if (i < imageCnt) {
                        val urione = clipData.getItemAt(i).uri
                        when (i) {
                            0 -> {
                                image1_ImageView.visibility = View.VISIBLE
                                image1_ImageView.setImageURI(urione)
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, urione)
                                image1_ImageView.setImageBitmap(bitmap)

                                var source: ImageDecoder.Source? =
                                    urione?.let { ImageDecoder.createSource(contentResolver, it) }
                                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                bitmap = resize(bitmap)
//                                image1 = BitMapToString(bitmap)
                                image1 = encodeBitmapImage(bitmap)

                                Log.d("img..1", "image1")

                            }
                            1 -> {
                                image2_ImageView.visibility = View.VISIBLE
                                image2_ImageView.setImageURI(urione)
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, urione)
                                image2_ImageView.setImageBitmap(bitmap)

                                var source: ImageDecoder.Source? =
                                    urione?.let { ImageDecoder.createSource(contentResolver, it) }
                                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                bitmap = resize(bitmap)
//                                image2 = BitMapToString(bitmap)
                                image2 = encodeBitmapImage(bitmap)
                                Log.d("img..2", "image2")
                            }
                            2 -> {
                                image3_ImageView.visibility = View.VISIBLE
                                image3_ImageView.setImageURI(urione)
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, urione)
                                image3_ImageView.setImageBitmap(bitmap)

                                var source: ImageDecoder.Source? =
                                    urione?.let { ImageDecoder.createSource(contentResolver, it) }
                                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                bitmap = resize(bitmap)
//                                image3 = BitMapToString(bitmap)
                                Log.d("img..3", "image3")
                                image3 = encodeBitmapImage(bitmap)
                            }
                            3 -> {
                                image4_ImageView.visibility = View.VISIBLE
                                image4_ImageView.setImageURI(urione)
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, urione)
                                image4_ImageView.setImageBitmap(bitmap)

                                var source: ImageDecoder.Source? =
                                    urione?.let { ImageDecoder.createSource(contentResolver, it) }
                                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                bitmap = resize(bitmap)
//                                image4 = BitMapToString(bitmap)
                                image4 = encodeBitmapImage(bitmap)
                            }
                            4 -> {
                                image5_ImageView.visibility = View.VISIBLE
                                image5_ImageView.setImageURI(urione)
                                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, urione)
                                image5_ImageView.setImageBitmap(bitmap)

                                var source: ImageDecoder.Source? =
                                    urione?.let { ImageDecoder.createSource(contentResolver, it) }
                                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                bitmap = resize(bitmap)
//                                image5 = BitMapToString(bitmap)
                                image5 = encodeBitmapImage(bitmap)
                            }
                        }
                    }
                }
            } else if (uri != null) {
                image1_ImageView.setImageURI(uri)
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                image1_ImageView.setImageBitmap(bitmap)

                var source: ImageDecoder.Source? =
                    uri?.let { ImageDecoder.createSource(contentResolver, it) }
                bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                bitmap = resize(bitmap)
                image1 = BitMapToString(bitmap)

                imageCnt ++
            }

        }

        }
    }

    private fun encodeBitmapImage(bitmap: Bitmap) : String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesOfImage = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
    }

//    @RequiresApi(Build.VERSION_CODES.P)
//    fun uriToBitmap(ImageData : Uri) : String {
//        try {
//            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImageData)
//
//            var source: ImageDecoder.Source? =
//                ImageData?.let { ImageDecoder.createSource(contentResolver, it) }
//            bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
//
//
//            bitmap = resize(bitmap)
//
//
////                    image = bitmapToByteArray(bitmap)
//            image = BitMapToString(bitmap)
//        } catch (e: FileNotFoundException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        } catch (e: IOException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        } catch (e: OutOfMemoryError) {
//            Toast.makeText(applicationContext, "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT)
//                .show()
//        }
//        return image
//    }

    private fun resize(bitmap: Bitmap): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val config: Configuration = Resources.getSystem().configuration
        bitmap = if (config.smallestScreenWidthDp >= 800) Bitmap.createScaledBitmap(
            bitmap!!,
            400,
            440,
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
