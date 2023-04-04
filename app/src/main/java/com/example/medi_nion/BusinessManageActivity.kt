package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
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
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.example.medi_nion.VolleyMultipartRequest2.DataPart
import kotlinx.android.synthetic.main.business_home.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class BusinessManageActivity : AppCompatActivity() {
    //해야할일: 이미지 가져와서 띄울때 프사 및 배경사진에 맞게 크기조절, uri->bitmap으로 바꿔서 DB에 넣기
     private val GALLERY = 1
    var image_profile : String = "null"

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

    var profileImgPath : String = ""
    private var resultLauncher //콜백함수
            : ActivityResultLauncher<Intent>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)
        val id:String? = this.intent.getStringExtra("id")

        items.clear()
        all_items.clear()

        fetchProfile()
        fetchBusinessPost()

        findViewById<RadioGroup>(R.id.businessSetting_RadioGroup).bringToFront()

        val write = findViewById<Button>(R.id.write_btn)
        val profileImg = findViewById<ImageView>(R.id.profileImg)
        val saveBtn = findViewById<Button>(R.id.save_btn)
        val subscribe_count = findViewById<EditText>(R.id.subscribe_count)

        val editProfile = findViewById<ImageView>(R.id.profileImg)
        val editName = findViewById<EditText>(R.id.profileName)
        val editDesc = findViewById<EditText>(R.id.profileDesc)

        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        subscribe_count.setOnClickListener {
            Toast.makeText(applicationContext, "구독자 수는 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

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
                intent.putExtra("chanIntro", editIntro.text.toString())
                startActivity(intent)
            }

        }


        saveBtn.setOnClickListener {
            requestBusinessProfile()
        }

        editName.isEnabled = false
        editDesc.isEnabled = false

        val settingBtn = findViewById<Button>(R.id.businessSettingBtn)
        settingBtn.setOnClickListener{
            val setting_RadioGroup = findViewById<RadioGroup>(R.id.businessSetting_RadioGroup)

            val editProfile_RadioBtn = findViewById<RadioButton>(R.id.edit_profile)
            val editName_RadioBtn = findViewById<RadioButton>(R.id.edit_chanName)
            val editDesc_RadioBtn = findViewById<RadioButton>(R.id.edit_chanDesc)

            setting_RadioGroup.bringToFront()

            if(setting_RadioGroup.visibility == View.VISIBLE) setting_RadioGroup.visibility = View.GONE
            else setting_RadioGroup.visibility = View.VISIBLE

            editProfile_RadioBtn.setOnClickListener{
//                openGallery()
                showFileChooser()
            }

            editName_RadioBtn.setOnClickListener{
                Log.d("92123", "clickName")
                setting_RadioGroup.visibility = View.GONE
                editName.isEnabled = true
                editName.setFocusableInTouchMode(true);
                editName.setFocusable(true);
                editName.requestFocus();
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT)
                editName.setSelection(editName.length()); //커서를 끝에 위치!

                editName_RadioBtn.isSelected = false
                editName_RadioBtn.isChecked = false
            }

            editDesc_RadioBtn.setOnClickListener{
                Log.d("92123", "clickDesc")
                setting_RadioGroup.visibility = View.GONE
                editDesc.isEnabled = true
                editDesc.setFocusableInTouchMode(true);
                editDesc.setFocusable(true);
                editDesc.requestFocus();
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editDesc, InputMethodManager.SHOW_IMPLICIT)
                editDesc.setSelection(editDesc.length()); //커서를 끝에 위치!

                editDesc_RadioBtn.isSelected = false
                editDesc_RadioBtn.isChecked = false
            }
        }

        if(image_profile!=null){
            val bitmap: Bitmap? = StringToBitmaps(image_profile)
            profileImg.setImageBitmap(bitmap)
        }

    }

    @SuppressLint("SetTextI18n")
    fun fetchProfile(){
        var id = intent.getStringExtra("id")!!
        val url = "http://seonho.dothome.co.kr/BusinessProfile.php"
        val noPostView = findViewById<TextView>(R.id.noBusinessPostTextView)

        val editName = findViewById<EditText>(R.id.profileName)
        val editDesc = findViewById<EditText>(R.id.profileDesc)
        val editProfile = findViewById<ImageView>(R.id.profileImg)
        val subscribe_text = findViewById<EditText>(R.id.subscribe_count)

        Toast.makeText(this, "로딩중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                Log.d("0i234",response)
                if(!response.equals("business profile fail")){
                    val jsonArray = JSONArray(response)

                    for (i in jsonArray.length()-1  downTo  0) {
                        val item = jsonArray.getJSONObject(i)

                        val channel_name = item.getString("Channel_Name")
                        val channel_desc = item.getString("Channel_Message")
                        val image_profile = item.getString("Channel_Profile_Img")
                        val subscribe_count = item.getInt("subscribe_count")

                        Log.d("4444", "$channel_name, $channel_desc, $image_profile")

                        if(channel_name == null)
                            editName.setHint("채널명")
                        else editName.setText(channel_name)

                        if(channel_desc == null)
                            editDesc.setHint("채널에 대한 간단한 소개를 작성해주세요.")
                        else editDesc.setText(channel_desc)

                        if(image_profile!=null){
                            val bitmap: Bitmap? = StringToBitmaps(image_profile)
                            editProfile.setImageBitmap(bitmap)
                        }

                        subscribe_text.setText("구독자 수: " + subscribe_count.toString() + "명")


                    }
                }

            }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "id" to id
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
    fun fetchBusinessPost() {
        // url to post our data
        var appUser = intent.getStringExtra("id")!!
        val urlBoard = "http://seonho.dothome.co.kr/BusinessManage.php"
        val urlIsSub = "http://seonho.dothome.co.kr/ChannelSubList.php"
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
                    var isSub = false

                    val requestSub = Board_Request(
                        Request.Method.POST,
                        urlIsSub,
                        { responseIsSub ->
                            if(responseIsSub != "business subscribers list fail"){
                                val jsonArray = JSONArray(responseIsSub)

                                for (i in jsonArray.length()-1  downTo  0) {
                                    if(jsonArray[i].toString() == appUser) {
                                        isSub = true
                                        break
                                    }
                                }
                            }

                        }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
                        hashMapOf(
                            "channel_name" to channel_name
                        )
                    )
                    val queue = Volley.newRequestQueue(this)
                    queue.add(requestSub)

                    val BusinessItem = BusinessBoardItem(num, id, channel_name, title, content, time, image1, image2, image3,false, false)

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
                "id" to appUser
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    fun requestBusinessProfile() {
        // url to post our data
        var id = intent.getStringExtra("id")!!
        var isFirst = intent.getBooleanExtra("isFirst", true)

        val channel_name = findViewById<EditText>(R.id.profileName).text.toString()
        val channel_desc = findViewById<EditText>(R.id.profileDesc).text.toString()

        val progressBar = findViewById<ProgressBar>(R.id.progressbarBusiness)
        val loadingText = findViewById<TextView>(R.id.loading_textView_business)

        val urlBusinessProfileInsert = "http://seonho.dothome.co.kr/BusinessProfileInsert.php"
        val urlBusinessProfileUpdate = "http://seonho.dothome.co.kr/BusinessProfileUpdate.php"

        val intent: Intent = Intent(applicationContext, ProfileFragment::class.java)

        Log.d("FIRst??", isFirst.toString())

        var image_profile1 : String = ""
        var image_profile2 : String = ""
        if(image_profile != "null"){
            image_profile1 = image_profile.substring(0,image_profile.length/2+1)
            image_profile2 = image_profile.substring(image_profile.length/2+1,image_profile.length)
        }

        Log.d("skfasd", image_profile)


        //처음 생성하는 것 -> BusinessProfile에 삽입
        if(isFirst){
            loadingText.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            val request = Login_Request(
                Request.Method.POST,
                urlBusinessProfileInsert,
                { response ->
                    Log.d("bussinesssssss", response.toString())
                    if(!response.equals("business profile insert fail")) {
                        Toast.makeText(this, "비즈니스 채널 프로필 생성 완료", Toast.LENGTH_SHORT).show()

                        intent.putExtra("id", id)
                        intent.putExtra("channel_name", channel_name)

                        startActivity(intent)


                    } else {
                        Toast.makeText(this, "비즈니스 채널 프로필 생성 실패", Toast.LENGTH_SHORT).show()
                    }

                    loadingText.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    val setting_RadioGroup = findViewById<RadioGroup>(R.id.businessSetting_RadioGroup)
                    setting_RadioGroup.visibility = View.GONE

                }, { Log.d("business profile failed", "error......${error(this)}") },
                hashMapOf(
                    "id" to id,
                    "Channel_Name" to channel_name,
                    "Channel_Message" to channel_desc,
                    "Channel_Profile_Img" to image_profile
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
        //원래 있던 프로필 업데이트 -> BusinessProfile update
        else{
            loadingText.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            val request = Login_Request(
                Request.Method.POST,
                urlBusinessProfileUpdate,
                { response ->
                    Log.d("bussine123", response.toString())
                    if(!response.equals("business Chan update fail")) {
                        Toast.makeText(this, "비즈니스 채널 프로필 업데이트 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "비즈니스 채널 프로필 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                    loadingText.visibility = View.GONE
                    progressBar.visibility = View.GONE

                }, { Log.d("business profile failed", "error......${this.let { it1 -> error(it1) }}") },
                hashMapOf(
                    "id" to id,
                    "Channel_Name" to channel_name,
                    "Channel_Message" to channel_desc,
                    "Channel_Profile_Img" to image_profile
                )
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)

        }
    }

    private fun openGallery() {
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //uri -> bitmap
        super.onActivityResult(requestCode, resultCode, data)

        var profileImg = findViewById<ImageView>(R.id.profileImg)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY) {
                val currentImgUri : Uri? = data?.data
                profileImgPath = currentImgUri?.let { getRealPathFromUri(it) }.toString()

                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                    profileImg.setImageBitmap(bitmap)
                    roundAll(profileImg, 70.0f)

                    var source: ImageDecoder.Source? =
                        currentImgUri?.let { ImageDecoder.createSource(contentResolver, it) }
                    bitmap = source?.let { ImageDecoder.decodeBitmap(it) }

                    bitmap = resize(bitmap)
                    uploadBitmap(bitmap);
                    profileImg.setImageBitmap(bitmap)
                    image_profile = BitMapToString(bitmap)

                    val setting_RadioGroup = findViewById<RadioGroup>(R.id.businessSetting_RadioGroup)
                    setting_RadioGroup.visibility = View.GONE
                } catch (e:Exception) {
                    e.printStackTrace()
                }

            } else {
                Log.d("activity result", "wrong")
            }
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {



    }


    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
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

    //이미지 주소를 절대경로로 바꿔주는 메소드
    fun getRealPathFromUri(uri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, uri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
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