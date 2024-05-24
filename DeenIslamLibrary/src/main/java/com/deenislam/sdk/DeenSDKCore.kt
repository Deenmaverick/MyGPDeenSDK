package com.deenislam.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import com.deenislam.sdk.service.database.AppPreference
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
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.views.main.MainActivityDeenSDK
import com.deenislam.sdk.views.prayertimes.PrayerTimeNotification
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
@Keep
object DeenSDKCore {

    @JvmStatic
    private var  DeenCallBackListener : DeenSDKCallback? =null

    @JvmStatic
    private var  DeenPrayerTimeCallBackListener : PrayerTimeNotification? =null

    @JvmStatic
    var appContext: Context? = null

    @JvmStatic
    var baseContext: Context? = null

    @JvmStatic
    private var token: String = ""

    @JvmStatic
    private var deen_language = "bn"

    @JvmStatic
    private var msisdn:String = ""

    private var  prayerDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(
        Date()
    )

    private var isTodayNotificationSet = false


    @JvmStatic
    fun initDeen(context: Context, token:String, callback: DeenSDKCallback)
    {

        this.baseContext = context
        this.appContext = context.applicationContext
        this.DeenCallBackListener = callback
        this.token = token

        AppPreference.init(context)

        try {
            val decoded = JWTdecode(token)
           msisdn = decoded.payload.name
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO).launch {

            withContext(Dispatchers.Main)
            {
                checkAuth()
            }



            AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).initSDK(token, msisdn)

           /* if(!AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).initSDK(token, msisdn))
            {
                baseContext = null
                appContext = null
                //CallBackListener = callback
            }*/

        }

    }

    fun GetDeenLanguage() = deen_language
   /* private fun SetDeenLanguage(language:String)
    {
        deen_language = if(language == "en" || language == "bn")
            language
        else
            "bn"
    }*/

    fun GetDeenToken() = token
    fun SetDeenToken(token: String)
    {
        this.token = token
    }

    fun GetDeenMsisdn() = msisdn
    fun GetDeenPrayerDate() = prayerDate

    fun GetDeenCallbackListner() = DeenCallBackListener

    fun SetPrayerTimeCallback(callback:PrayerTimeNotification) {
        DeenPrayerTimeCallBackListener=callback
    }

    private fun checkAuth(): Boolean {
        if(appContext == null || baseContext == null || token.isEmpty() || msisdn.isEmpty()) {
            DeenCallBackListener?.onDeenSDKInitFailed()
            return false
        }
        else
            DeenCallBackListener?.onDeenSDKInitSuccess()

        return true
    }

    private fun reCheckAuth(): Boolean {
        if(appContext == null || baseContext == null || token.isEmpty() || msisdn.isEmpty()) {
            DeenCallBackListener?.onDeenSDKOperationFailed()
            return false
        }
        else
            DeenCallBackListener?.onDeenSDKOperationSuccess()

        return true
    }

    fun authSDK(context: Context, getmsisdn:String, callback: DeenSDKCallback? = null) {

        this.baseContext = context
        this.appContext = context.applicationContext
        this.DeenCallBackListener = callback

        AppPreference.init(context)

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
            Intent(baseContext, MainActivityDeenSDK::class.java)
        intent.putExtra("destination",R.id.dashboardFakeFragment)
        baseContext?.startActivity(intent)

    }

    @JvmStatic
    fun openFromRC(rc:String)
    {
        if (!reCheckAuth()) {
            return
        }

        when(rc){
            "live_ijtema","khatam_e_quran_ramadan","ramadan" -> {

                val intent =
                    Intent(baseContext, MainActivityDeenSDK::class.java)
                intent.putExtra("destination",R.id.dashboardFakeFragment)
                intent.putExtra("rc",rc)
                baseContext?.startActivity(intent)
            }

            else->{
                val getDestination = rc.getRCDestination()

                if(getDestination == 0) {
                    DeenCallBackListener?.onDeenSDKRCFailed()
                    return
                }

                val intent =
                    Intent(baseContext, MainActivityDeenSDK::class.java)
                intent.putExtra("destination",getDestination)
                baseContext?.startActivity(intent)
            }
        }

    }

    @JvmStatic
    private fun openTasbeeh()
    {

        if (!reCheckAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivityDeenSDK::class.java)
        intent.putExtra("destination",R.id.tasbeehFragment)
        baseContext?.startActivity(intent)

    }

    @JvmStatic
    private fun openPrayerTime()
    {

        if (!reCheckAuth()) {
            return
        }

        val intent =
            Intent(baseContext, MainActivityDeenSDK::class.java)
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
    fun prayerNotification(isEnabled: Boolean, context: Context,callback: DeenSDKCallback)
    {
        internalPrayerNotification(
            isEnabled = isEnabled,
            isClearData = false,
            context = context,
            callback = callback
        )
    }

    @JvmStatic
    fun clearAllPrayerNotification(context: Context,callback: DeenSDKCallback)
    {
        internalPrayerNotification(
            isEnabled = false,
            isClearData = true,
            context = context,
            callback = callback
        )
    }

    @JvmStatic
    private fun internalPrayerNotification(isEnabled: Boolean,isClearData:Boolean = false, context: Context,callback: DeenSDKCallback)
    {

        this.isTodayNotificationSet = false
        this.baseContext = context
        this.appContext = context.applicationContext
        this.DeenCallBackListener = callback

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


                        val getPrayerTime = async { prayerTimesRepository.getPrayerTimeSDK("Dhaka", deen_language, prayerDate) }.await()
                        val getPrayerTimeNextDay = async { prayerTimesRepository.getPrayerTimeSDK("Dhaka", deen_language, prayerDate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1)) }.await()


                when (getPrayerTime) {
                            is ApiResource.Failure -> {
                                withContext(Dispatchers.Main)
                                {
                                    DeenCallBackListener?.DeenPrayerNotificationFailed()
                                }

                                return@launch
                            }

                            is ApiResource.Success -> {

                                getPrayerTime.value?.let {

                                    val isha =
                                        "$prayerDate ${it.Data.Isha}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

                                    val currentTime =
                                        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Date()).StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

                                   if(currentTime<isha)
                                    setupPrayerNotification(prayerTimesRepository,it, prayerDate)
                                }?: run {
                                    withContext(Dispatchers.Main)
                                    {
                                        DeenCallBackListener?.DeenPrayerNotificationFailed()
                                    }
                                    return@launch
                                }
                            }
                        }

                when (getPrayerTimeNextDay) {
                    is ApiResource.Failure -> {
                        withContext(Dispatchers.Main)
                        {
                            DeenCallBackListener?.DeenPrayerNotificationFailed()
                        }

                        return@launch
                    }

                    is ApiResource.Success -> {

                        getPrayerTimeNextDay.value?.let {
                                setupPrayerNotification(prayerTimesRepository,it, nextPrayerDate = prayerDate.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1))
                        }?: run {
                            withContext(Dispatchers.Main)
                            {
                                DeenCallBackListener?.DeenPrayerNotificationFailed()
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


                if(prayerTimesRepository.updatePrayerNotification(
                        "",
                        "Notification",
                        0,
                        "",
                        null
                    ) == 1
                )
                {
                    if(isClearData)

                    {
                        Log.e("isClearData", prayerTimesRepository.clearPrayerNotification().toString())
                    }

                    withContext(Dispatchers.Main)
                    {
                        DeenCallBackListener?.DeenPrayerNotificationOff()
                    }
                }
                else
                {
                    withContext(Dispatchers.Main)
                    {
                        DeenCallBackListener?.DeenPrayerNotificationFailed()
                    }
                }

               /* if(prayerTimesRepository.clearPrayerNotification()>0)
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationOff()
                    }
                else
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationFailed()
                    }*/

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
                    it,
                    isFromInsideSDK = false
                ) == 1
            )
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt3",
                    3,
                    "",
                    it,
                    isFromInsideSDK = false
                ) == 1
            )
                prayerNotifyCount++
            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt4",
                    3,
                    "",
                    it,
                    isFromInsideSDK = false
                ) == 1)
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt5",
                    3,
                    "",
                    it,
                    isFromInsideSDK = false
                ) == 1
            )
                prayerNotifyCount++

            if (prayerTimesRepository.updatePrayerNotification(
                    nextPrayerDate,
                    "pt6",
                    3,
                    "",
                    it,
                    isFromInsideSDK = false
                ) == 1
            )
                prayerNotifyCount++

            Log.e("DEEN_NOTIFY",prayerNotifyCount.toString())

            if(!isTodayNotificationSet) {

                /*if (prayerNotifyCount > 0) {
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationOn()
                    }
                    isTodayNotificationSet = true
                } else {
                    withContext(Dispatchers.Main)
                    {
                        CallBackListener?.DeenPrayerNotificationFailed()
                    }
                }*/

                // new method to check notification state

                if(prayerTimesRepository.updatePrayerNotification(
                        "",
                        "Notification",
                        1,
                        "",
                        null
                    ) == 1
                )
                {
                    withContext(Dispatchers.Main)
                    {
                        DeenCallBackListener?.DeenPrayerNotificationOn()
                        DeenPrayerTimeCallBackListener?.isSDKCoreNotificationEnable()
                    }
                    isTodayNotificationSet = true
                }
                else {
                    withContext(Dispatchers.Main)
                    {
                        DeenCallBackListener?.DeenPrayerNotificationFailed()
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

            //return@withContext prayerTimesRepository.getTotalActiveNotification() > 0


            val getStatus = prayerTimesRepository.getNotificationData(
                date = "",
                prayer_tag = "Notification",
                finalState = 0
            )

            Log.e("prayerTimesRepository",Gson().toJson(getStatus))

            getStatus?.state == 1

        }


    @JvmStatic
    fun destroySDK() {
        DeenCallBackListener = null
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

    @JvmStatic
    fun setSehriAlaram(time: String){
        initSetRamadanAlarm(time,1,"Sehri")
    }

    @JvmStatic
    fun setIftarAlarm(time: String){
        initSetRamadanAlarm(time,0,"Iftar")
    }

    private fun initSetRamadanAlarm(time:String,alarmType:Int,title:String){

        tryCatch {
            val sdf =
                SimpleDateFormat(
                    "HH:mm",
                    Locale.ENGLISH
                ) // or "hh:mm" for 12 hour format

            val date = sdf.parse(time)

            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val min = calendar.get(Calendar.MINUTE)

                val i = Intent(AlarmClock.ACTION_SET_ALARM)

                i.putExtra(AlarmClock.EXTRA_MESSAGE, title)

                if (alarmType == 0) {
                    i.putExtra(AlarmClock.EXTRA_HOUR, 12 + hour)
                } else {
                    i.putExtra(AlarmClock.EXTRA_HOUR, hour)
                }

                i.putExtra(AlarmClock.EXTRA_MINUTES, min)
                baseContext?.startActivity(i)
            }

        }
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