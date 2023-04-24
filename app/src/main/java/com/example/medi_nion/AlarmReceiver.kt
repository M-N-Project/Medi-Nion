package com.example.medi_nion


import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

class AlarmRecevier : BroadcastReceiver() {
    var manager: NotificationManager? = null
    var builder: NotificationCompat.Builder? = null
    companion object {
        //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
        private const val CHANNEL_ID = "medinion"
        private const val CHANNEL_NAME = "calendar alarm"
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        builder = null
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager!!.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(context)
        }

        //알림창 클릭 시 activity 화면 부름
        val intent2 = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT)

        //알림창 제목
        builder!!.setContentTitle("캘린더 알람")
        //알림창 아이콘
        builder!!.setSmallIcon(R.drawable.ic_launcher_background)
        //알림창 터치시 자동 삭제
        builder!!.setAutoCancel(true)
        builder!!.setContentIntent(pendingIntent)
        val notification: Notification = builder!!.build()
        manager!!.notify(1, notification)
    }

}