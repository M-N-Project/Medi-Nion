package com.example.medi_nion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.business_board_items.*
import kotlinx.android.synthetic.main.business_home.BusinessBoardRecyclerView
import kotlinx.android.synthetic.main.business_manage_create.*
import kotlinx.android.synthetic.main.business_writing.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class BusinessManageActivity : AppCompatActivity() {
    //해야할일: 이미지 가져와서 띄울때 프사 및 배경사진에 맞게 크기조절, uri->bitmap으로 바꿔서 DB에 넣기
     private val GALLERY = 1
    var image_background : String = "null"
    var image_profile : String = "null"
    var editWhichOne = 0 //0은 background, 1은 profile

    private var haveChan = false
    var items =ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    var scroll_count = 1
    var adapter = BusinessManageRecyclerAdapter(items)
    var scrollFlag = false
    var itemIndex = ArrayList<Int>()
    // RecyclerView.adapter에 지정할 Adapter
    private lateinit var listAdapter: BusinessManageRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)
        val id:String? = this.intent.getStringExtra("id")

        items.clear()
        all_items.clear()

        fetchProfile()
        fetchBusinessPost()

        val write = findViewById<Button>(R.id.write_btn)
        val profileImg = findViewById<ImageView>(R.id.profileImg)
        val backgroundImg = findViewById<ImageView>(R.id.backgroundImg)
        val saveBtn = findViewById<Button>(R.id.save_btn)

        val editBackgroundBtn = findViewById<Button>(R.id.backgroundEditBtn)
        val editProfileBtn = findViewById<Button>(R.id.profileImgEditBtn)
        val editNameBtn = findViewById<Button>(R.id.nameEditBtn)
        val editIntroBtn = findViewById<Button>(R.id.introEditBtn)

        val editName = findViewById<EditText>(R.id.profileName)
        val editIntro = findViewById<EditText>(R.id.profileDesc)

        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //채널명이 없으면 글쓰기 못하게끔

        write.setOnClickListener {
            val editName = findViewById<EditText>(R.id.profileName)
            val editIntro = findViewById<EditText>(R.id.profileDesc)
            if(editName.text.toString() == "" || editIntro.text.toString() == ""){
                Toast.makeText(this, "비즈니스 채널 설정 완료 후에 게시글 업로드가 가능합니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                var intent = Intent(this, BusinessWriting::class.java) //비즈니스 글쓰기 액티비티
                intent.putExtra("id", id)
                intent.putExtra("chanName", editName.text.toString())
                startActivity(intent)
            }

        }


        saveBtn.setOnClickListener {
            requestBusinessProfile()
            Toast.makeText(this, "비즈니스 채널 프로필 업데이트 완료", Toast.LENGTH_SHORT).show()
        }

        //배경사진 수정 버튼
        editBackgroundBtn.setOnClickListener{
            openGallery()
            editWhichOne = 0
        }

        editProfileBtn.setOnClickListener{
            openGallery()
            editWhichOne = 1
        }

        editName.setOnClickListener{
            editName.setHint("")
        }
        editNameBtn.setOnClickListener{
            inputMethodManager.showSoftInput(editName, 0)
            editName.setHint("")
        }

        editIntro.setOnClickListener{
            editIntro.setHint("")
        }
        editIntroBtn.setOnClickListener{
            inputMethodManager.showSoftInput(editIntro, 0)
            editIntro.setHint("")
        }

        if(image_profile!=null){
            val bitmap: Bitmap? = StringToBitmaps(image_profile)
            profileImg.setImageBitmap(bitmap)
        }
        if(image_background!=null) {
            val bitmap: Bitmap? = StringToBitmaps(image_background)
            backgroundImg.setImageBitmap(bitmap)
        }
    }

    fun fetchProfile(){
        var id = intent.getStringExtra("id")!!
        val url = "http://seonho.dothome.co.kr/BusinessProfile.php"
        val noPostView = findViewById<TextView>(R.id.noBusinessPostTextView)

        val editName = findViewById<EditText>(R.id.profileName)
        val editIntro = findViewById<EditText>(R.id.profileDesc)

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("0i234",response)
                if(response != "business profile fail"){
                    val jsonArray = JSONArray(response)

                    for (i in jsonArray.length()-1  downTo  0) {
                        val item = jsonArray.getJSONObject(i)

                        val channel_name = item.getString("Channel_Name")
                        val channel_desc = item.getString("Channel_Message")

                        if(channel_name == null)
                            editName.setHint("채널명")
                        else editName.setText(channel_name)

                        if(channel_desc == null)
                            editIntro.setHint("채널에 대한 간단한 소개를 작성해주세요.")
                        else editIntro.setText(channel_desc)

                    }
                }

            }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
    fun fetchBusinessPost() {
        // url to post our data
        var id = intent.getStringExtra("id")!!
        val urlBoard = "http://seonho.dothome.co.kr/BusinessManage.php"
        val noPostView = findViewById<TextView>(R.id.noBusinessPostTextView)

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)

                if(jsonArray.length() == 0) noPostView.visibility = View.VISIBLE
                items.clear()
                all_items.clear()
                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val id = item.getString("id")
                    val channel_name = item.getString("channel_name")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")
                    val image1 = item.getString("image1")
                    val image2 = item.getString("image2")
                    val image3 = item.getString("image3")
                    val BusinessItem = BusinessBoardItem(id, channel_name, title, content, time, image1, image2, image3)

                    items.add(BusinessItem)
                    all_items.add(BusinessItem)
                }
                var recyclerViewState = BusinessBoardRecyclerView.layoutManager?.onSaveInstanceState()
                var new_items = ArrayList<BusinessBoardItem>()
                new_items.addAll(items)
                adapter = BusinessManageRecyclerAdapter(new_items)
                BusinessBoardRecyclerView.adapter = adapter
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                BusinessBoardRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState);

            }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    fun requestBusinessProfile() {
        // url to post our data
        var id = intent.getStringExtra("id")!!
        var isFirst = intent.getBooleanExtra("isFirst", true)

        val editName = findViewById<EditText>(R.id.profileName)
        val editIntro = findViewById<EditText>(R.id.profileDesc)
//        val editProfile = findViewById<ImageView>(R.id.profileImg)
//        val editBackImg = findViewById<ImageView>(R.id.backgroundImg)

        val progressBar = findViewById<ProgressBar>(R.id.progressbarBusiness)
        val loadingText = findViewById<TextView>(R.id.loading_textView_business)

        val urlBusinessProfileInsert = "http://seonho.dothome.co.kr/BusinessProfileInsert.php"
        val urlBusinessProfileUpdate = "http://seonho.dothome.co.kr/BusinessProfileUpdate.php"

        //처음 생성하는 것 -> BusinessProfile에 삽입
        if(isFirst){
            val request = Login_Request(
                Request.Method.POST,
                urlBusinessProfileInsert,
                { response ->
                    Log.d("09123", response)


                }, { Log.d("business profile failed", "error......${this.let { it1 -> error(it1) }}") },
                hashMapOf(
                    "id" to id,
                    "channel_name" to editName.text.toString(),
                    "channel_desc" to editIntro.text.toString(),
                    "image_background" to image_background,
                    "image_profile" to image_profile
                )
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
        //원래 있던 프로필 업데이트 -> BusinessProfile update
        else{
            val request = Board_Request(
                Request.Method.POST,
                urlBusinessProfileUpdate,
                { response ->
                    Log.d("09123", response)

                    loadingText.visibility = View.GONE
                    progressBar.visibility = View.GONE

                }, { Log.d("business profile failed", "error......${this.let { it1 -> error(it1) }}") },
                hashMapOf(
                    "id" to id,
                    "channel_name" to editName.text.toString(),
                    "channel_desc" to editIntro.text.toString(),
                    "image_background" to image_background,
                    "image_profile" to image_profile
                )
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)

            loadingText.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun openGallery() {
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //uri -> bitmap
        super.onActivityResult(requestCode, resultCode, data)

        var backgroundImg = findViewById<ImageView>(R.id.backgroundImg)
        var profileImg = findViewById<ImageView>(R.id.profileImg)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY) {
                val currentImgUri : Uri? = data?.data

                if(editWhichOne==0) {
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                        backgroundImg.setImageBitmap(bitmap)

                        var source: ImageDecoder.Source? =
                            currentImgUri?.let { ImageDecoder.createSource(contentResolver, it) }
                        bitmap = source?.let { ImageDecoder.decodeBitmap(it) }


                        bitmap = resize(bitmap)
                        backgroundImg.setImageBitmap(bitmap)
                        image_background = BitMapToString(bitmap)

                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }

                else if(editWhichOne==1) {
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                        profileImg.setImageBitmap(bitmap)
                        roundAll(profileImg, 70.0f)

                        var source: ImageDecoder.Source? =
                            currentImgUri?.let { ImageDecoder.createSource(contentResolver, it) }
                        bitmap = source?.let { ImageDecoder.decodeBitmap(it) }

                        bitmap = resize(bitmap)

                        image_profile = BitMapToString(bitmap)
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }

            } else {
                Log.d("activity result", "wrong")
            }
        }
    }

    fun roundAll(iv: ImageView, curveRadius : Float)  : ImageView {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            iv.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view!!.width, view.height, curveRadius)
                }
            }

            iv.clipToOutline = true
        }
        return iv
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