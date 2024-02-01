package com.deenislam.sdk.service.di;

import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.HadithService
import com.deenislam.sdk.service.network.api.IslamicNameService
import com.deenislam.sdk.service.network.api.QuranService
import com.deenislam.sdk.service.network.api.YoutubeService
import com.deenislam.sdk.utils.BASE_AUTH_API_URL
import com.deenislam.sdk.utils.BASE_DEEN_SERVICE_API_URL
import com.deenislam.sdk.utils.BASE_QURAN_API_URL
import com.deenislam.sdk.utils.BASE_YOUTUBE_VIDEO_API
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
    private var islamicNameService:IslamicNameService ? = null
    private var youtubeService:YoutubeService ? = null

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
        isHadithServiceTest: Boolean = false,
        isIslamicNameService: Boolean = false,
        isYoutubeService: Boolean = false,
    )
    {
        if(instance?.authInterceptor==null) {

            val userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            var acceessToken = DeenSDKCore.GetDeenToken()

                val userData = userPrefDao?.select()

           /* userData?.let {

                Log.e("AUTH_DATA_1", Gson().toJson(it))

                acceessToken = if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else
                    Deen.token.toString()
            }?.apply { acceessToken = Deen.token.toString() }*/

           // if(acceessToken.isNotEmpty()) {
                instance?.authInterceptor = AuthInterceptor().apply {

                    if (instance?.okHttpClient == null) {
                        /*val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY*/
                        instance?.okHttpClient =
                            OkHttpClient.Builder()
                                //.addInterceptor(loggingInterceptor)
                                .addInterceptor(this)
                                .build()
                    }
                //}
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
                    baseUrl = BASE_DEEN_SERVICE_API_URL,
                    okHttpClient = it
                )
            }
        }


        if(instance?.islamicNameService == null && isIslamicNameService) {
            instance?.okHttpClient?.let {
                instance?.islamicNameService = buildAPI(
                    api = IslamicNameService::class.java,
                    baseUrl = BASE_DEEN_SERVICE_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.youtubeService == null && isYoutubeService) {
            instance?.okHttpClient?.let {
                instance?.youtubeService = buildAPI(
                    api = YoutubeService::class.java,
                    baseUrl = BASE_YOUTUBE_VIDEO_API,
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

    fun provideIslamicNameService(): IslamicNameService? {
        initInstance(isIslamicNameService = true)
        return instance?.islamicNameService
    }

    fun provideYoutubeService(): YoutubeService? {
        initInstance(isYoutubeService = true)
        return instance?.youtubeService
    }

    fun provideAuthInterceptor(): AuthInterceptor? {
        initInstance()
        return instance?.authInterceptor
    }

}
