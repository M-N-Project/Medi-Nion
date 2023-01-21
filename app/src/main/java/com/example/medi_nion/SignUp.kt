package com.example.medi_nion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        //test

        var userTypeGroup = findViewById<RadioGroup>(R.id.userType_RadioGroup)

        userTypeGroup.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                //R.id.basicUser_RadioBtn ->
                //R.id.corpUser_RadioBtn ->
            }
        }



//        LinearLayout View = (LinearLayout)findViewById(R.id.mainview);
//        Button button = new Button(this);
//        View.addView(button);

    }
}