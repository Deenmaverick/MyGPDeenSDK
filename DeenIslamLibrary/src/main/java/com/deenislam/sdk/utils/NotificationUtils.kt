package com.deenislam.sdk.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.libs.notification.AlarmReceiver
import com.deenislam.sdk.views.main.MainActivity

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
fun NotificationManager.sendNotification(
    title:String,
    messageBody: String,
    applicationContext: Context,
    largeImg:Int?,
    channelID:String,
    notification_id:Int
) {

    Log.e("RECIVER_NOTIFY","CALLED")
    // Create the content intent for the notification which launches the MainActivity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(
            applicationContext,
            notification_id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    } else {
        PendingIntent.getBroadcast(
            applicationContext,
            notification_id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }


    val largeImage = largeImg?.let {
        BitmapFactory.decodeResource(
        applicationContext.resources,
            it
    )
    }

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(largeImage)
        .bigLargeIcon(null)


    // Add snooze action
    /*val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        FLAGS)*/


    // Cancel intent for notification dismiss listner

    val DismissIntent = Intent(DeenSDKCore.appContext, AlarmReceiver::class.java)
    DismissIntent.putExtra("dismiss","ok")

    val DismissPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            DeenSDKCore.appContext,
            notification_id,
            DismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    val builder = NotificationCompat.Builder(
        applicationContext,channelID
    )
        .setSmallIcon(R.drawable.ic_notification_small)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setDeleteIntent(DismissPendingIntent)
        .setAutoCancel(true)
        .setLargeIcon(getBitmapFromVectorDrawable(applicationContext,R.mipmap.ic_launcher))
      /*  .addAction(
            R.drawable.button_icon,
            applicationContext.getString(R.string.snooze),
            snoozePendingIntent
        )*/
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if(largeImage!=null)
        builder.setStyle(bigPicStyle)

    notify(notification_id, builder.build())
}

fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context!!, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}