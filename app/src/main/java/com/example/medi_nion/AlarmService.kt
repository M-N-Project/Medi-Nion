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


class AlarmService: Service() {
    //여기서 백그라운드로 실행해야되는데 ,,,,,,,,,,,,,,,,,,,,,,,,,,, 우짜지 ?

    companion object {
        const val CHANNEL_ID = "medinion"
        const val CHANNEL_NAME = "schedule alarm"
        private const val ALARM_REQUEST_CODE = 1000
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("onStartConmmet", "dfds")
        Intent(this, Calendar_Add::class.java).flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this, ALARM_REQUEST_CODE, Intent(
                this,
                CalendarFragment::class.java
            ), FLAG_MUTABLE
        )

//        val requestCode = intent?.extras!!.getInt("alarm_rqCode")
//        val title = intent.extras!!.getString("content")

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("[medinion] 캘린더 알람")
            .setContentText("오늘의 일정을 확인하세요!")
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