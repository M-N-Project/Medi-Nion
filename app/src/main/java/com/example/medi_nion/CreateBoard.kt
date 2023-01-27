package com.example.medi_nion

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CreateBoard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_writing)

        var imgbtn = findViewById<ImageButton>(R.id.imageButton)
        var title = findViewById<EditText>(R.id.editText_Title)
        var content = findViewById<EditText>(R.id.editText_Content)
    }


    private fun loadImage() {
        //db
    }

    private fun uploadPost() {
        //db
    }

}