package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000 // 2초 후에 LoginActivity로 이동하도록 지연 시간 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val myLayout: RelativeLayout = findViewById(R.id.myLayout)
//
//// 나타날 때 애니메이션
//        val appearAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
//            Animation.RELATIVE_TO_SELF, 0.5f,
//            Animation.RELATIVE_TO_SELF, 0.5f)
//        appearAnimation.duration = 1000 // 애니메이션 지속 시간 설정
//        myLayout.startAnimation(appearAnimation)
//
//// 사라질 때 애니메이션
//        val disappearAnimation = ScaleAnimation(1f, 0f, 1f, 0f,
//            Animation.RELATIVE_TO_SELF, 0.5f,
//            Animation.RELATIVE_TO_SELF, 0.5f)
//        disappearAnimation.duration = 1000 // 애니메이션 지속 시간 설정
//        disappearAnimation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation?) {
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                myLayout.visibility = View.INVISIBLE // 애니메이션이 끝난 후 레이아웃을 보이지 않도록 설정
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {
//            }
//        })


            // 지연 후에 Login으로 이동
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, Login::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
