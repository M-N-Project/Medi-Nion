package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Gallery
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.nio.channels.GatheringByteChannel

class CreateBoard : AppCompatActivity() {

    private val GALLERY = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)

        var imgbtn = findViewById<ImageButton>(R.id.imageButton_gallery)
        var title = findViewById<EditText>(R.id.editText_Title)
        var content = findViewById<EditText>(R.id.editText_Content)

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //intent.setType("image/*)
            startActivityForResult(intent, GALLERY)

        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //사진 첨부시 갤러리로 이동시켜주는,,
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) { //호출 코드 확인
            if( requestCode ==  GALLERY)
            {
                var ImnageData: Uri? = data?.data
                Toast.makeText(this,ImnageData.toString(), Toast.LENGTH_SHORT ).show()
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImnageData)
                    //imgbtn.setImageBitmap(bitmap) -> 수정해야됨,,,
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun loadImage() {
        //db
    }

    private fun uploadPost() {
        //db
    }



}