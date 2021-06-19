package com.beome.notify

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent == null) return

        val notificationManager = ContextCompat.getSystemService(context!!, NotificationManager::class.java)
        notificationManager?.sendNotification(context, "", "")
    }
}