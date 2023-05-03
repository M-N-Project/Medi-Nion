package com.example.medi_nion


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver_hot : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmIntentServiceIntent = Intent(
            context,
            AlarmService_hot::class.java
        )
        context!!.startService(alarmIntentServiceIntent)
    }

}