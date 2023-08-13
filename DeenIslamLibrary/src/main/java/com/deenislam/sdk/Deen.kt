package com.deenislam.sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.Keep
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.repository.AuthenticateRepository
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import com.deenislam.sdk.views.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var msisdn:String = ""

    @JvmStatic
    fun openDeen(context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn

        CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            /*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*/

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

    @JvmStatic
    fun openTasbeeh(context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn

        CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            /*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*/

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

        }
    }

    @JvmStatic
    fun openPrayerTime(context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn

        CoroutineScope(Dispatchers.IO).launch {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

            /*.apply {

                if (!this.isNullOrEmpty())
                {
                    language = SettingRepository(
                        userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
                    ).getSetting()?.language?:"bn"

                    Log.e("language1", language)
                }
            }*/

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

        }
    }

    @JvmStatic
    fun prayerNotification(isEnabled: Boolean,context: Context, msisdn:String, callback: DeenCallback? = null)
    {
        this.appContext = context.applicationContext
        this.CallBackListener = callback
        this.msisdn = msisdn
        val prayerDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
            Date()
        )


        CoroutineScope(Dispatchers.IO).launch {

            if(isEnabled)
            {

            token = AuthenticateRepository(
                authenticateService = NetworkProvider().getInstance().provideAuthService(),
                userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            ).authDeen(msisdn)

                if (token != null && token?.isNotEmpty() == true) {


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
                                    callback?.prayerNotificationFailed()
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
                                    callback?.prayerNotificationOn()
                                }
                            }
                            else {
                                withContext(Dispatchers.Main)
                                {
                                    callback?.prayerNotificationFailed()
                                }
                            }


                        } ?:
                        withContext(Dispatchers.Main)
                        {
                            callback?.prayerNotificationFailed()
                        }


                    }

                else {
                    withContext(Dispatchers.Main)
                    {
                        callback?.onAuthFailed()
                    }

                }

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
                        callback?.prayerNotificationOff()
                    }
                else
                    withContext(Dispatchers.Main)
                    {
                        callback?.prayerNotificationFailed()
                    }


            }

        }
    }

    @JvmStatic
    fun destroySDK() {
        CallBackListener = null
        appContext = null
    }


}



interface DeenCallback
{
    fun onAuthSuccess()
    fun onAuthFailed()
    fun prayerNotificationOn()
    fun prayerNotificationOff()
    fun prayerNotificationFailed()
}