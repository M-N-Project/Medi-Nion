package com.example.medi_nion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.business_home.BusinessBoardRecyclerView
import kotlinx.android.synthetic.main.business_manage_create.*

class BusinessManageActivity : AppCompatActivity() {
    //해야할일: 이미지 가져와서 띄울때 프사 및 배경사진에 맞게 크기조절, uri->bitmap으로 바꿔서 DB에 넣기
    private val OPEN_GALLERY = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_create)
        val id:String? = this.intent.getStringExtra("id")

        val businessBoard = ArrayList<BusinessBoardItem>() //일단 더미데이터, db 연동해야함
        businessBoard.add(BusinessBoardItem(
            id.toString(), "2023년 2월 15일 오후 1시 30분",getDrawable(R.drawable.business_profile_img)!!,
            "이것은 내용입니다. 약사세요~ 줄바꿈도 해야한답니다", 1, 2))

        businessBoard.add(BusinessBoardItem("1월 이벤트!!", "2023년 1월 11일 오전 10시 30분",getDrawable(R.drawable.business_profile_img)!!,
            "반가워요 1월이 밝았네요 이벤트 어쩌구 저쩌구", 3, 2))

        //이벤트 연결중,,,
        val adapter = BusinessRecyclerAdapter(businessBoard)
        BusinessBoardRecyclerView.adapter = adapter

        val write = findViewById<Button>(R.id.write_btn)
        val profileImg = findViewById<ImageView>(R.id.profileImg)
        val backgroundImg = findViewById<ImageView>(R.id.backgroundImg)
        val saveBtn = findViewById<Button>(R.id.save_btn)

        write.setOnClickListener {
            var newIntent = Intent(this, BusinessWriting::class.java) //비즈니스 글쓰기 액티비티
            startActivity(newIntent)
        }

        profileImg.setOnClickListener {
            //프로필 이미지 수정하게 하는,,
            Toast.makeText(this, "동그란 맘속에 피어난 how is the life", Toast.LENGTH_SHORT).show()
            openGallery() //갤러리로 이동
        }

        backgroundImg.setOnClickListener {
            //배경 이미지 수정하게 하는,,
            Toast.makeText(this, "사람 찾아 인생을 찾아~", Toast.LENGTH_SHORT).show()
            openGallery() //갤러리로 이동
        }

        saveBtn.setOnClickListener {
            Toast.makeText(this, "저장을 할 예정인 버튼이에요~", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == OPEN_GALLERY) {
                val currentImgUri : Uri? = data?.data

                if(profileImg.isClickable) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                        profileImg.setImageBitmap(bitmap)
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }

                else if(backgroundImg.isClickable) {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImgUri)
                        backgroundImg.setImageBitmap(bitmap)
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }

            } else {
                Log.d("activity result", "wrong")
            }
        }
    }
}