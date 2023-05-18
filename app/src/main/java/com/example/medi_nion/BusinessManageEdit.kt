package com.example.medi_nion

import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.board_home.*

class BusinessManageEdit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_manage_edit)
        setSupportActionBar(toolbar2)
        supportActionBar!!.title = ""
        supportActionBar!!.subtitle = "비즈니스 채널 프로필 수정"


        val chanName = intent.getStringExtra("chanName")
        val chanDesc = intent.getStringExtra("chanDesc")
        val chanImgUrl = intent.getStringExtra("chanImg")
        Log.d("비즈니스 수정", "$chanName $chanDesc $chanImgUrl")

        val chanProfileImg = findViewById<ImageView>(R.id.chanProfileImg)
        val task = ImageLoadTask(chanImgUrl, chanProfileImg)
        task.execute()
        roundAll(chanProfileImg, 100.0f)

        val editName = findViewById<EditText>(R.id.editChanName)
        val editDesc = findViewById<EditText>(R.id.editChanDesc)
        editName.setText(chanName)
        editDesc.setText(chanDesc)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.business_chan_manage, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        var id = intent.getStringExtra("id")

        return when(item.itemId){
            R.id.done -> {
                val intent = Intent(this, BusinessManageActivity::class.java)
                val id = intent.getStringExtra("id").toString()
                intent.putExtra("id", id)
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
}