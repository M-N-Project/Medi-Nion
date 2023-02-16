package com.example.medi_nion

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import java.io.FileNotFoundException


class CreateBoard : AppCompatActivity() {

    private val GALLERY = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)

        var imgbtn = findViewById<ImageButton>(R.id.imageButton_gallery)
        var postTitle = findViewById<EditText>(R.id.editText_Title)
        var postContent = findViewById<EditText>(R.id.editText_Content)
        var postbtn = findViewById<Button>(R.id.post_Btn)
        var board_select = findViewById<TextView>(R.id.board_select)
        var select_RadioGroup = findViewById<RadioGroup>(R.id.select_RadioGroup)
        var free_RadioBtn = findViewById<RadioButton>(R.id.free_RadioBtn)
        var job_RadioBtn = findViewById<RadioButton>(R.id.job_RadioBtn)
        var department_RadioBtn = findViewById<RadioButton>(R.id.department_RadioBtn)
        var my_hospital_RadioBtn = findViewById<RadioButton>(R.id.my_hospital_RadioBtn)
        var market_RadioBtn = findViewById<RadioButton>(R.id.market_RadioBtn)
        var QnA_RadioBtn = findViewById<RadioButton>(R.id.QnA_RadioBtn)

        val url_Post = "http://seonho.dothome.co.kr/createBoard.php"

        imgbtn.setOnClickListener { //imageButton_gallery 클릭시 갤러리로 이동
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //intent.setType("image/*)
            startActivityForResult(intent, GALLERY)
        }

        board_select.setOnClickListener {
            select_RadioGroup.visibility = View.VISIBLE
            select_RadioGroup.bringToFront()

            free_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "전체 게시판"
            }

            job_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "직종별 게시판"
            }

            department_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "진료과별 게시판"
            }

            my_hospital_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "우리 병원 게시판"
            }

            market_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "장터 게시판"
            }

            QnA_RadioBtn.setOnClickListener {
                select_RadioGroup.visibility = View.GONE
                board_select.text = "QnA 게시판"
            }
        }

        postbtn.setOnClickListener {
            if(postTitle.length() == 0 || postContent.length() == 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("경고")
                    .setMessage("제목 또는 내용을 입력해주세요.")
                    .setNegativeButton("닫기"
                    ) { dialog, which ->
                    }
                builder.show()
            } else {
                createBoardRequest(url_Post)
                var intent = Intent(applicationContext, Board::class.java)
                startActivity(intent)
            }

        }
    }

    private fun createBoardRequest(postUrl: String) {
        var postTitle = findViewById<EditText>(R.id.editText_Title).text.toString()
        var postContent = findViewById<EditText>(R.id.editText_Content).text.toString()
        var board_select = findViewById<TextView>(R.id.board_select).text.toString()

        val request = SignUP_Request(
            Request.Method.POST,
            postUrl,
            { response ->
                val success = true
                if (success) {
                    postTitle = response.toString()
                    postContent = response.toString()
                    board_select = response.toString()

                    Toast.makeText(
                        baseContext,
                        String.format("게시물 업로드가 완료되었습니다."),
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "Post success",
                        "$postTitle, $postContent, $board_select"
                    )
                } else {
                    Toast.makeText(
                        applicationContext,
                        "게시물 업로드가 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { Log.d("failed", "error......${error(applicationContext)}") },

                hashMapOf(
                    "board" to board_select,
                    "title" to postTitle,
                    "content" to postContent
                )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //사진 첨부시 갤러리로 이동시켜주는,,
        super.onActivityResult(requestCode, resultCode, data)
        var imgbtn = findViewById<ImageButton>(R.id.imageButton_gallery)
        var postbtn = findViewById<Button>(R.id.post_Btn)

        if( resultCode == Activity.RESULT_OK) { //호출 코드 확인
            if( requestCode ==  GALLERY)
            {
                var ImageData: Uri? = data?.data
                imgbtn.setImageURI(ImageData)

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImageData)
                    imgbtn.setImageBitmap(bitmap)

                    var imagePath = ImageData?.let { getPath(context = baseContext, it) }.toString()

                    Log.d("<<<<<<<<<<<<<<<<<<<<<<", imagePath)

                    AlertDialog.Builder(this).setMessage(imagePath).create().show()

                    val request = SignUP_Request(
                        Request.Method.POST,
                        url = "http://seonho.dothome.co.kr/Image.php",
                        { response ->
                            val success = true
                            if (success) {
                                imagePath = response.toString()

                                Toast.makeText(
                                    baseContext,
                                    String.format("이미지를 불러왔습니다."),
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d(
                                    "Image success",
                                    "$imagePath"
                                )
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "이미지를 불러오는 데 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        { Log.d("failed", "error......${error(applicationContext)}") },

                        hashMapOf(
                            "imagePath" to imagePath
                        )
                    )
                    val queue = Volley.newRequestQueue(this)
                    queue.add(request)

                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
                catch (e: FileNotFoundException)
                {
                    e.printStackTrace()
                }
            }
        }
    }


    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = uri?.let {
                context.getContentResolver().query(
                    it, projection, selection, selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}