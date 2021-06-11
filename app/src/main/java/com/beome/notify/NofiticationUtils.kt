package com.beome.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.beome.MainActivity
import com.beome.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(context : Context, username : String, action : String){
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val builder = NotificationCompat.Builder(context, context.getString(R.string.notif_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(context.getString(R.string.notif_title))
        .setContentText(context.getString(R.string.notif_message, username, action))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID, builder.build())
}

fun createChannel(context: Context, idNotif : Int, nameNotif : Int, descNotif : Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationChannel = NotificationChannel(context.getString(idNotif), context.getString(nameNotif), NotificationManager.IMPORTANCE_HIGH)
        with(notificationChannel){
            setShowBadge(true)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = context.getString(descNotif)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}
