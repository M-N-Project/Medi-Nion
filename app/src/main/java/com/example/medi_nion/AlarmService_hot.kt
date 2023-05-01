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


class AlarmService_hot: Service() {
    //여기서 백그라운드로 실행해야되는데 ,,,,,,,,,,,,,,,,,,,,,,,,,,, 우짜지 ?

    companion object {
        const val CHANNEL_ID = "medinion"
        const val CHANNEL_NAME = "hot alarm"
        private const val ALARM_REQUEST_CODE = 1000
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("onStartConmmet", "dfds")
        Intent(this, HomeFragment::class.java).flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this, ALARM_REQUEST_CODE, Intent(
                this,
                Login::class.java
            ), FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("[medinion] HOT 게시물 알림")
            .setContentText("어제의 HOT했던 게시물을 확인해보세요!")
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
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