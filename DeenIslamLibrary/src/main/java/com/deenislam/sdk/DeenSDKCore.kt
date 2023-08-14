package com.deenislam.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.auth.jwt.JwtResponse
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.repository.AuthenticateRepository
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import com.deenislam.sdk.views.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("StaticFieldLeak")
@Keep
object DeenSDKCore {

    @JvmStatic
    var  CallBackListener : DeenSDKCallback? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var baseContext: Context? = null

    @JvmStatic
    var token: String = ""

    @JvmStatic
    var language = "bn"

    @JvmStatic
    var msisdn:String = ""


    @JvmStatic
    fun initDeen(context: Context, token:String, callback: DeenSDKCallback? = null)
    {

        this.baseContext = context
        this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.token = token

        try {
            val decoded = JWTdecode(token)
           msisdn = decoded.payload.name
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO).launch {

            if(!AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).initSDK(token, msisdn))
            {
                baseContext = null
                appContext = null
                CallBackListener = callback
            }

            withContext(Dispatchers.Main)
            {
                checkAuth()
            }
        }

    }

    private fun checkAuth(): Boolean {
        if(appContext == null || baseContext == null || token.isEmpty() || msisdn.isBlank()) {
            CallBackListener?.onDeenSDKInitFailed()
            return false
        }
        else
            CallBackListener?.onDeenSDKInitSuccess()

        return true
    }

    @JvmStatic
    fun openDeen()
    {

        if (!checkAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",R.id.dashboardFragment)
        baseContext?.startActivity(intent)

       /* this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn*/

        /*CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            *//*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*//*

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

        }*/
    }

    @JvmStatic
    fun openTasbeeh()
    {

        if (!checkAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",R.id.tasbeehFragment)
        baseContext?.startActivity(intent)

        /*this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn*/

        /*CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            *//*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*//*

            withContext(Dispatchers.Main) {

                if (token != null && token?.isNotEmpty() == true) {

                    val intent =
                        Intent(context, MainActivity::class.java)
                    intent.putExtra("destination",R.id.tasbeehFragment)
                    context.startActivity(intent)

                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }*/
    }

    @JvmStatic
    fun openPrayerTime()
    {

        if (!checkAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",R.id.prayerTimesFragment)
        baseContext?.startActivity(intent)
       /* this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn*/

        /*CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            *//*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*//*

            withContext(Dispatchers.Main) {

                if (token != null && token?.isNotEmpty() == true) {

                    val intent =
                        Intent(context, MainActivity::class.java)
                    intent.putExtra("destination",R.id.prayerTimesFragment)
                    context.startActivity(intent)

                    CallBackListener?.onAuthSuccess()

                } else {
                    CallBackListener?.onAuthFailed()
                }
            }

        }*/
    }

    @JvmStatic
    fun prayerNotification(isEnabled: Boolean)
    {
        if (!checkAuth()) {
            return
        }

        val prayerDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
            Date()
        )


        CoroutineScope(Dispatchers.IO).launch {

            if(isEnabled)
            {

           /* token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

                if (token != null && token?.isNotEmpty() == true) {*/


                        val prayerTimesRepository = PrayerTimesRepository(
                            deenService = NetworkProvider().getInstance().provideDeenService(),
                            prayerNotificationDao = DatabaseProvider().getInstance()
                                .providePrayerNotificationDao(),
                            prayerTimesDao = null
                        )


                        var getPrayerTime = async { prayerTimesRepository.getPrayerTimes("Dhaka", language, prayerDate) }

                    val finalPrayerTime = getPrayerTime.await()
                        var prayerTimesResponse: PrayerTimesResponse? = null

                        when (finalPrayerTime) {
                            is ApiResource.Failure -> {
                                withContext(Dispatchers.Main)
                                {
                                    CallBackListener?.DeenPrayerNotificationFailed()
                                }

                                return@launch
                            }

                            is ApiResource.Success -> {
                                prayerTimesResponse = finalPrayerTime.value
                            }
                        }


                        prayerTimesResponse?.let {

                            var prayerNotifyCount = 0

                            if (prayerTimesRepository.updatePrayerNotification(
                                    prayerDate,
                                    "pt1",
                                    3,
                                    "",
                                    it
                                ) == 1
                            )
                                prayerNotifyCount++

                            if (prayerTimesRepository.updatePrayerNotification(
                                    prayerDate,
                                    "pt3",
                                    3,
                                    "",
                                    it
                                ) == 1
                            )
                                prayerNotifyCount++
                             if (prayerTimesRepository.updatePrayerNotification(
                                    prayerDate,
                                    "pt4",
                                    3,
                                    "",
                                    it
                                ) == 1
                            )
                                prayerNotifyCount++

                             if (prayerTimesRepository.updatePrayerNotification(
                                    prayerDate,
                                    "pt5",
                                    3,
                                    "",
                                    it
                                ) == 1
                            )
                                prayerNotifyCount++

                             if (prayerTimesRepository.updatePrayerNotification(
                                    prayerDate,
                                    "pt6",
                                    3,
                                    "",
                                    it
                                ) == 1
                            )
                                prayerNotifyCount++

                            Log.e("DEEN_NOTIFY",prayerNotifyCount.toString())

                            if (prayerNotifyCount > 0) {
                                withContext(Dispatchers.Main)
                                {
                                    CallBackListener?.DeenPrayerNotificationOn()
                                }
                            }
                            else {
                                withContext(Dispatchers.Main)
                                {
                                    CallBackListener?.DeenPrayerNotificationFailed()
                                }
                            }


                        } ?:
                        withContext(Dispatchers.Main)
                        {
                            CallBackListener?.DeenPrayerNotificationFailed()
                        }


                    //}


        }else
            {

                val prayerTimesRepository = PrayerTimesRepository(
                    deenService = NetworkProvider().getInstance().provideDeenService(),
                    prayerNotificationDao = DatabaseProvider().getInstance()
                        .providePrayerNotificationDao(),
                    prayerTimesDao = null
                )

                if(prayerTimesRepository.clearPrayerNotification(prayerDate)>0)
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationOff()
                    }
                else
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationFailed()
                    }


            }

        }
    }

    @JvmStatic
    fun destroySDK() {
        CallBackListener = null
        appContext = null
        baseContext = null
    }

   private fun JWTdecode(jwtToken: String): JwtResponse {
        val split = jwtToken.split("\\.".toRegex()).toTypedArray()
        if (split.size < 2) {
            throw IllegalArgumentException("Invalid JWT token.")
        }

        val header = String(Base64.decode(split[0], Base64.DEFAULT))
        val payload = String(Base64.decode(split[1], Base64.DEFAULT))

        val headerJson = JSONObject(header)
        val payloadJson = JSONObject(payload)

        val result = JSONObject()
        result.put("header", headerJson)
        result.put("payload", payloadJson)

        return Gson().fromJson(result.toString(),JwtResponse::class.java)
    }


}



interface DeenSDKCallback
{
    fun onDeenSDKInitSuccess()
    fun onDeenSDKInitFailed()
    fun DeenPrayerNotificationOn()
    fun DeenPrayerNotificationOff()
    fun DeenPrayerNotificationFailed()
}