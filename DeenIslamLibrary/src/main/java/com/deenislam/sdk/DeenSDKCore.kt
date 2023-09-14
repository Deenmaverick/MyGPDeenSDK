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
import com.deenislam.sdk.utils.MilliSecondToStringTime
import com.deenislam.sdk.utils.StringTimeToMillisecond
import com.deenislam.sdk.utils.getRCDestination
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

    var  prayerDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
        Date()
    )

    private var isTodayNotificationSet = false


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
        if(appContext == null || baseContext == null || token.isEmpty() || msisdn.isEmpty()) {
            CallBackListener?.onDeenSDKInitFailed()
            return false
        }
        else
            CallBackListener?.onDeenSDKInitSuccess()

        return true
    }

    private fun reCheckAuth(): Boolean {
        if(appContext == null || baseContext == null || token.isEmpty() || msisdn.isEmpty()) {
            CallBackListener?.onDeenSDKOperationFailed()
            return false
        }
        else
            CallBackListener?.onDeenSDKOperationSuccess()

        return true
    }

     private fun authSDK(context: Context, getmsisdn:String, callback: DeenSDKCallback? = null)
    {

        this.baseContext = context
        this.appContext = context.applicationContext
        this.CallBackListener = callback

        CoroutineScope(Dispatchers.IO).launch {

            val authenticateRepository = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            )

            token = authenticateRepository.authDeen(getmsisdn).toString().apply {

                try {
                    val decoded = JWTdecode(this)
                    msisdn = decoded.payload.name
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Log.e("authSDK", msisdn)
            }

            withContext(Dispatchers.Main)
            {
                checkAuth()
            }

        }

        }

    @JvmStatic
    fun openDeen()
    {

        if (!reCheckAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",R.id.action_blankFragment_to_dashboardFakeFragment)
        baseContext?.startActivity(intent)

    }

    @JvmStatic
    fun openFromRC(rc:String)
    {
        if (!reCheckAuth()) {
            return
        }

        val getDestination = rc.getRCDestination()

        if(getDestination == 0) {
            CallBackListener?.onDeenSDKRCFailed()
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",getDestination)
        baseContext?.startActivity(intent)

    }

    @JvmStatic
    private fun openTasbeeh()
    {

        if (!reCheckAuth()) {
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
    private fun openPrayerTime()
    {

        if (!reCheckAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivity::class.java)
        intent.putExtra("destination",R.id.action_blankFragment_to_prayerTimesFragment)
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
    fun prayerNotification(isEnabled: Boolean,context: Context,callback: DeenSDKCallback? = null)
    {

        this.isTodayNotificationSet = false
        this.baseContext = context
        this.appContext = context.applicationContext
        this.CallBackListener = callback

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


                        val getPrayerTime = async { prayerTimesRepository.getPrayerTimeSDK("Dhaka", language, prayerDate) }.await()
                        val getPrayerTimeNextDay = async { prayerTimesRepository.getPrayerTimeSDK("Dhaka", language, prayerDate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1)) }.await()


                when (getPrayerTime) {
                            is ApiResource.Failure -> {
                                withContext(Dispatchers.Main)
                                {
                                    CallBackListener?.DeenPrayerNotificationFailed()
                                }

                                return@launch
                            }

                            is ApiResource.Success -> {

                                getPrayerTime.value?.let {

                                    val isha =
                                        "$prayerDate ${it.Data.Isha}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

                                    val currentTime =
                                        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()).StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

                                    if(currentTime>isha)
                                        return@launch
                                    else
                                    setupPrayerNotification(prayerTimesRepository,it, prayerDate)
                                }?: run {
                                    withContext(Dispatchers.Main)
                                    {
                                        CallBackListener?.DeenPrayerNotificationFailed()
                                    }
                                    return@launch
                                }
                            }
                        }

                when (getPrayerTimeNextDay) {
                    is ApiResource.Failure -> {
                        withContext(Dispatchers.Main)
                        {
                            CallBackListener?.DeenPrayerNotificationFailed()
                        }

                        return@launch
                    }

                    is ApiResource.Success -> {

                        getPrayerTimeNextDay.value?.let {
                                setupPrayerNotification(prayerTimesRepository,it, nextPrayerDate = prayerDate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1))
                        }?: run {
                            withContext(Dispatchers.Main)
                            {
                                CallBackListener?.DeenPrayerNotificationFailed()
                            }
                            return@launch
                        }
                    }
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

                if(prayerTimesRepository.clearPrayerNotification()>0)
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

    private suspend fun setupPrayerNotification(
        prayerTimesRepository: PrayerTimesRepository,
        prayerTimesResponse: PrayerTimesResponse,
        nextPrayerDate:String
    )
    {
        prayerTimesResponse.let {

            var prayerNotifyCount = 0

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt1",
                    3,
                    "",
                    it
                ) == 1
            )
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt3",
                    3,
                    "",
                    it
                ) == 1
            )
                prayerNotifyCount++
            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt4",
                    3,
                    "",
                    it
                ) == 1)
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt5",
                    3,
                    "",
                    it
                ) == 1
            )
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt6",
                    3,
                    "",
                    it
                ) == 1
            )
                prayerNotifyCount++

            Log.e("DEEN_NOTIFY",prayerNotifyCount.toString())

            if(!isTodayNotificationSet) {
                if (prayerNotifyCount > 0) {
                    isTodayNotificationSet = true
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationOn()
                    }
                } else {
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationFailed()
                    }
                }
            }

        }
    }

    @JvmStatic
    suspend fun isPrayerNotificationEnabled(context: Context):Boolean =
        withContext(Dispatchers.IO)
        {
            baseContext = context
            appContext = context.applicationContext

            val prayerTimesRepository = PrayerTimesRepository(
                deenService = NetworkProvider().getInstance().provideDeenService(),
                prayerNotificationDao = DatabaseProvider().getInstance()
                    .providePrayerNotificationDao(),
                prayerTimesDao = null
            )

            return@withContext prayerTimesRepository.getTotalActiveNotification() > 0

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
    fun onDeenSDKOperationSuccess()
    fun onDeenSDKOperationFailed()
    fun onDeenSDKRCFailed()
    fun DeenPrayerNotificationOn()
    fun DeenPrayerNotificationOff()
    fun DeenPrayerNotificationFailed()
}