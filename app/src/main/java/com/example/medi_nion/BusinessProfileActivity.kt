package com.example.medi_nion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_profile_home.*
import kotlinx.coroutines.delay
import org.json.JSONArray
import java.io.ByteArrayOutputStream


class BusinessProfileActivity : AppCompatActivity() {
    //해야할일: 이미지 가져와서 띄울때 프사 및 배경사진에 맞게 크기조절, uri->bitmap으로 바꿔서 DB에 넣기
    private val GALLERY = 1
    var image_background: String = "null"
    var image_profile: String = "null"
    var profileMap = HashMap<String, Bitmap>()

    private var haveChan = false
    var items = ArrayList<BusinessBoardItem>()
    var all_items = ArrayList<BusinessBoardItem>()
    val item_count = 20 // 초기 20개의 아이템만 불러오게 하고, 스크롤 시 더 많은 아이템 불러오게 하기 위해
    var scroll_count = 1
    var adapter = BusinessProfileRecyclerAdapter(items)
    var scrollFlag = false
    var itemIndex = ArrayList<Int>()

    // RecyclerView.adapter에 지정할 Adapter
    private lateinit var listAdapter: BusinessManageRecyclerAdapter

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_profile_home)

        items.clear()
        all_items.clear()

        fetchProfile()

        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val url = "http://seonho.dothome.co.kr/businessSubcribeInsert.php"
        var appUser = intent.getStringExtra("appUser")!!
        var channel_name = intent.getStringExtra("channel_name")!!
        val channelPlusBtn = findViewById<CheckBox>(R.id.channelPlusBtn)
        channelPlusBtn.setOnClickListener {
            Toast.makeText(applicationContext, "구독목록에 추가되었습니다", Toast.LENGTH_SHORT).show()

            //구독 php
            val request = Board_Request(
                Request.Method.POST,
                url,
                { response ->
                    if (response != "business profile fail") {

                    }
                }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
                hashMapOf(
                    "id" to appUser,
                    "channel_name" to channel_name,
                    "flag" to (channelPlusBtn.isChecked).toString()
                )
            )
            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }
    }

    fun fetchProfile() {
        var channel_name = intent.getStringExtra("channel_name")!!
        var appUser = intent.getStringExtra("appUser")!!
        val url = "http://seonho.dothome.co.kr/BusinessProfileInfo2.php"
        val urlIsSub = "http://seonho.dothome.co.kr/ChannelSubList.php"
        val urlBoard = "http://seonho.dothome.co.kr/BusinessProfilePost.php"
        val noPostView = findViewById<TextView>(R.id.noBusinessPostTextView)

        val chanName = findViewById<TextView>(R.id.profileName)
        val chanDesc = findViewById<TextView>(R.id.profileDesc)
        val chanIsSub = findViewById<CheckBox>(R.id.channelPlusBtn)
        val chanSubNum = findViewById<TextView>(R.id.subscribe_count)

        val request = Board_Request(
            Request.Method.POST,
            url,
            { response ->
                if (response != "business profile fail") {
                    val jsonArray = JSONArray(response)
                    for (i in jsonArray.length() - 1 downTo 0) {
                        val item = jsonArray.getJSONObject(i)

                        val id = item.getString("id")
                        val channel_name = item.getString("Channel_Name")
                        val channel_desc = item.getString("Channel_Message")
                        val chanSub_num = item.getString("subscribe_count")
                        val channel_img = item.getString("Channel_Profile_Img")

                        chanName.text = channel_name
                        chanDesc.text = channel_desc

                        chanSubNum.text = chanSub_num + " 명 구독 중"

                        val chanProfileImg = findViewById<ImageView>(R.id.profileImg)
                        val imgUrl =
                            "http://seonho.dothome.co.kr/images/businessProfile/$channel_img"
                        val task = ImageLoadTask(imgUrl, chanProfileImg)
                        task.execute()
                        roundAll(chanProfileImg, 100.0f)


                        val requestSub = Board_Request(
                            Request.Method.POST,
                            urlIsSub,
                            { responseIsSub ->
                                if (responseIsSub != "business subscribers list fail") {
                                    val jsonArray = JSONArray(responseIsSub)

                                    for (i in jsonArray.length() - 1 downTo 0) {
                                        val item = jsonArray.getJSONObject(i)

                                        if (item.getString("id") == appUser) {
                                            chanIsSub.isChecked = true
                                            break
                                        }
                                    }

                                    fetchBusinessPost(channel_img)

                                }

                            },
                            {
                                Log.d(
                                    "login failed",
                                    "error......${this.let { it1 -> error(it1) }}"
                                )
                            },
                            hashMapOf(
                                "channel_name" to channel_name
                            )
                        )
                        val queue = Volley.newRequestQueue(this)
                        queue.add(requestSub)
                    }
                }

            }, { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "channel_name" to channel_name
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun fetchBusinessPost(channel_img:String) :Boolean {
        // url to post our data
        var channel_name = intent.getStringExtra("channel_name")!!
        val appUser = intent.getStringExtra("appUser").toString()
        val urlBoard = "http://seonho.dothome.co.kr/BusinessProfilePost.php"
        val urlProfile = "http://seonho.dothome.co.kr/BusinessProfile.php"
        val urlBookmark = "http://seonho.dothome.co.kr/BusinessBookmark_list.php"
        val urlLike = "http://seonho.dothome.co.kr/BusinessLike_list.php"
        val noPostView = findViewById<TextView>(R.id.noBusinessPostTextView)


        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                if (jsonArray.length() == 0) noPostView.visibility = View.VISIBLE
                items.clear()
                all_items.clear()
                for (i in jsonArray.length() - 1 downTo 0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val writerid = item.getString("id")
                    val channel_name = item.getString("channel_name")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")
                    val image1 = item.getString("image1")
                    val image2 = item.getString("image2")
                    val image3 = item.getString("image3")
                    val image4 = item.getString("image4")
                    val image5 = item.getString("image5")
                    val profileImg = findViewById<ImageView>(R.id.profileImg)

                    var isBookmark = false
                    var isHeart = false

                    val bookfetchrequest = Login_Request(
                        Request.Method.POST,
                        urlBookmark,
                        { responseBookmark ->
                            if (responseBookmark.equals("Success Bookmark")) {
                                isBookmark = true
                            } else if (responseBookmark.equals("No Bookmark")) {
                                isBookmark = false
                            }
                            val heartfetchrequest = Login_Request(
                                Request.Method.POST,
                                urlLike,
                                { responseLike ->
                                    if (responseLike.equals("Success Heart")) {
                                        isHeart = true
                                    } else if (responseLike.equals("No Heart")) {
                                        isHeart = false
                                    }

                                    val BusinessItem = BusinessBoardItem(
                                        num,
                                        writerid,
                                        channel_img,
                                        channel_name,
                                        title,
                                        content,
                                        time,
                                        image1,
                                        image2,
                                        image3,
                                        isHeart,
                                        isBookmark,
                                        false
                                    )

                                    Log.d("비즈니스1", title)

                                    if(items.add(BusinessItem)){
                                        if(items.size == jsonArray.length()) {
                                            setAdapter()
                                        }
                                    }
                                },
                                {
                                    Log.d(
                                        "login failed",
                                        "error......${this.let { it1 -> error(it1) }}"
                                    )
                                },
                                hashMapOf(
                                    "id" to appUser,
                                    "post_num" to num.toString()
                                )
                            )
                            val queue = Volley.newRequestQueue(this)
                            queue.add(heartfetchrequest)
                        },
                        {
                            Log.d(
                                "login failed",
                                "error......${this.let { it1 -> error(it1) }}"
                            )
                        },
                        hashMapOf(
                            "id" to appUser,
                            "post_num" to num.toString()
                        )
                    )
                    val queue = Volley.newRequestQueue(this)
                    queue.add(bookfetchrequest)
                }
            },
            { Log.d("login failed", "error......${this.let { it1 -> error(it1) }}") },
            hashMapOf(
                "channel_name" to channel_name
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

        return true
    }

    private fun setAdapter() {
        var channel_name = intent.getStringExtra("channel_name")!!
        val appUser = intent.getStringExtra("appUser").toString()

        // 여기에서 adapter 설정
        var new_items = ArrayList<BusinessBoardItem>()
        new_items.addAll(items)
        new_items.sortBy { it.time }
        new_items.reverse()
        Log.d("비즈니스 리사이클러3", new_items.toString())
        adapter = BusinessProfileRecyclerAdapter(new_items)
        BusinessBoardRecyclerView.adapter = adapter

        // 좋아요 앤 북마크 request
        adapter.setOnItemClickListener(object :
            BusinessProfileRecyclerAdapter.OnItemClickListener {
            override fun onProfileClick(
                v: View,
                data: BusinessBoardItem,
                pos: Int
            ) {

            }

            override fun onItemHeart(
                v: View,
                data: BusinessBoardItem,
                pos: Int
            ) {
                val heartrequest = Board_Request(
                    Request.Method.POST,
                    "http://seonho.dothome.co.kr/BusinessLike.php",
                    { response ->
                        if (response != "Heart fail") {
                            data.isHeart = !data.isHeart
                            Toast.makeText(
                                applicationContext,
                                "좋아요 완료",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else Log.d(
                            "heartrequest fail",
                            response
                        )
                    },
                    {
                        Log.d(
                            "b-heart failed",
                            "error......${
                                this?.let { it1 ->
                                    error(
                                        it1
                                    )
                                }
                            }"
                        )
                    },
                    hashMapOf(
                        "id" to appUser,
                        "post_num" to data.post_num.toString(),
                        "flag" to (!data.isHeart).toString()
                    )
                )

                val queue = Volley.newRequestQueue(applicationContext)
                queue.add(heartrequest)
            }

            override fun onItemBook(
                v: View,
                data: BusinessBoardItem,
                pos: Int
            ) {
                val bookrequest = Board_Request(
                    Request.Method.POST,
                    "http://seonho.dothome.co.kr/BusinessBookmark.php",
                    { response ->
                        if (response != "Bookmark fail") {
                            data.isBookm = !data.isBookm
                            Toast.makeText(
                                applicationContext,
                                "북마크 완료",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Log.d("bookrequest fail", response)
                        }
                    },
                    {
                        Log.d(
                            "b-bookmark failed",
                            "error......${
                                this?.let { it1 ->
                                    error(
                                        it1
                                    )
                                }
                            }"
                        )
                    },
                    hashMapOf(
                        "id" to appUser,
                        "post_num" to data.post_num.toString(),
                        "flag" to (!data.isBookm).toString()
                    )
                )

                val queue = Volley.newRequestQueue(applicationContext)
                queue.add(bookrequest)
            }

        })
    }

    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                val currentImgUri: Uri? = data?.data
                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        currentImgUri
                    )
                    profileImg.setImageBitmap(bitmap)
                    roundAll(profileImg, 70.0f)

                    var source: ImageDecoder.Source? =
                        currentImgUri?.let {
                            ImageDecoder.createSource(
                                contentResolver,
                                it
                            )
                        }
                    bitmap = source?.let { ImageDecoder.decodeBitmap(it) }


                    bitmap = resize(bitmap)

                    image_profile = BitMapToString(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Log.d("activity result", "wrong")
            }
        }
    }

    fun roundAll(iv: ImageView, curveRadius: Float): ImageView {

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
        var bitmap_width: Int? = bitmap?.width
        var bitmap_height: Int? = bitmap?.height

        bitmap =
            Bitmap.createScaledBitmap(bitmap!!, bitmap_width!!, bitmap_height!!, true)
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
            val bitmap: Bitmap =
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
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