package com.deenislamic.sdk.service.libs.sessiontrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deenislamic.sdk.DeenSDKCore

class SessionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if(DeenSDKCore.appContext == null)
            DeenSDKCore.appContext = context.applicationContext
        if(DeenSDKCore.baseContext == null)
            DeenSDKCore.baseContext = context
        if(DeenSDKCore.GetDeenToken().isEmpty())
            DeenSDKCore.SetDeenToken("MyBLSDK")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {

            }

            else -> {

                val msisdn = intent.extras?.getString("msisdn").toString()
                val sessionStart = intent.extras?.getLong("sessionStart",0)?:0
                val sessionEnd = intent.extras?.getLong("sessionEnd",0)?:0


                val service = Intent(context, SessionReceiverService::class.java)
                service.putExtra("msisdn",msisdn)
                service.putExtra("sessionStart",sessionStart)
                service.putExtra("sessionEnd",sessionEnd)
                context.startService(service)


            }
        }

    }
}