package com.deenislam.sdk

import android.content.Context
import androidx.annotation.Keep
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.AuthenticateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Keep
object Deen {

    private var scope = CoroutineScope(Dispatchers.IO)
    private var  CallBackListener : DeenAuthCallback? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var token: String? = null

    @JvmStatic
    fun authDeen(context: Context,msisdn:String)
    {
        appContext = context

        scope.launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            withContext(Dispatchers.Main) {

                if (token != null) {
                    //val intent = Intent(context, MainActivity::class.java)
                    //context.startActivity(intent)
                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }
    }

}

interface DeenAuthCallback
{
    fun onAuthSuccess()
    fun onAuthFailed()
}