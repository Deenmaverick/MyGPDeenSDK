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

    private var scope = CoroutineScope(Dispatchers.IO)

    @JvmStatic
    var  CallBackListener : DeenAuthCallback? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var token: String? = null

    @JvmStatic
    fun authDeen(context: Context, msisdn:String, callback: DeenAuthCallback? = null)
    {
        this.appContext = context
        this.CallBackListener = callback

        scope.launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            withContext(Dispatchers.Main) {

                if (token != null) {
                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }
    }

    @JvmStatic
    fun openDeen(context: Context)
    {
        if(token!=null)
        {
            val intent =
                Intent(context, MainActivity::class.java)
            intent.putExtra("destination",R.id.dashboardFragment)
            context.startActivity(intent)
        }
        else
            CallBackListener?.onAuthFailed()

    }

}

interface DeenAuthCallback
{
    fun onAuthSuccess()
    fun onAuthFailed()
}