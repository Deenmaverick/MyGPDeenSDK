package com.deenislam.sdk.service.di;

import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.BASE_AUTH_API_URL
import com.deenislam.sdk.utils.BASE_DEEN_SERVICE_API_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class NetworkProvider {

    private var authInterceptor:AuthInterceptor ? =null
    private var okHttpClient:OkHttpClient ? =null
    private var deenService:DeenService ? = null
    private var authService:AuthenticateService ? = null

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
        isAuthService:Boolean = false
    )
    {
        if(instance?.authInterceptor==null)
            instance?.authInterceptor = AuthInterceptor("dfasfd")

        instance?.authInterceptor?.apply {

            if(instance?.okHttpClient == null) {

                /*val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY*/
                instance?.okHttpClient =
                    OkHttpClient.Builder()
                        //.addInterceptor(loggingInterceptor)
                        .addInterceptor(this)
                        .build()
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
                deenService = buildAPI(
                    api = DeenService::class.java,
                    baseUrl = BASE_DEEN_SERVICE_API_URL,
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

    fun provideAuthService(): AuthenticateService? {
        initInstance(isAuthService = true)
        return instance?.authService
    }

}
