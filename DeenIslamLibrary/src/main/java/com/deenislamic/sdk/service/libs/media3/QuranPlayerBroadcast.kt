package com.deenislamic.sdk.service.libs.media3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

internal class QuranPlayerBroadcast:BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action != null) {
            // Send a local broadcast to the service
            val localIntent = Intent(action)
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        }
    }
}