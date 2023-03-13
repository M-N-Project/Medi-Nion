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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.board_home.*
import kotlinx.android.synthetic.main.business_board_items.*
import kotlinx.android.synthetic.main.business_home.BusinessBoardRecyclerView
import kotlinx.android.synthetic.main.business_manage_create.*
import org.json.JSONArray


class BusinessManageActivity : AppCompatActivity() {
    //해야할일: 이미지 가져와서 띄울때 프사 및 배경사진에 맞게 크기조절, uri->bitmap으로 바꿔서 DB에 넣기
    private val OPEN_GALLERY = 1

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

        fetchData()

        val write = findViewById<Button>(R.id.write_btn)
        val profileImg = findViewById<ImageView>(R.id.profileImg)
        val backgroundImg = findViewById<ImageView>(R.id.backgroundImg)
        val saveBtn = findViewById<Button>(R.id.save_btn)

        write.setOnClickListener {
            var intent = Intent(this, BusinessWriting::class.java) //비즈니스 글쓰기 액티비티
            intent.putExtra("id", id)
            startActivity(intent)
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

    fun fetchData() {
        // url to post our data
        var id = intent.getStringExtra("id")!!
        val urlBoard = "http://seonho.dothome.co.kr/BusinessManage.php"
        val jsonArray : JSONArray

        val request = Board_Request(
            Request.Method.POST,
            urlBoard,
            { response ->
                val jsonArray = JSONArray(response)
                items.clear()
                all_items.clear()
                for (i in jsonArray.length()-1  downTo  0) {
                    val item = jsonArray.getJSONObject(i)

                    val num = item.getInt("num")
                    val id = item.getString("id")
                    val title = item.getString("title")
                    val content = item.getString("content")
                    val time = item.getString("time")
                    val image1 = item.getString("image1")
                    val image2 = item.getString("image2")
                    val image3 = item.getString("image3")
                    val BusinessItem = BusinessBoardItem(id, title, content, time, image1, image2, image3)

//                    if(i >= jsonArray.length() - item_count*scroll_count){
//                        items.add(BusinessItem)
//                        itemIndex.add(num) //앞에다가 추가.
//                    }
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