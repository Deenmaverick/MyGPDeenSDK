package com.deenislam.sdk

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.AuthenticateRepository
import com.deenislam.sdk.views.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Keep
object Deen {

    @JvmStatic
    var  CallBackListener : DeenCallback? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var token: String? = null

    @JvmStatic
    fun openDeen(context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback

        CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            withContext(Dispatchers.Main) {

                if (token != null && token?.isNotEmpty() == true) {

                    val intent =
                        Intent(context, MainActivity::class.java)
                    intent.putExtra("destination",R.id.dashboardFragment)
                    context.startActivity(intent)

                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }
    }

}

interface DeenCallback
{
    fun onAuthSuccess()
    fun onAuthFailed()
}