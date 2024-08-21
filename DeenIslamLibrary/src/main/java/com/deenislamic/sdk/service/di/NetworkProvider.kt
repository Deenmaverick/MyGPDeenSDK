package com.deenislamic.sdk.service.di;

import com.deenislamic.sdk.service.network.AuthInterceptor
import com.deenislamic.sdk.service.network.api.AuthenticateService
import com.deenislamic.sdk.service.network.api.DashboardService
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.service.network.api.HadithService
import com.deenislamic.sdk.service.network.api.IslamicNameService
import com.deenislamic.sdk.service.network.api.NagadPaymentService
import com.deenislamic.sdk.service.network.api.PaymentService
import com.deenislamic.sdk.service.network.api.QuranService
import com.deenislamic.sdk.service.network.api.QuranShikkhaService
import com.deenislamic.sdk.service.network.api.YoutubeService
import com.deenislamic.sdk.utils.BASE_AUTH_API_URL
import com.deenislamic.sdk.utils.BASE_DCB_PAYMENT_API_URL
import com.deenislamic.sdk.utils.BASE_DEEN_SERVICE_API_URL
import com.deenislamic.sdk.utils.BASE_PAYMENT_API_URL
import com.deenislamic.sdk.utils.BASE_QURAN_API_URL
import com.deenislamic.sdk.utils.BASE_QURAN_SHIKKHA_API_URL
import com.deenislamic.sdk.utils.BASE_YOUTUBE_VIDEO_API
import com.deenislamic.sdk.utils.NAGAD_BASE_PAYMENT_API_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class NetworkProvider {

    private var authInterceptor:AuthInterceptor ? =null
    private var okHttpClient:OkHttpClient ? =null
    private var okHttpClientNoAuth:OkHttpClient ? =null
    private var deenService:DeenService ? = null
    private var quranService:QuranService ? = null
    private var authService:AuthenticateService ? = null
    private var hadithService:HadithService ? = null
    private var islamicNameService:IslamicNameService ? = null
    private var youtubeService:YoutubeService ? = null
    private var dashboardService:DashboardService ? = null
    private var quranShikkhaService:QuranShikkhaService ? = null
    private var paymentService:PaymentService ? = null
    private var paymentDCBService:PaymentService ? = null
    private var nagadPaymentService:NagadPaymentService ? = null

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
        isDashboardService: Boolean = false,
        isQuranShikkhaService: Boolean = false,
        isPaymentService: Boolean = false,
        isNagadPaymentService: Boolean = false,
        isDCBPaymentService: Boolean = false
    )
    {
        if(instance?.authInterceptor==null) {

          /*  val userPrefDao = DatabaseProvider().getInstance().provideUserPrefDao()
            var acceessToken = DeenSDKCore.GetDeenToken()

                val userData = userPrefDao?.select()
*/
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

        if(instance?.okHttpClientNoAuth == null){
            instance?.okHttpClientNoAuth =
                OkHttpClient.Builder()
                    //.addInterceptor(loggingInterceptor)
                    .build()
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
            instance?.okHttpClientNoAuth?.let {
                instance?.youtubeService = buildAPI(
                    api = YoutubeService::class.java,
                    baseUrl = BASE_YOUTUBE_VIDEO_API,
                    okHttpClient = it
                )
            }
        }

        if(instance?.dashboardService == null && isDashboardService) {
            instance?.okHttpClient?.let {
                instance?.dashboardService = buildAPI(
                    api = DashboardService::class.java,
                    baseUrl = BASE_AUTH_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.quranShikkhaService == null && isQuranShikkhaService) {
            instance?.okHttpClient?.let {
                instance?.quranShikkhaService = buildAPI(
                    api = QuranShikkhaService::class.java,
                    baseUrl = BASE_QURAN_SHIKKHA_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.paymentService == null && isPaymentService) {
            instance?.okHttpClient?.let {
                instance?.paymentService = buildAPI(
                    api = PaymentService::class.java,
                    baseUrl = BASE_PAYMENT_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.paymentDCBService == null && isDCBPaymentService) {
            instance?.okHttpClient?.let {
                instance?.paymentDCBService = buildAPI(
                    api = PaymentService::class.java,
                    baseUrl = BASE_DCB_PAYMENT_API_URL,
                    okHttpClient = it
                )
            }
        }

        if(instance?.nagadPaymentService == null && isNagadPaymentService) {
            instance?.okHttpClient?.let {
                instance?.nagadPaymentService = buildAPI(
                    api = NagadPaymentService::class.java,
                    baseUrl = NAGAD_BASE_PAYMENT_API_URL,
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

    fun provideDashboardService(): DashboardService? {
        initInstance(isDashboardService = true)
        return instance?.dashboardService
    }

    fun provideQuranShikkhaService(): QuranShikkhaService? {
        initInstance(isQuranShikkhaService = true)
        return instance?.quranShikkhaService
    }

    fun providePaymentService(): PaymentService? {
        initInstance(isPaymentService = true)
        return instance?.paymentService
    }

    fun provideDCBPaymentService(): PaymentService? {
        initInstance(isDCBPaymentService = true)
        return instance?.paymentDCBService
    }

    fun provideNagadPaymentService(): NagadPaymentService? {
        initInstance(isNagadPaymentService = true)
        return instance?.nagadPaymentService
    }
}
