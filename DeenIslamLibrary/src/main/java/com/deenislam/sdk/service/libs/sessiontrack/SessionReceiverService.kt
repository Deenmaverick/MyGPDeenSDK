package com.deenislam.sdk.service.libs.sessiontrack

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.UserTrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionReceiverService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val serviceContext: Context = this
        if(DeenSDKCore.appContext == null)
            DeenSDKCore.appContext = this.applicationContext
        if(DeenSDKCore.baseContext == null)
            DeenSDKCore.baseContext = this
        if(DeenSDKCore.GetDeenToken().isEmpty())
            DeenSDKCore.SetDeenToken("MyBLSDK")

        val msisdn = intent?.extras?.getString("msisdn").toString()
        val sessionStart = intent?.extras?.getLong("sessionStart",0)?:0
        val sessionEnd = intent?.extras?.getLong("sessionEnd",0)?:0

        Log.e("MyBLSDK","OKK11111")

        CoroutineScope(Dispatchers.IO).launch {
            sendUserSession(msisdn,sessionStart,sessionEnd)
        }

        return super.onStartCommand(intent, flags, startId)
    }


    suspend fun sendUserSession(msisdn: String, sessionStart: Long, sessionEnd: Long) =
    withContext(Dispatchers.IO)
    {

        val userTrackRepository = UserTrackRepository(authenticateService = NetworkProvider().getInstance().provideAuthService())
        userTrackRepository.userSessionTrack(msisdn = msisdn, sessionStart = sessionStart,SessionEnd=sessionEnd)
    }

}