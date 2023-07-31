package com.deenislam.sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.Keep
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.AuthenticateRepository
import com.deenislam.sdk.service.repository.SettingRepository
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
    var language = "bn"

    @JvmStatic
    fun openDeen(context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback

        CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn).apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }

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