package com.example.medi_nion

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AlarmService_opencv: Service() {
    //여기서 백그라운드로 실행해야되는데 ,,,,,,,,,,,,,,,,,,,,,,,,,,, 우짜지 ?

    companion object {
        private var NOTIFICATION_ID = "medinion"
        private var NOTIFICATION_NAME = "인증 알림"
        private val ALARM_REQUEST_CODE = 1001
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("onStartConmmet", "dfds")
        Intent(this, MainActivity::class.java).flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this, ALARM_REQUEST_CODE, Intent(
                this,
                Login::class.java
            ), FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this@AlarmService_opencv, NOTIFICATION_ID)
            .setContentTitle("[Medi_Nion] 사용자 인증 알림") //타이틀 TEXT
            .setContentText("인증할 수 없습니다. 인증을 다시 시도해주세요.\n프로필 메뉴 > 설정") //세부내용 TEXT
            .setSmallIcon(R.drawable.logo) //필수 (안해주면 에러)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}