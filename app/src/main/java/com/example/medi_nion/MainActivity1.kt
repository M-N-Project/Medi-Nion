package com.example.medi_nion

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashMap

//MainActivity.java
class MainActivity1 : AppCompatActivity() {
    private var bitmap: Bitmap? = null
    private var filePath: String? = null
    var imageView: ImageView? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val picUri = data.data
            filePath = getPath(picUri)
            if (filePath != null) {
                try {
                    textView!!.text = "File Selected"
                    Log.d("filePath", filePath.toString())
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, picUri)
                    uploadBitmap(bitmap)
                    imageView!!.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this, "no image selected",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getPath(uri: Uri?): String {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor!!.moveToFirst()
        @SuppressLint("Range") val path = cursor.getString(
            cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        )
        cursor.close()
        return path
    }

    fun getFileDataFromDrawable(bitmap: Bitmap?): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadBitmap(bitmap: Bitmap?) {
        var url = ""
        val volleyMultipartRequest: VolleyMultipartRequest2 =  VolleyMultipartRequest2(
            Request.Method.POST, url,
            Response.Listener<NetworkResponse> { response ->
                try {
                    val obj = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                Log.e("GotError", "" + error.message)
            })
//            {
//            override fun getByteData(): Map<String, VolleyMultipartRequest2.DataPart> {
//                val params: MutableMap<String, VolleyMultipartRequest2.DataPart> = HashMap()
//                val imagename = System.currentTimeMillis()
//                params["image"] = VolleyMultipartRequest2.DataPart(
//                    "$imagename.png",
//                    getFileDataFromDrawable(bitmap)
//                )
//                return params
//            }
//        }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    companion object {
        private const val ROOT_URL = "http://seoforworld.com/api/v1/file-upload.php"
        private const val REQUEST_PERMISSIONS = 100
        private const val PICK_IMAGE_REQUEST = 1
    }
}