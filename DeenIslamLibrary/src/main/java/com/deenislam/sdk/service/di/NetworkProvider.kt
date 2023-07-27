package com.deenislam.sdk.service.di;

import android.util.Log
import com.deenislam.sdk.Deen
import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.HadithService
import com.deenislam.sdk.service.network.api.HadithServiceTest
import com.deenislam.sdk.service.network.api.QuranService
import com.deenislam.sdk.utils.BASE_AUTH_API_URL
import com.deenislam.sdk.utils.BASE_DEEN_SERVICE_API_URL
import com.deenislam.sdk.utils.BASE_HADITH_API_TEST_URL
import com.deenislam.sdk.utils.BASE_HADITH_API_URL
import com.deenislam.sdk.utils.BASE_QURAN_API_URL
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class NetworkProvider {

    private var authInterceptor:AuthInterceptor ? =null
    private var okHttpClient:OkHttpClient ? =null
    private var deenService:DeenService ? = null
    private var quranService:QuranService ? = null
    private var authService:AuthenticateService ? = null
    private var hadithService:HadithService ? = null
    private var hadithServiceTest:HadithServiceTest ? = null

    companion object {
        var instance: NetworkProvider? = null
    }

    fun getInstance(): NetworkProvider {
        if (instance == null)
            instance = NetworkProvider()

        return instance as NetworkProvider
    }

    private fun initInstance(
        isDeenService:Boolean = false,
        isAuthService:Boolean = false,
        isQuranService:Boolean = false,
        isHadithService: Boolean = false,
        isHadithServiceTest: Boolean = false
    )
    {
        if(instance?.authInterceptor==null) {

            val userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            var acceessToken = ""

                val userData = userPrefDao?.select()

            userData?.let {

                Log.e("AUTH_DATA_1", Gson().toJson(it))

                acceessToken = if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else
                    Deen.token.toString()
            }?.apply { acceessToken = Deen.token.toString() }

            if(acceessToken.isNotEmpty()) {
                instance?.authInterceptor = AuthInterceptor(userPrefDao).apply {

                    if (instance?.okHttpClient == null) {
                        /*val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY*/
                        instance?.okHttpClient =
                            OkHttpClient.Builder()
                                //.addInterceptor(loggingInterceptor)
                                .addInterceptor(this)
                                .build()
                    }
                }
            }

        }


        if(instance?.deenService == null && isDeenService) {
            instance?.okHttpClient?.let {
                instance?.deenService = buildAPI(
                    api = DeenService::class.java,
                    baseUrl = BASE_DEEN_SERVICE_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.authService == null && isAuthService) {
            instance?.okHttpClient?.let {
                instance?.authService = buildAPI(
                    api = AuthenticateService::class.java,
                    baseUrl = BASE_AUTH_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.deenService == null && isDeenService) {
            instance?.okHttpClient?.let {
                instance?.deenService = buildAPI(
                    api = DeenService::class.java,
                    baseUrl = BASE_DEEN_SERVICE_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.quranService == null && isQuranService) {
            instance?.okHttpClient?.let {
                instance?.quranService = buildAPI(
                    api = QuranService::class.java,
                    baseUrl = BASE_QURAN_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.hadithService == null && isHadithService) {
            instance?.okHttpClient?.let {
                instance?.hadithService = buildAPI(
                    api = HadithService::class.java,
                    baseUrl = BASE_HADITH_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.hadithServiceTest == null && isHadithServiceTest) {
            instance?.okHttpClient?.let {
                instance?.hadithServiceTest = buildAPI(
                    api = HadithServiceTest::class.java,
                    baseUrl = BASE_HADITH_API_TEST_URL,
                    okHttpClient = it
                )
            }
        }

    }

    private fun<Api> buildAPI(
        api:Class<Api>,
        baseUrl:String,
        okHttpClient: OkHttpClient
    ) : Api
    {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)

    }


    fun provideDeenService(): DeenService? {
        initInstance(isDeenService = true)
        return instance?.deenService
    }

    fun provideQuranService(): QuranService? {
        initInstance(isQuranService = true)
        return instance?.quranService
    }

    fun provideAuthService(): AuthenticateService? {
        initInstance(isAuthService = true)
        return instance?.authService
    }

    fun provideHadithService(): HadithService? {
        initInstance(isHadithService = true)
        return instance?.hadithService
    }

    fun provideHadithServiceTest(): HadithServiceTest? {
        initInstance(isHadithServiceTest = true)
        return instance?.hadithServiceTest
    }

}
