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
import android.os.PersistableBundle
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class BusinessManageEdit : AppCompatActivity() {
    private val GALLERY = 1
    var image_profile: String = "null"
    lateinit var bitmap: Bitmap
    var profileEncoded: String = ""

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
            openGallery()
        }

        val chanDelete = findViewById<Button>(R.id.chanDelete)
        chanDelete.setOnClickListener {
            // 채널 삭제
        }
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

        var profileImg = findViewById<ImageView>(R.id.chanProfileImg)
        val id: String? = this.intent.getStringExtra("id")
        //val progressBar = findViewById<ProgressBar>(R.id.progressbarBusiness)
        //val loadingText = findViewById<TextView>(R.id.loading_textView_business)
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

//                    val setting_RadioGroup =
//                        findViewById<RadioGroup>(R.id.businessSetting_RadioGroup)
//                    setting_RadioGroup.visibility = View.GONE

//                    loadingText.visibility = View.VISIBLE
//                    loadingText.text = "프로필 사진 업로드는 최대 2분 소요될 수 있습니다."
//                    loadingText.bringToFront()
//                    progressBar.visibility = View.VISIBLE
//                    progressBar.bringToFront()


                    //uploadDataToDB()

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } else {
                Log.d("activity result", "wrong")
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
        var chanName = findViewById<EditText>(R.id.editChanName)

        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.done -> {
                uploadDataToDB()
                val intent = Intent(this, BusinessManageActivity::class.java)
                val id = this.intent.getStringExtra("id").toString()
                intent.putExtra("id", id)
                intent.putExtra("chanName", chanName.text)
                intent.putExtra("isFirst", false)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
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