package com.example.medi_nion

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Outline
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import java.io.ByteArrayOutputStream

class BusinessManageEdit : AppCompatActivity() {
    private val GALLERY = 1
    var image_profile: String = "null"
    lateinit var bitmap: Bitmap
    var profileEncoded: String = ""
    private var isImgSelected :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_edit)
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        supportActionBar!!.subtitle = "비즈니스 채널 프로필 수정"


        val chanName = intent.getStringExtra("chanName")
        val chanDesc = intent.getStringExtra("chanDesc")
        val chanImgUrl = intent.getStringExtra("chanImg")
        val isFirst = intent.getBooleanExtra("isFirst", false)

        val chanProfileImg = findViewById<ImageView>(R.id.chanProfileImg)
        val editName = findViewById<EditText>(R.id.editChanName)
        val editDesc = findViewById<EditText>(R.id.editChanDesc)

        if(!isFirst) {
            val task = ImageLoadTask(chanImgUrl, chanProfileImg)
            task.execute()
            roundAll(chanProfileImg, 100.0f)

            editName.setText(chanName)
            editDesc.setText(chanDesc)
        } else{
            chanProfileImg.setImageResource(R.drawable.person_filled)
        }

        chanProfileImg.setOnClickListener {
            isImgSelected = true
            openGallery()
        }

        val chanDelete = findViewById<Button>(R.id.chanDelete)
        chanDelete.setOnClickListener {
            // 다이얼로그 띄우고 OK 누르면
            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object : CustomDialog.OnDialogClickListener {
                override fun onClicked()
                {
                    // 채널 삭제
                    deleteFromDB()
                }

            })
        }
    }

    private fun deleteFromDB() {
        var id = intent.getStringExtra("id")!!
        val urlBusinessProfileDelete = "http://seonho.dothome.co.kr/BusinessProfileDelete.php"

        val request: StringRequest =
            object : StringRequest(
                Method.POST,
                urlBusinessProfileDelete,
                object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        if (response != null) {
                            Log.d("비즈니스 삭제", response)
                        }
                        // profile Fragment로 이동
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        error.printStackTrace()
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val map: MutableMap<String, String> = HashMap()
                    // 1번 인자는 PHP 파일의 $_POST['']; 부분과 똑같이 해줘야 한다
                    map["id"] = id
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

    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    private fun uploadDataToDB() {
        var id = intent.getStringExtra("id")!!
        var isFirst = intent.getBooleanExtra("isFirst", true)
        val chanImgUrl = intent.getStringExtra("chanImg")

        val channel_name = findViewById<EditText>(R.id.editChanName).text.toString()
        val channel_desc = findViewById<EditText>(R.id.editChanDesc).text.toString()

        //val progressBar = findViewById<ProgressBar>(R.id.progressbarBusiness)
        //val loadingText = findViewById<TextView>(R.id.loading_textView_business)

        val urlBusinessProfileUpdate = "http://seonho.dothome.co.kr/BusinessProfileUpdate2.php"

        Intent(this, BusinessProfileService::class.java).also { intent ->
            intent.putExtra("id", id)
            intent.putExtra("isFirst", isFirst)
            intent.putExtra("channel_name", channel_name)
            intent.putExtra("channel_desc", channel_desc)
            intent.putExtra("profile_img", profileEncoded)

            startService(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { //uri -> bitmap
        super.onActivityResult(requestCode, resultCode, data)

        if(isImgSelected) {
            var profileImg = findViewById<ImageView>(R.id.chanProfileImg)
            val id: String? = this.intent.getStringExtra("id")
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY) {
                    val currentImgUri: Uri? = data?.data

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                        profileImg.setImageBitmap(bitmap)

                        bitmap = resizeProfile(bitmap)
                        image_profile = BitMapToString(bitmap)
                        encodeBitmapImage(bitmap)

                        roundAll(profileImg, 70.0f)

                        var source: ImageDecoder.Source? =
                            currentImgUri?.let { ImageDecoder.createSource(contentResolver, it) }
                        bitmap = source?.let { ImageDecoder.decodeBitmap(it) }!!

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    Log.d("activity result", "wrong")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.business_chan_manage, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        var id = intent.getStringExtra("id")
        var isFirst = intent.getBooleanExtra("isFirst", true)
        var chanName = findViewById<EditText>(R.id.editChanName)
        var chanDesc = findViewById<EditText>(R.id.editChanDesc)

        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.done -> {
                if(isFirst) {
                    if (chanName.text.isNotEmpty() && chanDesc.text.isNotEmpty() && profileEncoded.length > 0) {
                        uploadDataToDB()
                        Toast.makeText(this, "사진 업로드는 최대 2분 소요됩니다.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, BusinessManageActivity::class.java)
                        val id = this.intent.getStringExtra("id").toString()
                        intent.putExtra("id", id)
                        intent.putExtra("chanName", chanName.text)
                        intent.putExtra("isFirst", false)
                        intent.putExtra("loading", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "채널 이름과 소개글 모두 작성해주세요",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else{
                    if (chanName.text.isNotEmpty() && chanDesc.text.isNotEmpty()) {
                        uploadDataToDB()
                        val intent = Intent(this, BusinessManageActivity::class.java)
                        val id = this.intent.getStringExtra("id").toString()
                        intent.putExtra("id", id)
                        intent.putExtra("chanName", chanName.text)
                        intent.putExtra("isFirst", false)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "채널 이름과 소개글 모두 작성해주세요",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
                true
            }
            else -> {super.onOptionsItemSelected(item)}
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

    private fun resizeProfile(bitmap: Bitmap): Bitmap {
        var bitmap: Bitmap? = bitmap
        val config: Configuration = Resources.getSystem().configuration
        var bitmap_width: Int? = bitmap?.width
        var bitmap_height: Int? = bitmap?.height

        val resize_size = 260

        //사진의 가로길이가 더 길거나 같으면
        if (bitmap_width != null && bitmap_height != null) {
            if (bitmap_width >= bitmap_height) {

                val ratio = (bitmap_height * resize_size) / bitmap_width
                bitmap = Bitmap.createScaledBitmap(bitmap!!, resize_size, ratio, true)
            }
            //사진의 세로길이가 더 길면
            else {
                val ratio = (bitmap_width * resize_size) / bitmap_height
                bitmap = Bitmap.createScaledBitmap(bitmap!!, ratio, resize_size, true)
            }
        }
        return bitmap!!
    }

    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesOfImage = byteArrayOutputStream.toByteArray()
        profileEncoded = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)
    }
}