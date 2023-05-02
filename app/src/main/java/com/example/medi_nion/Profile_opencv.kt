package com.example.medi_nion

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class Profile_opencv: AppCompatActivity() {
    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    var photoUri: Uri? = null
    var image : String = "null"
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_opencv)

        var id = intent.getStringExtra("id")
        var userType = intent.getStringExtra("userType")
        var userDept = intent.getStringExtra("userDept")
        var userGrade = intent.getStringExtra("userGrade")
        var nickname = intent.getStringExtra("nickname")
        var button_opencv = findViewById<Button>(R.id.opencv_button)
        var identity_img = findViewById<ImageView>(R.id.identity_imageView)
        var intent = Intent(this, ProfileFragment::class.java)
        intent.putExtra("id", id)
        intent.putExtra("userType", userType)
        intent.putExtra("userDept", userDept)
        intent.putExtra("userGrade", userGrade)
        intent.putExtra("nickname", nickname)

        updateIdentity()

        cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("01503", "oepn Cmaera")
                openCamera()
            } else {
                Toast.makeText(applicationContext, "권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            identity_img?.setImageURI(photoUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bitmap = photoUri?.let { it1 -> ImageuriToBitmap(it1) }!!
                image = BitMapToString(bitmap)
                Toast.makeText(applicationContext, "신분증을 분석합니다. 최대 3일 소요됩니다.", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
            Log.d("phto", "uri")
        }
    }


    fun updateIdentity() {
        var button_opencv = findViewById<Button>(R.id.opencv_button)
        val id = intent.getStringExtra("id").toString()
        val updateidentityurl = "http://seonho.dothome.co.kr/identity_update.php"
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", id)
        var img1 : String = ""
        var img2 : String = ""

        if(image != "null"){
            img1 = image.substring(0,image.length/2+1)
            img2 = image.substring(image.length/2+1,image.length)
        }

        val request = Login_Request(
            Request.Method.POST,
            updateidentityurl,
            { response ->
                Log.d("idendkd", response.toString())
                if(!response.equals("updateIdentity fail")) {
                    button_opencv.setOnClickListener {
                        if(hasPermission(this, android.Manifest.permission.CAMERA)) {
                            openCamera()
                            Log.d("123456", "789")
                            updateIdentity()
                        } else {
                            setViews()
                            Log.d("789", "789")
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "신분증을 다시 촬영해주세요.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Log.d(
                    "Indentity update failed",
                    "error......${applicationContext?.let { it1 -> error(it1) }}"
                )
            },
            hashMapOf(
                "id" to id,
                "identity_image1" to img1,
                "identity_image2" to img2
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    fun hasPermission(context: FragmentActivity?, permission: String): Boolean {
        val cameraPermissionCheck = context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1,
                android.Manifest.permission.CAMERA
            )
        }
        return if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            Log.d("LDF", "DLF")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
            false
        } else { //권한이 있는 경우
            Log.d("dsfs","dfs")
            true
        }
    }

    //카메라 권한 요청
    fun setViews() {
        cameraPermission.launch(android.Manifest.permission.CAMERA)
    }

    private fun openCamera() {
        Log.d("123", "456")

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        photoUri = this.let {
            FileProvider.getUriForFile(
                it, "${applicationContext?.packageName}.provider", photoFile
            )
        }
        cameraLauncher.launch(photoUri)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ImageuriToBitmap(photouri: Uri): Bitmap {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = this.let {
                    ImageDecoder.createSource(
                        it.contentResolver,
                        photoUri!!
                    )
                }?.let { ImageDecoder.decodeBitmap(it) }!!
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bitmap compress
        val arr = baos.toByteArray()
        val base64Image = Base64.encodeToString(arr, Base64.DEFAULT)
        var temp = ""
        try {
            //temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return base64Image
    }
}